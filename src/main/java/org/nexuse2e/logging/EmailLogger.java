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

package org.nexuse2e.logging;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.service.Service;
import org.nexuse2e.service.mail.SmtpSender;

/**
 * @author mbreilmann
 *
 */
public class EmailLogger extends AbstractLogger {

    private static Logger       LOG                 = Logger.getLogger( EmailLogger.class );
    private static final String SERVICE_PARAM_NAME  = "service";
    private static final String RECIPIENT_PARAM     = "recipient";
    private static final String SUBJECT_PARAM       = "subject";

    private Service             service             = null;
    private String              serviceName         = null;
    private String              recipient           = null;
    private String              subject             = null;
    private EngineConfiguration engineConfiguration = null;

    /**
     * Default constructor.
     */
    public EmailLogger() {

        parameters = new HashMap<String, Object>();
        parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
        parameterMap.put( SERVICE_PARAM_NAME, new ParameterDescriptor( ParameterType.SERVICE, "Service",
                "The name of the SMTP service that shall be used by the sender", SmtpSender.class ) );
        parameterMap.put( RECIPIENT_PARAM, new ParameterDescriptor( ParameterType.STRING, "Recipient",
                "The recipient(s) of the email", "" ) );
        parameterMap.put( SUBJECT_PARAM, new ParameterDescriptor( ParameterType.STRING, "Subject",
                "The subject line of the email", "" ) );
        status = BeanStatus.INSTANTIATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) {

        String serviceName = getParameter( SERVICE_PARAM_NAME );
        String recipient = getParameter( RECIPIENT_PARAM );
        String subject = getParameter( SUBJECT_PARAM );

        if ( ( serviceName == null ) && ( serviceName.trim().length() == 0 ) ) {
            LOG.error( "EmailLogger.initialize(): Service name not specified. Please check your configuration" );
            return;
        }

        if ( ( recipient == null ) || ( recipient.length() == 0 ) ) {
            LOG.error( "EmailLogger.initialize(): Recipient(s) not specified. Please check your configuration" );
            return;
        }
        if ( ( subject == null ) || ( subject.length() == 0 ) ) {
            LOG.error( "EmailLogger.initialize(): Subject not specified. Please check your configuration" );
            return;
        }

        this.serviceName = serviceName;
        this.recipient = recipient;
        this.subject = subject;
        engineConfiguration = config;

        status = BeanStatus.INITIALIZED;
    }

    private SmtpSender findService() {

        SmtpSender smtpSender = null;

        if ( service != null ) {
            return (SmtpSender) service;
        } else if ( serviceName != null && serviceName.trim().length() > 0 ) {
            Service service = engineConfiguration.getStaticBeanContainer().getService( serviceName );
            if ( service == null ) {
                LOG.error( "EmailLogger.initialize(): Service \"" + serviceName
                        + "\" not found. Please check your configuration" );
                return null;
            }
            if ( !( service instanceof SmtpSender ) ) {
                LOG.error( "EmailLogger.initialize(): Service \"" + serviceName
                        + "\" not of type SmtpSender. Please check your configuration" );
                return null;
            }
            smtpSender = (SmtpSender) service;
            this.service = service;
        } else {
            LOG.error( "No SMTP service found. Please check your configuration" );
            return null;
        }

        return smtpSender;
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    protected void append( LoggingEvent loggingEvent ) {

        if ( status != BeanStatus.ACTIVATED ) {
            System.err.println( "EmailLogger not in correct state to process log event: " + status );
            return;
        }

        //        System.out.println( "*** loggingEvent.getLevel(): " + loggingEvent.getLevel() );
        //        System.out.println( "*** getLogThreshold()      : " + getLogThreshold() );
        //        System.out.println( "*** Level.toLevel()        : " + Level.toLevel( getLogThreshold(), Level.ERROR ) );

        if ( !loggingEvent.getLevel().isGreaterOrEqual( Level.toLevel( getLogThreshold(), Level.ERROR ) ) ) {
            return;
        }

        SmtpSender smtpSender = findService();
        if ( smtpSender != null ) {
            try {
                smtpSender.sendMessage( recipient, subject, loggingEvent.getRenderedMessage() );
            } catch ( NexusException e ) {
                System.err.println( "Error sending log email: " + e );
                e.printStackTrace();
            }
        } else {
            System.err.println( "SMTP service not available!" );
        }

    }

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#close()
     */
    @Override
    public void close() {

    }

} // EmailLogger
