/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.nexuse2e.backend.pipelets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.backend.pipelets.helper.RequestResponseData;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

/**
 * @author mbreilmann
 *
 */
public class XSLTPipelet extends AbstractPipelet {

    private static Logger      LOG              = Logger.getLogger( XSLTPipelet.class );

    public static final String XSLT_FILE        = "xsltFile";

    private String             xsltFileName     = null;
    private StreamSource       xsltStreamSource = null;

    public XSLTPipelet() {

        parameterMap.put( XSLT_FILE, new ParameterDescriptor( ParameterType.STRING, "XSLT Path", "Path to XSLT file",
                "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        File testFile = null;

        String xsltFileNameValue = getParameter( XSLT_FILE );
        if ( ( xsltFileNameValue != null ) && ( xsltFileNameValue.length() != 0 ) ) {
            xsltFileName = xsltFileNameValue;
            testFile = new File( xsltFileName );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "XSLT file does not exist: " + testFile.getAbsolutePath() );
                return;
            }

            xsltStreamSource = new StreamSource( testFile );
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

        Map<?, ?> map = null;

        if ( ( messageContext.getData() != null ) && ( messageContext.getData() instanceof RequestResponseData )
                && ( ( (RequestResponseData) messageContext.getData() ).getParameters() != null ) ) {
            map = ( (RequestResponseData) messageContext.getData() ).getParameters();
        }
        if ( xsltStreamSource != null ) {
            List<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads();
            for ( Iterator iter = payloads.iterator(); iter.hasNext(); ) {
                MessagePayloadPojo messagePayloadPojo = (MessagePayloadPojo) iter.next();
                ByteArrayInputStream bais = new ByteArrayInputStream( messagePayloadPojo.getPayloadData() );

                messagePayloadPojo.setPayloadData( transformXML( new StreamSource( bais ), xsltStreamSource, map ) );

                if ( LOG.isTraceEnabled() ) {
                    LOG.trace( "...................." );
                    LOG.trace( new String( messagePayloadPojo.getPayloadData() ) );
                    LOG.trace( "...................." );
                }
            }

        } else {
            LOG.error( "No XSLT stylesheet configured - no transformation possible." );
            throw new NexusException( "No XSLT stylesheet configured - no transformation possible." );
        }// if

        return messageContext;
    }

    private byte[] transformXML( StreamSource xmlSource, StreamSource xsltSource, Map<?, ?> map ) throws NexusException {

        byte[] result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer( xsltSource );
            if ( map != null ) {
                LOG.debug( "Using provided XSLT parameters..." );
                if ( LOG.isTraceEnabled() ) {
                    for ( Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
                        String key = (String) iter.next();
                        transformer.setParameter( key, map.get( key ) );
                        LOG.trace( "XSLT param: " + key + " - " + map.get( key ) );
                    }
                }
            }
            transformer.transform( xmlSource, new StreamResult( baos ) );

            result = baos.toByteArray();
        } catch ( Exception e ) {
            if ( LOG.isTraceEnabled() ) {
                e.printStackTrace();
            }
            throw new NexusException( "Error transforming payload using XSLT: " + e );
        }

        return result;
    }

    public static void main( String args[] ) {

        if ( args.length < 2 ) {
            System.err.println( "Wrong number of parameters. Usage: XSLTPipelet <xml file> <xslt file> [<output file>]" );
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
            
            if(args.length > 2) {
                try {
                    File output = new File(args[2]);
                    FileOutputStream fos = new FileOutputStream(output);
                    fos.write( result );
                    fos.flush();
                    fos.close();
                    
                } catch ( FileNotFoundException e ) {
                    System.out.println("Error while creating output: "+e);
                } catch ( IOException e ) {
                    System.out.println("Error while writing output: "+e);
                }
                
            }
            else {
                System.out.println( "Result:\n" + new String( result ) );
            }
        } catch ( NexusException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("time: "+ (end-start));
        
    }
} // XSLTPipelet
