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
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.transport.TransportReceiver;
import org.nexuse2e.util.CertificatePojoSocketFactory;

/**
 * This class implements the POP3 receiver service.
 *
 * @author jonas.reese
 */
public class Pop3Receiver extends AbstractService implements ReceiverAware, Runnable {

    private static Logger      LOG                      = Logger.getLogger( Pop3Receiver.class );

    public static final String HOST_PARAM_NAME          = "host";
    public static final String PORT_PARAM_NAME          = "port";
    public static final String USER_PARAM_NAME          = "user";
    public static final String PASSWORD_PARAM_NAME      = "password";
    public static final String ENCRYPTION_PARAM_NAME    = "encryption";
    //    public static final String AUTOPOLL_PARAM_NAME      = "autopoll";
    public static final String POLL_INTERVAL_PARAM_NAME = "pollInterval";

    private TransportReceiver  transportReceiver;

    private Thread             thread                   = null;
    private long               pollingInterval          = 30000;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( HOST_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Host",
                "POP3 host name or IP address", "" ) );
        parameterMap.put( PORT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Port",
                "POP3 port number (default is 110 or 995 for SSL)", "110" ) );
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

        // obsolete, service polls automatically when started. Method poll is available when service is active.

        //        parameterMap.put( AUTOPOLL_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "Poll automatically",
        //                "Automatically check for new emails", Boolean.TRUE ) );
        parameterMap.put( POLL_INTERVAL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING,
                "Polling interval (sec)", "Polling interval in seconds", "300" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#getActivationRunlevel()
     */
    @Override
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#start()
     */
    @Override
    public void start() {

        if ( getStatus().getValue() < BeanStatus.STARTED.getValue() ) {
            super.start();

            int intervalSeconds = Integer.parseInt( (String) getParameter( POLL_INTERVAL_PARAM_NAME ) );
            pollingInterval = intervalSeconds * 1000;

            thread = new Thread( this, "MailPolling" );
            thread.start();

            LOG.debug( "Pop3Receiver service started" );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#stop()
     */
    @Override
    public void stop() {

        if ( getStatus() == BeanStatus.STARTED ) {
            if ( thread != null ) {
                thread.interrupt();
                thread = null;
            }
            super.stop();
        }
    }

    /**
     * 
     */
    public void poll() {

        // do nothing if not activated
        if ( getStatus().getValue() < BeanStatus.ACTIVATED.getValue() ) {
            return;
        }

        LOG.trace( "Polling mail account..." );

        receiveMail( (String) getParameter( HOST_PARAM_NAME ), (String) getParameter( USER_PARAM_NAME ),
                (String) getParameter( PASSWORD_PARAM_NAME ), (String) getParameter( PORT_PARAM_NAME ) );
    }

    private boolean receiveMail( String popServer, String popUser, String popPassword, String port ) {

        Store store = null;
        Folder folder = null;
        int mailsReceived = 0;
        boolean result = false;

        if ( ( popServer == null ) || ( popUser == null ) || ( popPassword == null ) ) {
            return result;
        }

        try {
            Session session = connect( popServer, popUser, popPassword, port );
            if ( session == null ) {
                return false;
            }

            store = session.getStore( (isSslEnabled() ? "pop3s" : "pop3") );
            store.connect( popServer, popUser, popPassword );

            LOG.trace( "Connected to server, checking for email messages" );

            // -- Try to get hold of the default folder --
            folder = store.getDefaultFolder();
            if ( folder == null ) {
                throw new Exception( "No default folder" );
            }

            // -- ...and its INBOX --
            folder = folder.getFolder( "inbox" );
            if ( folder == null ) {
                throw new Exception( "No POP3 INBOX folder" );
            }

            // -- Open the folder for read only --
            folder.open( Folder.READ_WRITE );

            // -- Get the message wrappers and process them --
            Message[] msgs = folder.getMessages();

            for ( int msgNum = 0; msgNum < msgs.length; msgNum++ ) {
                LOG.debug( "Processing message " + ( msgNum + 1 ) + " of " + msgs.length );
                Address[] addresses = msgs[msgNum].getFrom();
                LOG.debug( "Sender: " + ( (InternetAddress) addresses[0] ).getAddress() );

                mailsReceived++;
                if ( LOG.isDebugEnabled() ) {
                    if ( msgs[msgNum] instanceof MimeMessage ) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ( (MimeMessage) msgs[msgNum] ).writeTo( baos );
                        String dump = baos.toString();
                        LOG.debug( "********** INBOUND **********\n" + dump + "\n*****************************" );
                    }
                }

                // TODO: handle exceptions
                try {
                    MessageContext messageContext = new MessageContext();
                    messageContext.setData( msgs[msgNum] );
                    MessageContext responseContext = transportReceiver.processMessage( messageContext );
                    if ( responseContext != null ) {

                        LOG.trace( "ResponseContent:" + responseContext.getData() );
                        LOG.error( "no synchronous message transmission available for email connections!" );
                    }
                } catch ( Exception e ) {
                    LOG.warn( "Error processing email message: " + e );
                    e.printStackTrace();
                }
                msgs[msgNum].setFlag( Flags.Flag.DELETED, true );
            }
            result = true;

        } catch ( Exception ex ) {
            LOG.error( ex.getMessage(), ex );
            ex.printStackTrace();
            // handle error here
        } finally {
            // -- Close down nicely --
            try {
                if ( folder != null ) {
                    folder.close( true );
                }
                if ( store != null ) {
                    store.close();
                }
            } catch ( Exception ex2 ) {
                LOG.error( ex2.getMessage(), ex2 );
            }
        }

        return result;
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
    
    private Session connect( String host, String user, String password, String port ) throws Exception {

        boolean ssl = isSslEnabled();
        boolean tls = isTlsEnabled();
        
        String protocol = (ssl ? "pop3s" : "pop3");
        
        if ( ( host != null ) && !host.equals( "" ) && !host.equals( " " ) ) {
            Properties props = new Properties( System.getProperties() );
            props.put( "mail.host", host );

            if (port != null && !StringUtils.isEmpty( port )) {
                props.put( "mail." + protocol + ".port", port );
            } else {
                props.remove( "mail." + protocol + ".port" );
            }
            if (ssl) {
                props.put( "mail." + protocol + ".socketFactory.class", CertificatePojoSocketFactory.class.getName() );
            }
            
            if (tls) {
                // TODO: implement this
                props.put( "mail" + protocol + ".starttls.enabled", Boolean.TRUE.toString() );
                props.put( "mail" + protocol + ".starttls.required", Boolean.TRUE.toString() );
            }
            
            // Get a Session object
            Session session = Session.getInstance( props, null );

            PasswordAuthentication passwordAuthentication = new PasswordAuthentication( user, password );
            String urlNameString = protocol + "://" + host;
            URLName urlName = new URLName( urlNameString );

            session.setPasswordAuthentication( urlName, passwordAuthentication );

            return session;
        }

        return null;
    } // connect

    /**
     * 
     */
    public void run() {

        try { // check mail
            while ( getStatus().getValue() >= BeanStatus.STARTED.getValue() ) {
                try {
                    // Wait predefined interval
                    Thread.sleep( pollingInterval );
                } catch ( InterruptedException iEx ) {
                    LOG.info( "Email polling interrupted." );
                }

                // Get new messages
                poll();

            }
        } catch ( Exception ioe ) {
            LOG.error( "Email polling was interrupted!" );
            if ( LOG.isDebugEnabled() ) {
                ioe.printStackTrace();
            }
            // Ignore errors here, intentionally interupted
        }
    } // run

    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#getTransportReceiver()
     */
    public TransportReceiver getTransportReceiver() {

        return transportReceiver;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#setTransportReceiver(org.nexuse2e.transport.TransportReceiver)
     */
    public void setTransportReceiver( TransportReceiver transportReceiver ) {

        this.transportReceiver = transportReceiver;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        super.teardown();

        transportReceiver = null;
    } // teardown

}
