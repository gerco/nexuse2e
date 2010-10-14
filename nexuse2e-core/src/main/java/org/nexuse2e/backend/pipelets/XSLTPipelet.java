/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.backend.pipelets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration;
import org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfigurations;
import org.nexuse2e.backend.pipelets.helper.RequestResponseData;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.xml.sax.SAXException;

/**
 * @author mbreilmann
 *
 */
public class XSLTPipelet extends AbstractPipelet {

    private static Logger                 LOG                  = Logger.getLogger( XSLTPipelet.class );

    public static final String            XSLT_FILE            = "xsltFile";
    public static final String            PARTNER_SPECIFIC     = "partnerSpecific";

    private String                        xsltFileName         = null;
    private StreamSource                  xsltStreamSource     = null;
    private boolean                       partnerSpecific      = false;
    private HashMap<String, StreamSource> partnerStreamSources = null;

    public XSLTPipelet() {

        parameterMap.put( XSLT_FILE, new ParameterDescriptor( ParameterType.STRING, "XSLT Path", "Path to XSLT file",
                "" ) );
        parameterMap.put( PARTNER_SPECIFIC, new ParameterDescriptor( ParameterType.BOOLEAN,
                "Partner Specific Configuration", "Partner Specific Configuration", Boolean.FALSE ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        File testFile = null;

        Boolean partnerSpecificValue = getParameter( PARTNER_SPECIFIC );
        if ( partnerSpecificValue != null ) {
            partnerSpecific = partnerSpecificValue.booleanValue();
        }

        String xsltFileNameValue = getParameter( XSLT_FILE );
        if ( ( xsltFileNameValue != null ) && ( xsltFileNameValue.length() != 0 ) ) {
            xsltFileName = xsltFileNameValue;
            testFile = new File( xsltFileName );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "XSLT file does not exist: " + testFile.getAbsolutePath() );
                return;
            }

            if ( partnerSpecific ) {
                StreamSource partnerSource = null;
                PartnerSpecificConfigurations partnerSpecificConfigurations = readPartnerSpecificConfigurations( xsltFileName );
                partnerStreamSources = new HashMap<String, StreamSource>();

                for ( PartnerSpecificConfiguration partnerSpecificConfiguration : partnerSpecificConfigurations
                        .getPartnerSpecificConfigurations() ) {
                    partnerSource = new StreamSource( new File( partnerSpecificConfiguration.getConfigurationFile() ) );
                    partnerStreamSources.put( partnerSpecificConfiguration.getPartnerId(), partnerSource );

                }
                for ( String partnerId : partnerStreamSources.keySet() ) {
                    LOG.debug( "Configuration fpr partner: '" + partnerId + "' - "
                            + partnerStreamSources.get( partnerId ) );
                }
            } else {

                xsltStreamSource = new StreamSource( testFile );

            }

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'xslt file' provided!" );
            return;
        }

        LOG.trace( "xsltFileName  : " + xsltFileName );

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        LOG.debug( new LogMessage( "processing xslt",messageContext.getMessagePojo()) );
        Map<String, String> map = null;

        if ( ( messageContext.getData() != null ) && ( messageContext.getData() instanceof RequestResponseData )
                && ( ( (RequestResponseData) messageContext.getData() ).getParameters() != null ) ) {
            map = ( (RequestResponseData) messageContext.getData() ).getParameters();
        }
        if(map == null) {
            map = new HashMap<String, String>();
            
        }
        map.put( "nexuse2e_partner_id", messageContext.getMessagePojo().getParticipant().getPartner().getPartnerId() );
        map.put( "nexuse2e_conversation_id", messageContext.getMessagePojo().getConversation().getConversationId() );
        map.put( "nexuse2e_message_id", messageContext.getMessagePojo().getMessageId() );
        map.put( "nexuse2e_action_id", messageContext.getMessagePojo().getAction().getName() );
        map.put( "nexuse2e_choreography_id", messageContext.getMessagePojo().getConversation().getChoreography().getName() );
        
        Map<String, String> customMap = messageContext.getMessagePojo().getCustomParameters();
        if (customMap != null) {
            for (String key : customMap.keySet()) {
                if (key != null) {
                    String value = customMap.get( key );
                    if (value != null) {
                        map.put( key, value );
                    }
                }
            }
        }
        
        StreamSource streamSource = xsltStreamSource;
        
        try {
            if(partnerSpecific) {
                for ( String key : partnerStreamSources.keySet() ) {
                    LOG.debug(new LogMessage(  "id:"+ key +" -  source: "+partnerStreamSources.get( key ),messageContext.getMessagePojo()) );
                }
                LOG.debug( new LogMessage( "selecting streamSource: "+messageContext.getMessagePojo().getParticipant().getPartner().getPartnerId(),messageContext.getMessagePojo()) );
                streamSource = partnerStreamSources.get( messageContext.getMessagePojo().getParticipant().getPartner().getPartnerId()  );
            }
            
            if ( streamSource != null ) {
                List<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads();
                for (MessagePayloadPojo messagePayloadPojo : payloads) {
                    ByteArrayInputStream bais = new ByteArrayInputStream( messagePayloadPojo.getPayloadData() );
                    messagePayloadPojo.setPayloadData( transformXML( new StreamSource( bais ), streamSource, map ) );
                }

            } else {
                LOG.error( new LogMessage( "No XSLT stylesheet configured - no transformation possible.",messageContext.getMessagePojo()) );
                throw new NexusException( "No XSLT stylesheet configured - no transformation possible." );
            }// if
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException(e);
        }

        return messageContext;
    }

