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
package org.nexuse2e.messaging.ebxml;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchProviderException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * This <code>Pipelet</code> prepares an MIME message from a MIME message receiver
 * (e.g., a POP3 receiver) for further movement through an EBXML pipeline.
 * 
 * @author jonas.reese
 */
public class MimeMessageUnpackager extends AbstractPipelet {

    private static Logger       LOG = Logger.getLogger( MimeMessageUnpackager.class );

    
    /**
     * Default constructor.
     */
    public MimeMessageUnpackager() {

    }

    public MessageContext processMessage( MessageContext messagePipeletParameter )
            throws NexusException {

        MessagePojo messagePojo = messagePipeletParameter.getMessagePojo();
        List<MessagePayloadPojo> payloads = getDataFromMailMsg( (Message) messagePipeletParameter.getData() );

        String msgHdr = new String( payloads.get( 0 ).getPayloadData() );
        if ( msgHdr != null ) {
            msgHdr = msgHdr.trim();
            messagePojo.setHeaderData( msgHdr.getBytes() );
            LOG.debug( "Header: " + msgHdr );
        }
        
        // Remove header from payloads
        payloads.remove( 0 );
        
        int i = 0;
        for ( MessagePayloadPojo messagePayloadPojo : payloads ) {
            LOG.debug( "Payload " + i++ + ": " + new String( messagePayloadPojo.getPayloadData() ) );
            messagePayloadPojo.setMessage( messagePojo );
        }
        messagePojo.setMessagePayloads( payloads );

        return messagePipeletParameter;
    }

   

    private List<MessagePayloadPojo> getDataFromMailMsg( Message message ) throws NexusException {

        RecipientId recipientId = null;
        Part messagePart = message;
        Object content;
        Key privateKey = null;

        try {
            content = messagePart.getContent();
        } catch ( Exception e ) {
            throw new NexusException( e );
        }
        if ( content instanceof Multipart ) {
            try {
                Multipart mp = (Multipart) content;
                // Prepare for S/MIME decoding
                List<CertificatePojo> certificates = Engine.getInstance().getActiveConfigurationAccessService().getCertificates(
                        org.nexuse2e.configuration.Constants.CERTIFICATE_TYPE_LOCAL, null );
                if ( certificates.size() > 0 ) {
                    CertificatePojo localCert = certificates.iterator().next();
                    Certificate[] localCertChain = CertificateUtil.getLocalCertificateChain( localCert );
                    X509Certificate serverCertX509 = (X509Certificate) localCertChain[0];
                    // X509Certificate serverCertX509 = CertificateUtil.getX509Certificate( localCert.getBinaryData() );
                    if ( serverCertX509 != null ) {
                        recipientId = new RecipientId();
                        recipientId.setSerialNumber( serverCertX509.getSerialNumber() );
                        recipientId.setIssuer( serverCertX509.getIssuerX500Principal().getEncoded() );
                    }
                    KeyStore privateKeyChain = CertificateUtil.getPKCS12KeyStoreFromByteArray( localCert
                            .getBinaryData(), EncryptionUtil.decryptString( localCert.getPassword() ) );
                    privateKey = CertificateUtil.getPrivateKey( privateKeyChain );
                }

                List<MessagePayloadPojo> payloads = new ArrayList<MessagePayloadPojo>();
                int[] sequenceNumber = new int[] { 0};
                extractMultiPart( payloads, sequenceNumber, recipientId, mp, privateKey );

                return payloads;
            } catch ( Exception ex ) {
                throw new NexusException( ex );
            }
        } else {
            throw new NexusException( "Incoming email not MimeMultipart. Content: " + content );
        }
    }// getDataFromMailMsg

    private void extractMultiPart( List<MessagePayloadPojo> payloads, int[] sequenceNumber, RecipientId recipientId,
            Multipart mp, Key privateKey ) throws MessagingException, CMSException, SMIMEException,
            NoSuchProviderException, IOException, Exception {

        for ( int i = 0; i < mp.getCount(); i++ ) {
            BodyPart bp = mp.getBodyPart( i );
            if ( bp.getContent() instanceof Multipart ) {
                extractMultiPart( payloads, sequenceNumber, recipientId, (Multipart) bp.getContent(), privateKey );
            } else {
                payloads.add( extractPayload( recipientId, bp, privateKey, sequenceNumber[0]++ ) );
            }

        }
    }

