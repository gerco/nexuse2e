package org.nexuse2e.frontend.pipelets;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EnumerationParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.TimestampFormatter;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.util.ServerPropertiesUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;


/**
 * @author Guido Esch
 */
public class AdvancedFileSavePipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger(AdvancedFileSavePipelet.class);

    public static final String DIRECTORY_PARAM_NAME         = "directory";
    public static final String FILE_NAME_PATTERN_PARAM_NAME = "fileNamePattern";
    public static final String DEFAULT_TEMPLATE_PARAM_NAME  = "defaultTemplate";
    public static final String USE_CONTENT_ID_PARAM_NAME    = "useContentId";
    public static final String STOP_ON_ERROR_PARAM_NAME     = "stopOnError";
    public static final String TEMPLATES_MAP_PARAM_NAME     = "templatesMap";

    private TimestampFormatter formatter;

    public AdvancedFileSavePipelet() {
        try {
            formatter = Engine.getInstance().getTimestampFormatter("ebxml");
        } catch (NexusException e) {
            LOG.trace("no timestamp formatter found for ebxml");
        }
        setFrontendPipelet(true);

        parameterMap.put(DIRECTORY_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Save directory", "Path to directory where to store files", ""));
        parameterMap.put(DEFAULT_TEMPLATE_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "the default template",
                                                                              "the default output template use for writing files. Also the fallback if "
                                                                              + "nothing else matches", ""));
        parameterMap
            .put(FILE_NAME_PATTERN_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "File Name", "File Name Pattern", "${nexus.message.message}"));
        parameterMap.put(USE_CONTENT_ID_PARAM_NAME,
                         new ParameterDescriptor(ParameterType.BOOLEAN, "Use Content ID", "Flag whether to use the content ID as the file name",
                                                 Boolean.FALSE));
        parameterMap.put(STOP_ON_ERROR_PARAM_NAME,
                         new ParameterDescriptor(ParameterType.BOOLEAN, "Stop on Errror", "Message processing fails in case off issues while writing the file.",
                                                 Boolean.FALSE));
        parameterMap.put(TEMPLATES_MAP_PARAM_NAME, new ParameterDescriptor(ParameterType.ENUMERATION, "",
                                                                           "Map of templates an nodes: node=\"ProductMovementReport\", "
                                                                           + "value=\"c:/ProductMovementReport.xslt\"", new EnumerationParameter()));

    }

    @Override
    public MessageContext processMessage(MessageContext messageContext) throws NexusException /* ,IllegalArgumentException, IllegalStateException  */ {

        try {
            if(messageContext.getMessagePojo().getMessagePayloads() == null || messageContext.getMessagePojo().getMessagePayloads().size() == 0) {
                String templatePath = getParameter(DEFAULT_TEMPLATE_PARAM_NAME);
                File template = new File(templatePath);
                if (template.exists() && template.canRead()) {
                    ByteArrayOutputStream baos = applyTemplate(messageContext, null, template);
                    try {
                        writePayload(baos, null, messageContext);
                    } catch (IOException e) {
                        LOG.error("failed to persist file in file system.", e);
                    }
                }
            } else {
                for (MessagePayloadPojo messagePayloadPojo : messageContext.getMessagePojo().getMessagePayloads()) {
                    byte[] data = messagePayloadPojo.getPayloadData();
                    String templatePath = getParameter(DEFAULT_TEMPLATE_PARAM_NAME);
                    Document document = createDocument(data);
                    if (data != null) {
                        EnumerationParameter enumeration = getParameter(TEMPLATES_MAP_PARAM_NAME);
                        for (Map.Entry<String, String> expressionTemplateEntry : enumeration.getElements().entrySet()) {
                            if (expressionTemplateEntry.getKey() != null && isDocumentMatchingExpression(document, expressionTemplateEntry.getKey())) {
                                templatePath = expressionTemplateEntry.getValue();
                            }
                        }
                    }

                    File template = new File(templatePath);
                    if (template.exists() && template.canRead()) {
                        ByteArrayOutputStream baos = applyTemplate(messageContext, document, template);
                        try {
                            writePayload(baos, messagePayloadPojo, messageContext);
                        } catch (IOException e) {
                            LOG.error("failed to persist file in file system.", e);
                        }
                    }
                }
            }
        } catch (NexusException e) {
            boolean stopOnError = getParameter(STOP_ON_ERROR_PARAM_NAME);
            if (stopOnError) {
                throw e;
            } else {
                LOG.error("failed to convert and persist message.", e);
            }
        }
        return messageContext;
    }

    private String writePayload(ByteArrayOutputStream baos, MessagePayloadPojo payload, MessageContext messageContext )
        throws IOException {

        String destinationDirectory = getParameter(DIRECTORY_PARAM_NAME);

        File destDirFile = new File( destinationDirectory );
        // StringBuffer fileName = new StringBuffer();

        if ( destDirFile.exists() && !destDirFile.isDirectory() ) {
            throw new FileNotFoundException( "Not a directory: " + destDirFile );
        }
        if(!destDirFile.exists()) {
            if(!destDirFile.mkdirs()) {
                LOG.error("failed to create output directory");
            }
        }

        String fileName = null;

        boolean useContentId = getParameter(USE_CONTENT_ID_PARAM_NAME);
        if ( useContentId && payload != null) {
            fileName = destinationDirectory + File.separatorChar + payload.getContentId();
        } else {
            String fileNamePattern = getParameter(FILE_NAME_PATTERN_PARAM_NAME);
            String baseFileName = ServerPropertiesUtil.replaceServerProperties(fileNamePattern, messageContext );
            String extension = ".xml";
            int seqNo = 0;
            if (payload != null) {
                seqNo = payload.getSequenceNumber();
            }
            fileName = destinationDirectory + File.separatorChar + baseFileName + "_" + seqNo
                       + "." + extension;
        }


        BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(fileName) );
        baos.writeTo(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();

        return fileName;
    }

    private ByteArrayOutputStream applyTemplate(MessageContext messageContext, Document document, File template) throws NexusException {
        LOG.trace("apply template");
        byte[] result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(template));
            transformer.setParameter( "messageId", messageContext.getMessagePojo().getMessageId() );
            transformer.setParameter( "conversationId", messageContext.getMessagePojo().getConversation().getConversationId() );
            String date = formatter.getTimestamp(messageContext.getMessagePojo().getCreatedDate());

            transformer.setParameter( "createdDate", date);
            transformer.setParameter( "choreographyId", messageContext.getMessagePojo().getConversation().getChoreography().getName());
            transformer.setParameter( "actionId", messageContext.getMessagePojo().getAction().getName());
            transformer.setParameter( "direction", messageContext.getMessagePojo().isOutbound()?"Outbound":"Inbound");
            transformer.setParameter( "messageType", messageContext.getMessagePojo().isAck()?"TechnicalAck":"BusinessMessage");

            if(messageContext.getMessagePojo().isOutbound()) {
                transformer.setParameter("fromId", messageContext.getMessagePojo().getParticipant().getLocalPartner().getPartnerId());
                transformer.setParameter("fromType", messageContext.getMessagePojo().getParticipant().getLocalPartner().getPartnerIdType());
                transformer.setParameter("toId", messageContext.getMessagePojo().getConversation().getPartner().getPartnerId());
                transformer.setParameter("toType", messageContext.getMessagePojo().getConversation().getPartner().getPartnerIdType());
            } else {
                transformer.setParameter("toId", messageContext.getMessagePojo().getParticipant().getLocalPartner().getPartnerId());
                transformer.setParameter("toType", messageContext.getMessagePojo().getParticipant().getLocalPartner().getPartnerIdType());
                transformer.setParameter("fromId", messageContext.getMessagePojo().getConversation().getPartner().getPartnerId());
                transformer.setParameter("fromType", messageContext.getMessagePojo().getConversation().getPartner().getPartnerIdType());
            }


            transformer.transform(new DOMSource(document), new StreamResult(baos));

            return baos;
        } catch (Exception e) {
            throw new NexusException("Error transforming template using XSLT", e);
        }

    }

    private boolean isDocumentMatchingExpression(Document document, String expression) throws NexusException {

        if (StringUtils.isBlank(expression)) {
            throw new NexusException(expression + " is not a valid xpath expression");
        }
        XPath xPath = XPathFactory.newInstance().newXPath();
        Node root = document.getFirstChild();
        if (root == null) {
            throw new NexusException("XML document does not contain a valid root element");
        }
        try {
            String result = xPath.evaluate(expression, document);
            if (StringUtils.isNotBlank(result)) {
                return true;
            }
        } catch (XPathExpressionException e) {
            // expected for some queries.
        }
        return false;
    }

    private Document createDocument(byte[] data) throws NexusException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false);
            DocumentBuilder builder = null;
            builder = documentBuilderFactory.newDocumentBuilder();
            if(data != null) {
                return builder.parse(new ByteArrayInputStream(data));
            } else {
                return builder.newDocument();
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new NexusException("failed to create document from payload", e);
        }
    }
}
