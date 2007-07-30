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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.service.Service;
import org.nexuse2e.service.mail.SmtpSender;

/**
 * @author mbreilmann
 *
 */
public class EmailLogger extends AppenderSkeleton implements LogAppender {

    private static Logger                    LOG                 = Logger.getLogger( EmailLogger.class );
    private static final String              SERVICE_PARAM_NAME  = "service";
    private static final String              RECIPIENT_PARAM     = "recipient";
    private static final String              SUBJECT_PARAM       = "subject";

    private Map<String, Object>              parameters          = null;
    private Map<String, ParameterDescriptor> parameterMap        = null;
    private BeanStatus                       status              = BeanStatus.UNDEFINED;
    private int                              logThreshold        = 0;
    private List<Logger>                     loggers             = null;
    private Service                          service             = null;
    private String                           serviceName         = null;
    private String                           recipient           = null;
    private String                           subject             = null;
    private EngineConfiguration              engineConfiguration = null;

    /**
     * Default constructor.
     */
    public EmailLogger() {

        parameters = new HashMap<String, Object>();
        parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
        parameterMap.put( SERVICE_PARAM_NAME, new ParameterDescriptor( ParameterType.SERVICE, "Service",
                "The name of the SMTP service that shall be used by the sender", "" ) );
        parameterMap.put( RECIPIENT_PARAM, new ParameterDescriptor( ParameterType.STRING, "Recipient",
                "The recipient(s) of the email", "" ) );
        parameterMap.put( SUBJECT_PARAM, new ParameterDescriptor( ParameterType.STRING, "Subject",
                "The subject line of the email", "" ) );
        status = BeanStatus.INSTANTIATED;
        loggers = new ArrayList<Logger>();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.logging.LogAppender#deregisterLoggers()
     */
    public void deregisterLoggers() {

        for ( Logger logger : loggers ) {
            logger.removeAppender( this );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.logging.LogAppender#getLogThreshold()
     */
    public int getLogThreshold() {

        return logThreshold;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.logging.LogAppender#registerLogger(org.apache.log4j.Logger)
     */
    public void registerLogger( Logger logger ) {

        if ( loggers != null ) {
            loggers.add( logger );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.logging.LogAppender#setLogThreshold(int)
     */
    public void setLogThreshold( int threshold ) {

        this.logThreshold = threshold;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameter(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter( String name ) {

        return (T) parameters.get( name );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameters()
     */
    public Map<String, Object> getParameters() {

        return Collections.unmodifiableMap( parameters );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        return parameterMap;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#activate()
     */
    public void activate() {

        status = BeanStatus.ACTIVATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#deactivate()
     */
    public void deactivate() {

        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getActivationRunlevel()
     */
    public Layer getActivationLayer() {

        return Layer.CONFIGURATION;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public BeanStatus getStatus() {

        return status;
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
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        deregisterLoggers();
        loggers.clear();
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    protected void append( LoggingEvent loggingEvent ) {

        if ( status != BeanStatus.ACTIVATED ) {
            LOG.error( "EmailLogger not in correct state to process log event: " + status );
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
                LOG.error( "Error sending log email: " + e );
                e.printStackTrace();
            }
        } else {
            LOG.error( "SMTP service not available!" );
        }

    }

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#close()
     */
    @Override
    public void close() {

        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
     */
    @Override
    public boolean requiresLayout() {

        return false;
    }

} // EmailLogger
