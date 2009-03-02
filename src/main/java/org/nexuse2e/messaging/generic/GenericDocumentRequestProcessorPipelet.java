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
package org.nexuse2e.messaging.generic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.FrontendInboundDispatcher;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.RequestInfo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This pipelet implementation checks the payload for a GenericDocumentRequest document
 * and manipulates the message context so the {@link FrontendInboundDispatcher} knows
 * that this is a request for a document or a set of documents instead of a business
 * document itself..
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class GenericDocumentRequestProcessorPipelet extends AbstractPipelet {

    private static Logger       LOG            = Logger.getLogger( GenericDocumentRequestProcessorPipelet.class );

    /**
     * Default constructor.
     */
    public GenericDocumentRequestProcessorPipelet() {
        frontendPipelet = true;
    }
    
    
    protected RequestInfo getRequestInfoFromXml( Node rootNode ) throws XPathExpressionException {
        RequestInfo requestInfo = null;
        
        if (rootNode != null && "GenericDocumentRequest".equals( rootNode.getNodeName() )) { // this is a request document
            LOG.info( "Found a GenericDocumentRequest" );
            XPath xPath = XPathFactory.newInstance().newXPath();
            String documentType = xPath.evaluate( "/GenericDocumentRequest/GenericDocumentRequestBody/DocumentType", rootNode );
            requestInfo = new RequestInfo( documentType );
        }
        return requestInfo;
    }
    
    

    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException,
            NexusException {

        if (messageContext != null) {
            List<MessagePayloadPojo> messagePayloads = messageContext.getMessagePojo().getMessagePayloads();
            if (messagePayloads.size() == 1) {
                MessagePayloadPojo messagePayload = messagePayloads.get( 0 );

                // get DOM tree
                Node n;
                if (messageContext.getData() instanceof Node) {
                    n = (Node) messageContext.getData();
                } else {
                    try {
                        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                        builderFactory.setNamespaceAware( true );
                        Document d = builderFactory.newDocumentBuilder().parse( new ByteArrayInputStream( messagePayload.getPayloadData() ) );
                        n = d.getDocumentElement();
                    } catch (SAXException e) {
                        throw new NexusException( e );
                    } catch (IOException e) {
                        throw new NexusException( e );
                    } catch (ParserConfigurationException e) {
                        throw new NexusException( e );
                    }
                }
                try {
                    messageContext.setRequestInfo( getRequestInfoFromXml( n ) );
                } catch (XPathExpressionException e) {
                    throw new NexusException( e );
                }
            }

        }
        return messageContext;
    }
    
}