    private MessagePayloadPojo extractPayload( RecipientId recipientId, BodyPart bp, Key privateKey, int sequenceNumber )
            throws MessagingException, CMSException, SMIMEException, NoSuchProviderException, IOException, Exception {

        MimeBodyPart mbp = (MimeBodyPart) bp;

        LOG.debug( "content type: " + mbp.getContentType() );

        // Decode S/MIME
        if ( ( mbp.getContentType().indexOf( "application/pkcs7-mime" ) != -1 ) && ( privateKey != null )
                && ( recipientId != null ) ) {
            SMIMEEnveloped m = new SMIMEEnveloped( mbp );

            RecipientInformationStore recipients = m.getRecipientInfos();
            RecipientInformation recipient = recipients.get( recipientId );

            if ( recipient != null ) {
                // System.out.println( "-------------------------------------------" );
                LOG.debug( "Using private key:\n" + privateKey );
                LOG.debug( "recipient.getKeyEncryptionAlgOID:\n" + recipient.getKeyEncryptionAlgOID() );
                // System.out.println( "-------------------------------------------" );
                mbp = SMIMEUtil
                        .toMimeBodyPart( recipient.getContent( privateKey, CertificateUtil.DEFAULT_JCE_PROVIDER ) );
                LOG.debug( "Decoded content:\n" + mbp.getContent() );
            } else {
                LOG.error( "The inbound message was not encrypted for the currently "
                        + "configured certificate! The message will be discarded." );
            }
        }
        if ( mbp.getContentType().indexOf( "application/pkcs7-mime" ) != -1
                || mbp.getContentType().indexOf( "application/pkcs7-signature" ) != -1 ) {
            // Get content from signed body part
            SMIMESigned sMIMESigned = null;
            // System.out.println( "Mime type: " + mbp.getContentType( ) );
            if ( mbp.getContentType().indexOf( "multipart/signed" ) != -1 ) {
                Object signedContent = mbp.getContent();
                // System.out.println( "Class: " + signedContent.getClass() );
                if ( signedContent instanceof MimeMultipart ) {
                    sMIMESigned = new SMIMESigned( (MimeMultipart) signedContent );
                } else {
                    LOG.error( "Conent type not supported: " + signedContent.getClass() );
                }
            } else {
                sMIMESigned = new SMIMESigned( mbp );
            }
            verifySignature( sMIMESigned );
            // extract the content
            mbp = sMIMESigned.getContent();
        }
        InputStream in = mbp.getInputStream();
        byte[] data = new byte[in.available()];
        in.read( data );
        return new MessagePayloadPojo( null, sequenceNumber, mbp.getContentType(), mbp.getContentID(), data,
                new Date(), new Date(), 0 );
    }

    @SuppressWarnings("unchecked")
    private X509Certificate[] verifySignature( SMIMESigned s ) throws Exception {

        X509Certificate firstCert = null;
        Collection firstSignerChain = null;
        X509Certificate cert = null;

        // certificates and crls passed in the signature
        CertStore certs = s.getCertificatesAndCRLs( "Collection", CertificateUtil.DEFAULT_JCE_PROVIDER );

        // SignerInfo blocks which contain the signatures
        SignerInformationStore signers = s.getSignerInfos();

        // check each signer
        for ( Object o : signers.getSigners() ) {
            SignerInformation signer = (SignerInformation) o;
            Collection certCollection = certs.getCertificates( signer.getSID() );
            if ( firstSignerChain == null ) {
                firstSignerChain = certCollection;
            }
            Iterator certIt = certCollection.iterator();
            if ( certIt.hasNext() ) {
                cert = (X509Certificate) certIt.next();
                if ( firstCert == null ) {
                    firstCert = cert;
                }
                // verify that the sig is correct and that it was generated
                // when the certificate was current
                if ( signer.verify( cert, CertificateUtil.DEFAULT_JCE_PROVIDER ) ) {
                } else {
                    LOG.error( "signature verification failed!" );
                }
            } else {
                LOG.error( "No certificate found for S/MIME signature!" );
            }
        }
        return (X509Certificate[]) firstSignerChain.toArray( new X509Certificate[firstSignerChain.size()] );
    } // verifySignature
}