    /**
     * @param configFileName
     * @return
     */
    private PartnerSpecificConfigurations readPartnerSpecificConfigurations( String configFileName ) {

        PartnerSpecificConfigurations partnerSpecificConfigurations = null;
        Digester digester = new Digester();
        digester.setValidating( false );
        digester.addObjectCreate( "PartnerSpecificConfigurations",
                "org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfigurations" );
        digester.addSetProperties( "PartnerSpecificConfigurations" );
        digester.addObjectCreate( "PartnerSpecificConfigurations/PartnerSpecificConfiguration",
                "org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration" );
        digester.addSetProperties( "PartnerSpecificConfigurations/PartnerSpecificConfiguration" );
        digester.addSetNext( "PartnerSpecificConfigurations/PartnerSpecificConfiguration",
                "addPartnerSpecificConfiguration", "org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration" );

        try {
            partnerSpecificConfigurations = (PartnerSpecificConfigurations) digester.parse( configFileName );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( SAXException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return partnerSpecificConfigurations;
    }

    /**
     * @param xmlSource
     * @param xsltSource
     * @param map
     * @return
     * @throws NexusException
     */
    private byte[] transformXML( StreamSource xmlSource, StreamSource xsltSource, Map<String, String> map ) throws NexusException {

        byte[] result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer( xsltSource );
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "XML Transformer implementation is: " + transformer.getClass().getName() );
            }
            if ( map != null ) {
                LOG.debug( "Using provided XSLT parameters..." );
                LOG.debug( "map: "+map );
                if ( LOG.isDebugEnabled() ) {
                    for ( String key : map.keySet() ) {
                        transformer.setParameter( key, map.get( key ) );
                        LOG.debug( "XSLT param: " + key + " - " + map.get( key ) );
                    }
                }
            }
            transformer.transform( xmlSource, new StreamResult( baos ) );

            result = baos.toByteArray();
        } catch ( Exception e ) {
            if ( LOG.isTraceEnabled() ) {
                e.printStackTrace();
            }
            throw new NexusException( "Error transforming payload using XSLT: " + e.getMessage(), e );
        }

        return result;
    }

    public static void main( String args[] ) {

        if ( args.length < 2 ) {
            System.err
                    .println( "Wrong number of parameters. Usage: XSLTPipelet <xml file> <xslt file> [<output file>]" );
            return;
        }
        long start = System.currentTimeMillis();

        
        StreamSource xmlSource = new StreamSource( new File( args[0] ) );
        StreamSource xsltSource = new StreamSource( new File( args[1] ) );

        Map<String, String> map = new HashMap<String, String>();
        map.put( "/dXML/Order/OrderNumber", "479855385423" );
        map.put( "/dXML/Order/ReleaseNumber", "H89550x" );

        try {
            byte[] result = new XSLTPipelet().transformXML( xmlSource, xsltSource, map );

            if ( args.length > 2 ) {
                try {
                    File output = new File( args[2] );
                    FileOutputStream fos = new FileOutputStream( output );
                    fos.write( result );
                    fos.flush();
                    fos.close();

                } catch ( FileNotFoundException e ) {
                    System.out.println( "Error while creating output: " + e );
                } catch ( IOException e ) {
                    System.out.println( "Error while writing output: " + e );
                }

            } else {
                System.out.println( "Result:\n" + new String( result ) );
            }
        } catch ( NexusException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println( "time: " + ( end - start ) );

    }
} // XSLTPipelet
