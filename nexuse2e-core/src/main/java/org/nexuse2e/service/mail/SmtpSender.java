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
package org.nexuse2e.service.mail;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SenderAware;
import org.nexuse2e.transport.TransportSender;
import org.nexuse2e.util.CertificatePojoSocketFactory;
import org.nexuse2e.util.CertificateUtil;

/**
 * The SMTP sender service.
 * 
 * @author jonas.reese
 */
public class SmtpSender extends AbstractService implements SenderAware {

    private static Logger      LOG                 = Logger.getLogger( SmtpSender.class );

    public static final String HOST_PARAM_NAME     = "host";
    public static final String PORT_PARAM_NAME     = "port";
    public static final String TIMEOUT_PARAM_NAME  = "timeout";
    public static final String EMAIL_PARAM_NAME    = "email";
    public static final String USER_PARAM_NAME     = "user";
    public static final String PASSWORD_PARAM_NAME = "password";
    public static final String ENCRYPTION_PARAM_NAME = "encryption";

    private TransportSender    transportSender;

    public SmtpSender() {
        super();
    }
    
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( HOST_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Host",
                "SMTP host name or IP address", "" ) );
        parameterMap.put( PORT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Port",
                "SMTP port number (default is 25 or 465 for SSL)", "25" ) );
        parameterMap.put( TIMEOUT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Timeout",
                "Connection and I/O timeout in milliseconds (default is 10000)", "10000" ) );
        parameterMap.put( EMAIL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Email",
                "Sender email address", "" ) );
        parameterMap.put( USER_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "User",
                "Authentication user name", "" ) );
        parameterMap.put( PASSWORD_PARAM_NAME, new ParameterDescriptor( ParameterType.PASSWORD, "Password",
                "Authentication user password", "" ) );
        ListParameter encryptionTypeDrowdown = new ListParameter();
        encryptionTypeDrowdown.addElement( "None", "none" );
        encryptionTypeDrowdown.addElement( "TLS", "tls" );
        encryptionTypeDrowdown.addElement( "SSL", "ssl" );
        parameterMap.put( ENCRYPTION_PARAM_NAME, new ParameterDescriptor(
                ParameterType.LIST, "Encryption", "Connection encryption type", encryptionTypeDrowdown ) );
    }

    private boolean isSslEnabled() {
        ListParameter lp = getParameter( ENCRYPTION_PARAM_NAME );
        if (lp != null) {
            if ("ssl".equals( lp.getSelectedValue() )) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTlsEnabled() {
        ListParameter lp = getParameter( ENCRYPTION_PARAM_NAME );
        if (lp != null) {
            if ("tls".equals( lp.getSelectedValue() )) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void start() {

        super.start();
        LOG.debug( "SmtpSender service started" );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

    public TransportSender getTransportSender() {

        return transportSender;
    }

    public void setTransportSender( TransportSender transportSender ) {

        this.transportSender = transportSender;
    }

    public MessageContext sendMessage( MessageContext messageContext ) throws NexusException {

        Session session = null;
        Transport transport = null;
        
        if ( BeanStatus.STARTED != status ) {
            LOG.warn( "SMTP service not started!" );
            return null;
        }

        try {
            ParticipantPojo participant = messageContext.getParticipant();
            String emailAddr = participant.getConnection().getUri();

            // LOG.trace( "sendMessage: " + smtpHost + " - " + smtpUser + " - " + smtpPassword );
            Object[] connectionInfo = connect( (String) getParameter( HOST_PARAM_NAME ),
                    (String) getParameter( USER_PARAM_NAME ),
                    (String) getParameter( PASSWORD_PARAM_NAME ),
                    (String) getParameter( PORT_PARAM_NAME ),
                    (String) getParameter( TIMEOUT_PARAM_NAME ) );
            if ( connectionInfo != null ) {
                session = (Session) connectionInfo[0];
                transport = (Transport) connectionInfo[1];

                InternetAddress addr = new InternetAddress( emailAddr );

                MimeMessage mimeMsg = createMimeSMTPMsg( session, messageContext, participant.getConnection()
                        .isSecure() );
                mimeMsg.setRecipient( javax.mail.Message.RecipientType.TO, addr );
                mimeMsg.setFrom( new InternetAddress( (String) getParameter( EMAIL_PARAM_NAME ) ) );
                mimeMsg.setSubject( messageContext.getMessagePojo().getConversation().getConversationId() );
                mimeMsg.setSentDate( new java.util.Date() );
                mimeMsg.setHeader( "SOAPAction", "ebXML" );
                mimeMsg.saveChanges();

                // DEBUG OUTPUT--------------------------------
                if ( LOG.isDebugEnabled() ) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mimeMsg.writeTo( baos );
                    String dump = baos.toString();
                    LOG.debug(new LogMessage( "********** OUTBOUND *********\n" + dump + "\n*****************************",messageContext.getMessagePojo()) );
                }

                // Send the message
                sendMessage( transport, mimeMsg, new Address[] { addr } );
                
                transport.close();
            } else {
                LOG.error(new LogMessage("Cannot connect", messageContext.getMessagePojo()));
            }

        } catch ( Exception ex ) {
            LOG.error(new LogMessage("Error sending SMTP message", messageContext, ex), ex);
            throw new NexusException( ex );
        }
        
        return null;
    }

    private Object[] connect( String host, String user, String password, String port, String timeout ) throws Exception {

        boolean ssl = isSslEnabled();
        boolean tls = isTlsEnabled();
        boolean authenticate = false;

        if ( !StringUtils.isEmpty( user ) && !StringUtils.isEmpty( password ) ) {
            authenticate = true;
        }

        
        String protocol = ssl ? "smtps" : "smtp";

        if ( host != null && !host.trim().equals( "" ) ) {
            Properties props = new Properties( System.getProperties() );
            props.put( "mail." + protocol + ".host", host );
            if ( authenticate ) {
                props.put( "mail." + protocol + ".auth", "true" );
            } else {
                props.put( "mail." + protocol + ".auth", "false" );
            }
            if ( !StringUtils.isEmpty( port ) ) {
                props.put( "mail." + protocol + ".port", port );
            } else {
                props.remove( "mail." + protocol + ".port" );
            }
            if ( ssl ) {
                props.put( "mail." + protocol + ".socketFactory.class", CertificatePojoSocketFactory.class.getName() );
            }
            props.put( "mail." + protocol + ".starttls.enable", Boolean.toString( tls ) );

            props.put( "mail.host", host );

            if (timeout != null) {
                props.put( "mail." + protocol + ".connectiontimeout", timeout );
                props.put( "mail." + protocol + ".timeout", timeout );
            }

            // Get a Session object
            Session session = Session.getInstance( props, null );

            String urlNameString = protocol + "://" + host;
            URLName urlName = new URLName( urlNameString );
            if ( authenticate ) {
                PasswordAuthentication passwordAuthentication = new PasswordAuthentication( user, password );
                session.setPasswordAuthentication( urlName, passwordAuthentication );
            }
            Transport transport = session.getTransport( urlName );
            if ( authenticate ) {
                transport.connect( host, user, password );
            } else {
                transport.connect();
            }

            return new Object[] { session, transport};
        }

        return null;
    } // connect

    /**
     * Create a mime encoded message
     * @param msg
     */
    private static MimeMessage createMimeSMTPMsg( Session session, MessageContext messagePipelineParameter,
            boolean useEncryption ) throws NexusException {

        MimeMessage mimeMessage = null;
        MimeMultipart mimeMultipart = null;
        MimeBodyPart mimeBodyPart = null;
        SMIMESignedGenerator signer = null;
        SMIMEEnvelopedGenerator generator = null;
        PrivateKey privateKey = null;

        try {

            mimeMessage = new MimeMessage( session );
            mimeMultipart = new MimeMultipart();
            mimeMultipart.setSubType( "related" );
            // mimeMultipart.setSubType( "mixed" );

            if ( useEncryption ) {
                generator = new SMIMEEnvelopedGenerator();
                ParticipantPojo participant = messagePipelineParameter.getParticipant();
                CertificatePojo certPojo = participant.getConnection().getCertificate();

                List<CertificatePojo> certificates = Engine.getInstance().getActiveConfigurationAccessService()
                        .getCertificates( Constants.CERTIFICATE_TYPE_LOCAL, null );
                if ( !certificates.isEmpty() ) {
                    CertificatePojo localCert = certificates.iterator().next();
                    KeyStore privateKeyChain = CertificateUtil.getPKCS12KeyStore( localCert );
                    privateKey = (PrivateKey) CertificateUtil.getPrivateKey( privateKeyChain );

                    Certificate[] localCertChain = CertificateUtil.getCertificateChain( privateKeyChain );
                    X509Certificate serverCertX509 = (X509Certificate) localCertChain[0];

                    if ( ( certPojo != null ) && ( serverCertX509 != null ) && ( privateKey != null ) ) {
                        X509Certificate x509Cert = CertificateUtil.getX509Certificate( certPojo.getBinaryData() );

                        generator.addKeyTransRecipient( x509Cert ); // need users

                        /* Create the SMIMESignedGenerator */
                        SMIMECapabilityVector capabilities = new SMIMECapabilityVector();
                        capabilities.addCapability( SMIMECapability.dES_EDE3_CBC );
                        capabilities.addCapability( SMIMECapability.rC2_CBC, 128 );
                        capabilities.addCapability( SMIMECapability.dES_CBC );

                        ASN1EncodableVector attributes = new ASN1EncodableVector();

                        attributes.add( new SMIMEEncryptionKeyPreferenceAttribute( new IssuerAndSerialNumber(
                                new X509Name( serverCertX509.getIssuerDN().getName() ), serverCertX509
                                        .getSerialNumber() ) ) );
                        attributes.add( new SMIMECapabilitiesAttribute( capabilities ) );

                        signer = new SMIMESignedGenerator();
                        signer.addSigner( privateKey, serverCertX509, SMIMESignedGenerator.DIGEST_MD5,
                                new AttributeTable( attributes ), null );

                        /* Add the list of certs to the generator */
                        CertStore certStore = CertStore.getInstance( "Collection", new CollectionCertStoreParameters(
                                Collections.singletonList( serverCertX509 ) ), CertificateUtil.DEFAULT_JCE_PROVIDER );
                        signer.addCertificatesAndCRLs( certStore );
                    } else {
                        // Reset generator to avoid errors during encoding
                        generator = null;
                        useEncryption = false;
                    }
                } else {
                    LOG.error( "No server certificate available for encrypted email communication!" );
                }
            }

            // ebxml header
            MessagePojo msg = messagePipelineParameter.getMessagePojo();
            String ebXmlHeader = new String( msg.getHeaderData() );
            mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent( ebXmlHeader, "text/xml" );
            mimeBodyPart.setHeader( "Content-ID", msg.getMessageId() + "-" + msg.getTRP().getProtocol() + "-Header" );
            mimeBodyPart.setHeader( "Content-Type", "text/xml; charset=UTF-8" );

            mimeMultipart.addBodyPart( mimeBodyPart );

            // Encode body
            int partCount = 0;
            for ( MessagePayloadPojo payload : msg.getMessagePayloads() ) {

                mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setHeader( "Content-ID", msg.getMessageId() + "-body" + partCount );

                if ( payload.getMimeType().startsWith( "text" ) || payload.getMimeType().startsWith( "TEXT" ) ) {
                    //         mimeBodyPart.setContent( payload.getContentAsString(), payload.getContentType() );
                    mimeBodyPart.setContent( new String( payload.getPayloadData() ), "text/xml" );
                    mimeBodyPart.setHeader( "Content-Type", "text/xml; charset=UTF-8" );
                } else {
                	// TODO (encoding) should be byte[], not string ?
                    mimeBodyPart.setContent( new String( payload.getPayloadData() ), payload.getMimeType() );
                }

                if ( useEncryption && ( generator != null ) ) {
                    // Sign body part
                    mimeBodyPart = signer.generateEncapsulated( mimeBodyPart, CertificateUtil.DEFAULT_JCE_PROVIDER );

                    // Encrypt body part
                    LOG.debug( new LogMessage("Encrypting payload using TDES...",msg) );
                    mimeBodyPart = generator.generate( mimeBodyPart, SMIMEEnvelopedGenerator.DES_EDE3_CBC, 128,
                            CertificateUtil.DEFAULT_JCE_PROVIDER );
                }

                mimeMultipart.addBodyPart( mimeBodyPart );
            } // for

            // Add content type attributes required for multipart/related
            ContentType contentType = new ContentType( "multipart/related" );
            contentType.setParameter( "type", "text/xml" );
            contentType.setParameter( "boundary", "MIME_boundary" );
            contentType.setParameter( "start", msg.getMessageId() + msg.getTRP().getProtocol() + "-Header" );

            // sjw fix later
            //contentType.setParameter( "version", "2.0" );
            //            contentType.setParameter( "version", msg.getProtocolVersion() );
            mimeMessage.setHeader( "Content-Type", contentType.toString() );

            // MUST appear after setHeader with content-type!!!
            mimeMessage.setContent( mimeMultipart );

            mimeMessage.saveChanges();
        } catch ( Throwable ex ) {
            ex.printStackTrace();
            throw new NexusException( "Error creating mime message: " + ex.toString() );
        }

        return mimeMessage;
    }

    public void sendMessage( MessageContext messagePipelineParameter, boolean useSSL ) throws MessagingException {

        Session session = null;
        Transport transport = null;

        if ( BeanStatus.STARTED != status ) {
            LOG.warn(new LogMessage( "SMTP service not started!",messagePipelineParameter.getMessagePojo()) );
            return;
        }

        try {
            ParticipantPojo p = messagePipelineParameter.getParticipant();
            String emailAddr = p.getConnection().getUri();

            // LOG.trace( "sendMessage: " + smtpHost + " - " + smtpUser + " - " + smtpPassword );
            Object[] connectionInfo = connect( (String) getParameter( HOST_PARAM_NAME ),
                    (String) getParameter( USER_PARAM_NAME ),
                    (String) getParameter( PASSWORD_PARAM_NAME ),
                    (String) getParameter( PORT_PARAM_NAME ),
                    (String) getParameter( TIMEOUT_PARAM_NAME ) );
            session = (Session) connectionInfo[0];
            transport = (Transport) connectionInfo[1];

            InternetAddress addr = new InternetAddress( emailAddr );

            MimeMessage mimeMsg = null;

            // Create the message
            mimeMsg = createMimeSMTPMsg( session, messagePipelineParameter, useSSL );
            mimeMsg.setRecipient( Message.RecipientType.TO, addr );
            mimeMsg.setFrom( new InternetAddress( (String) getParameter( EMAIL_PARAM_NAME ) ) );
            mimeMsg.setSubject( messagePipelineParameter.getConversation().getConversationId() );
            mimeMsg.setSentDate( new Date() );
            mimeMsg.setHeader( "SOAPAction", "ebXML" );
            mimeMsg.saveChanges();

            // DEBUG OUTPUT--------------------------------
            if ( LOG.isDebugEnabled() ) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mimeMsg.writeTo( baos );
                String dump = baos.toString();
                LOG.debug( new LogMessage("********** OUTBOUND *********\n" + dump + "\n*****************************",messagePipelineParameter.getMessagePojo()) );
            }

            // Send the message
            javax.mail.Address[] aArray = { addr};
            sendMessage( transport, mimeMsg, aArray );

        } catch ( Exception ex ) {
            throw new MessagingException( ex.getMessage() );
        }

    }

    public void sendMessage( String recipient, String subjectLine, String description ) throws NexusException {

        sendMessage( recipient, subjectLine, description, null );
    }

    public void sendMessage( String recipient, String subjectLine, String description, MimeBodyPart[] mimeBodyParts )
            throws NexusException {

        Session session = null;
        Transport transport = null;

        if ( BeanStatus.STARTED != status ) {
            System.err.println( "SMTP service not started!" );
            return;
        }

        try {
            Object[] connectionInfo = connect( (String) getParameter( HOST_PARAM_NAME ),
                    (String) getParameter( USER_PARAM_NAME ),
                    (String) getParameter( PASSWORD_PARAM_NAME ),
                    (String) getParameter( PORT_PARAM_NAME ),
                    (String) getParameter( TIMEOUT_PARAM_NAME ) );
            if (connectionInfo != null) {
                session = (Session) connectionInfo[0];
                transport = (Transport) connectionInfo[1];
    
                // construct the message
                Message msg = new MimeMessage( session );
                msg.setRecipients( Message.RecipientType.TO, InternetAddress.parse( recipient, false ) );
                msg.setHeader( "X-Mailer", "msgsend" );
                msg.setFrom( new InternetAddress( (String) getParameter( EMAIL_PARAM_NAME ) ) );
                if ( ( mimeBodyParts != null ) && ( mimeBodyParts.length != 0 ) ) {
                    Multipart multipart = new MimeMultipart();
                    for ( int i = 0; i < mimeBodyParts.length; i++ ) {
                        multipart.addBodyPart( mimeBodyParts[i] );
                    }
                    msg.setContent( multipart );
                } else {
                    msg.setText( description );
                }
                msg.setSentDate( new Date() );
                msg.setSubject( subjectLine );
                msg.saveChanges();
                // send the thing off
                sendMessage( transport, msg, msg.getAllRecipients() );
            } else {
                throw new NexusException( "Cannot send message: Unable to connect to mail host" );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( "Error composing mail Exception: ", e );
        }
    }

    private void sendMessage( Transport transport, Message message, Address[] addresses ) throws MessagingException {

        if ( BeanStatus.STARTED != status ) {
            System.err.println( "SMTP service not started!" );
            return;
        }

        if ( transport != null ) {
            if ( !transport.isConnected() ) {
                transport.connect( (String) getParameter( HOST_PARAM_NAME ), (String) getParameter( USER_PARAM_NAME ),
                        (String) getParameter( PASSWORD_PARAM_NAME ) );
            }
            transport.sendMessage( message, addresses );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        super.teardown();

        transportSender = null;
    } // teardown

} // SmtpSender
