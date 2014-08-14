/*
 * Copyright (c) 2014 solutions direkt Gesellschaft für Lösungsentwicklung mbH
 *
 * Eigentum der solutions direkt GmbH.
 * Alle Rechte vorbehalten.
 *
 * Property of solutions direkt GmbH.
 * All rights reserved.
 *
 * solutions direkt Gesellschaft für Lösungsentwicklung mbH
 * Griegstraße 75, Haus 26
 * 22763 Hamburg
 * Deutschland / Germany
 *
 * Phone: +49 40 88155-0
 * Fax: +49 40 88155-5400
 * Email: info@direkt-gruppe.de
 * Web: http://www.direkt-gruppe.de
 */
package org.nexuse2e.backend.pipelets;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;


/**
 * @author jjerke
 *         <p/>
 *         This pipelet takes a csv list and duplicates a single column, then submits the new column to a different pipeline.
 */
public class ListColumnDuplicatorPipelet extends AbstractOutboundBackendPipelet {

    @SuppressWarnings("unused")
    private static Logger LOG = Logger.getLogger(ListColumnDuplicatorPipelet.class);

    private static final String COLUMN_PARAMETER_NAME          = "columnParameter";
    private static final String CHOREOGRAPHY_ID_PARAMETER_NAME = "choreographyId";
    private static final String PARTNER_ID_PARAMETER_NAME      = "partnerId";
    private static final String ACTION_ID_PARAMETER_NAME       = "actionId";

    private String columnParameter = "1";
    private String choreographyId  = null;
    private String partnerId       = null;
    private String actionId        = null;

    /**
     * Default constructor.
     */
    public ListColumnDuplicatorPipelet() {
        parameterMap.put(COLUMN_PARAMETER_NAME, new ParameterDescriptor(ParameterType.STRING, "Column",
                                                                        "The column to be copied into the new pipeline. Defaults to 1, "
                                                                        + "which is the first column.",
                                                                        ""));
        parameterMap.put(CHOREOGRAPHY_ID_PARAMETER_NAME,
                         new ParameterDescriptor(ParameterType.STRING, "Choreography ID", "The choreography to which the duplicated column will be submitted.",
                                                 ""));
        parameterMap.put(PARTNER_ID_PARAMETER_NAME,
                         new ParameterDescriptor(ParameterType.STRING, "Partner ID", "The partner to which the duplicated column will be submitted.", ""));
        parameterMap.put(ACTION_ID_PARAMETER_NAME,
                         new ParameterDescriptor(ParameterType.STRING, "Action ID", "The action to which the duplicated column will be submitted.", ""));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPayloadAvailable(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processPayloadAvailable(MessageContext messageContext) throws NexusException {

        columnParameter = getParameter(COLUMN_PARAMETER_NAME);
        choreographyId = getParameter(CHOREOGRAPHY_ID_PARAMETER_NAME);
        partnerId = getParameter(PARTNER_ID_PARAMETER_NAME);
        actionId = getParameter(ACTION_ID_PARAMETER_NAME);

        if (StringUtils.isBlank(choreographyId) || StringUtils.isBlank(partnerId) || StringUtils.isBlank(actionId)) {
            LOG.warn(new LogMessage("Parameter for ListColumnDuplicatorPipelet missing", messageContext));
            return messageContext;
        }

        // Iterate all payloads and set mime-type to the given value
        if (null != messageContext.getMessagePojo() && null != messageContext.getMessagePojo().getMessagePayloads()) {
            for (int i = 0; i < messageContext.getMessagePojo().getMessagePayloads().size(); i++) {
                MessagePayloadPojo onePayload = messageContext.getMessagePojo().getMessagePayloads().get(i);
                if (StringUtils.equals("text/plain", onePayload.getMimeType())) {
                    ByteArrayOutputStream emails = new ByteArrayOutputStream();

                    try (InputStream stream = new ByteArrayInputStream(onePayload.getPayloadData());
                        BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                        String oneLine;
                        while ((oneLine = br.readLine()) != null) {
                            String[] cells = oneLine.split(";");
                            Integer targetCell = Integer.valueOf(columnParameter) - 1;
                            if (targetCell < cells.length && StringUtils.isNotBlank(cells[targetCell])) {
                                emails.write(cells[targetCell].getBytes());
                                emails.write("\r\n".getBytes());
                            }
                        }
                    } catch (IOException e) {
                        LOG.error(new LogMessage(e.getMessage(), messageContext));
                    }

                    if (0 < emails.size()) {
                        // Got mail addresses, export them to new message.
                        String txId = Engine.getInstance().getIdGenerator(Constants.ID_GENERATOR_MESSAGE).getId();
                        Timestamp txTs = new Timestamp(System.currentTimeMillis());
                        generateMessage(emails.toByteArray(), txId, txTs, messageContext.getMessagePojo().getMessageId(), i);
                    }
                } else {
                    LOG.warn(new LogMessage("Payload was not of type text/csv, not processing.", messageContext));
                }
            }
        }

        return messageContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPrimaryKeyAvailable(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processPrimaryKeyAvailable(MessageContext messageContext) throws NexusException {

        return messageContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.unmodifiableMap(parameterMap);
    }

    protected void generateMessage(byte[] data, String txId, Timestamp txTs, String originalMessageId, int messagePayloadIndex) throws NexusException {
        LOG.debug("Generating message");
        MessagePayloadPojo payload = new MessagePayloadPojo();
        payload.setSequenceNumber(1);
        payload.setContentId("emails_" + txTs.toString() + ".csv");
        payload.setPayloadData(data);
        payload.setCreatedDate(txTs);
        payload.setMimeType("text/csv");

        Engine.getInstance().getCurrentConfiguration().getBackendPipelineDispatcher()
            .processMessage(partnerId, choreographyId, actionId, originalMessageId + "_mails", txId, null, null, Collections.singletonList(payload), null);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Generated CSV data: \r\n" + new String(data));
        }
    }

}
