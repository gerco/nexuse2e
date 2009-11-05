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
package org.nexuse2e.integration.info;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Level;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Version;
import org.nexuse2e.controller.TransactionService;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.integration.NEXUSe2eInterfaceImpl;
import org.nexuse2e.integration.info.wsdl.Choreographies;
import org.nexuse2e.integration.info.wsdl.LogLevel;
import org.nexuse2e.integration.info.wsdl.NEXUSe2EInfo;
import org.nexuse2e.integration.info.wsdl.NexusUptime;
import org.nexuse2e.integration.info.wsdl.NexusVersion;
import org.nexuse2e.integration.info.wsdl.StatisticsItem;
import org.nexuse2e.integration.info.wsdl.StatisticsResponse;
import org.nexuse2e.integration.info.wsdl.Uptime;
import org.nexuse2e.integration.info.wsdl.LogMessageCounts.LogMessageCount;
import org.nexuse2e.integration.info.wsdl.LogMessages.LogMessage;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;

/**
 * Implementation for NEXUSe2e info web service.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class NEXUSe2eInfoServiceImpl implements NEXUSe2EInfo {

    private StatisticsResponse convertToStatisticsResponse( List<int[]> l, boolean conv ) {
        // convert to WS structure
        StatisticsResponse r = new StatisticsResponse();
        for (int[] kv : l) {
            StatisticsItem item = new StatisticsItem();
            item.setName( (conv ? ConversationPojo.getStatusName( kv[0] ) : MessagePojo.getStatusName( kv[0] )) );
            item.setQuantity( kv[1] );
            r.getItem().add( item );
        }
        return r;
    }
    
    public StatisticsResponse getConversationStatesLast24Hours( Object getConversationStatesLast24Hours ) {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, -1 );
        List<int[]> l = Engine.getInstance().getTransactionService().getConversationStatesSince( cal.getTime() );

        return convertToStatisticsResponse( l, true );
    }

    public StatisticsResponse getMessageStatesLast24Hours( Object getMessageStatesLast24Hours ) {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, -1 );
        List<int[]> l = Engine.getInstance().getTransactionService().getMessageStatesSince( cal.getTime() );

        return convertToStatisticsResponse( l, false );
    }

    public StatisticsResponse getMessagesPerChoreographyLast24Hours( Object getMessagesPerChoreographyLast24Hours ) {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, -1 );
        List<String[]> l = Engine.getInstance().getTransactionService().getMessagesPerConversationSince( cal.getTime() );

        StatisticsResponse r = new StatisticsResponse();
        for (String[] kv : l) {
            StatisticsItem item = new StatisticsItem();
            item.setName( kv[0] );
            try {
                item.setQuantity( Integer.parseInt( kv[1] ) );
            } catch (NumberFormatException nfe) { // should never happen
                nfe.printStackTrace();
            }
            r.getItem().add( item );
        }
        return r;
    }

    public StatisticsResponse getMessagesPerHourLast24Hours( Object getMessagesPerHourLast24Hours ) {
        List<int[]> l = Engine.getInstance().getTransactionService().getMessagesPerHourLast24Hours();

        StatisticsResponse r = new StatisticsResponse();
        for (int[] kv : l) {
            StatisticsItem item = new StatisticsItem();
            item.setName( Integer.toString( kv[0] ) );
            item.setQuantity( kv[1] );
            r.getItem().add( item );
        }
        return r;
    }

    private Uptime createUptime( long millis ) {
        int dayLength = 1000*60*60*24;
        int hourlength = 1000*60*60;
        int minutelength = 1000*60;
        int secondlength = 1000;
        
        
        int days = (int)millis / dayLength;
        int hours = (int)(millis - (days*dayLength)) / hourlength;
        int minutes = (int)(millis - (days*dayLength) - (hours*hourlength)) / minutelength;
        int seconds = (int)(millis - (days*dayLength) - (hours*hourlength) - (minutes*minutelength)) / secondlength;
        Uptime uptime = new Uptime();
        uptime.setHours( hours );
        uptime.setMinutes( minutes );
        uptime.setSeconds( seconds );
        return uptime;
    }
    
    public NexusUptime getNexusUptime(Object getNexusUptime) {
        NexusUptime uptime = new NexusUptime();
        
        long l = System.currentTimeMillis();
        long serviceUptime = l - Engine.getInstance().getServiceStartTime();
        long engineUptime = l - Engine.getInstance().getEngineStartTime();

        uptime.setServiceUptime( createUptime( serviceUptime ) );
        uptime.setEngineUptime( createUptime( engineUptime ) );
        return uptime;
    }

    public NexusVersion getNexusVersion(Object getNexusVersion) {
        NexusVersion version = new NexusVersion();
        version.setVersion( Version.getVersion() );
        String date = null;
        try {
            date = Version.getMainAttribute( Version.MainAttribute.ImplementationBuildDate );
            XMLGregorianCalendar cal = (date == null ? null : DatatypeFactory.newInstance().newXMLGregorianCalendar());
            version.setBuildDate( cal );
            version.setJavaVersion( System.getProperty( "java.version" ) );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return version;
    }

    public List<LogMessageCount> getLogMessageCounts( int pastMinutes, LogLevel minLogLevel, LogLevel maxLogLevel ) {
        TransactionService service = Engine.getInstance().getTransactionService();
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.MINUTE, -pastMinutes );
        Level min = (minLogLevel == null ? Level.ALL : Level.toLevel( minLogLevel.toString() ));
        Level max = (maxLogLevel == null ? Level.OFF : Level.toLevel( maxLogLevel.toString() ));
        Map<Level, Long> c = null;
        try {
            c = service.getLogCount( cal.getTime(), new Date(), min, max );
        } catch (NexusException nex) {
            nex.printStackTrace();
        }
        List<LogMessageCount> l = new ArrayList<LogMessageCount>( c == null ? 0 : c.size() );
        if (c != null) {
            for (Level level : c.keySet()) {
                LogMessageCount count = new LogMessageCount();
                count.setLogLevel( LogLevel.fromValue( level.toString() ) );
                count.setValue( c.get( level ).intValue() );
                l.add( count );
            }
        }
        return l;
    }

    public List<LogMessage> getLogMessages( int pastMinutes, Integer maximumMessages ) {
        TransactionService service = Engine.getInstance().getTransactionService();
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.MINUTE, -pastMinutes );
        List<LogPojo> l = null;
        try {
            l = service.getLogEntriesForReport(
                    null, null, cal.getTime(), new Date(),
                    (maximumMessages == null ? Integer.MAX_VALUE : maximumMessages),
                    0, LogDAO.SORT_CREATED, true );
        } catch (NexusException nex) {
            nex.printStackTrace();
        }
        List<LogMessage> result = new ArrayList<LogMessage>( l == null ? 0 : l.size() );
        try {
            if (l != null) {
                for (LogPojo logPojo : l) {
                    LogMessage m = new LogMessage();
                    m.setLogLevel( LogLevel.fromValue( Level.toLevel( logPojo.getSeverity() ).toString() ) );
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime( logPojo.getCreatedDate() );
                    XMLGregorianCalendar xc = DatatypeFactory.newInstance().newXMLGregorianCalendar( gc );
                    m.setTimestamp( xc );
                    m.setValue( logPojo.getDescription() );
                    result.add( m );
                }
            }
        } catch (DatatypeConfigurationException dtcex) {
            dtcex.printStackTrace();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.info.wsdl.NEXUSe2EInfo#getChoreographies(java.lang.Object)
     */
    public Choreographies getChoreographies( Object getChoreographies ) {
        Choreographies c = new Choreographies();
        List<ChoreographyPojo> choreos = Engine.getInstance().getCurrentConfiguration().getChoreographies();
        List<String> result = c.getChoreography();
        if (choreos != null) {
            for (ChoreographyPojo cp : choreos) {
                result.add( cp.getName() );
            }
        }
        return c;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.info.wsdl.NEXUSe2EInfo#getActions(java.lang.String)
     */
    public List<String> getActions( String choreography ) {
        List<String> result = new ArrayList<String>();
        try {
            ChoreographyPojo cp = Engine.getInstance().getCurrentConfiguration().getChoreographyByChoreographyId( choreography );
            if (cp != null) {
                for (ActionPojo action : cp.getActions()) {
                    if (action != null && action.getName() != null) {
                        result.add( action.getName() );
                    }
                }
            }
        } catch (NexusException nex) {
            nex.printStackTrace();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.info.wsdl.NEXUSe2EInfo#getParticipants(java.lang.String)
     */
    public List<String> getParticipants( String choreography ) {
        List<String> result = new ArrayList<String>();
        try {
            ChoreographyPojo cp = Engine.getInstance().getCurrentConfiguration().getChoreographyByChoreographyId( choreography );
            if (cp != null) {
                for (ParticipantPojo participant : cp.getParticipants()) {
                    if (participant != null && participant.getPartner() != null && participant.getPartner().getPartnerId() != null) {
                        result.add( participant.getPartner().getPartnerId() );
                    }
                }
            }
        } catch (NexusException nex) {
            nex.printStackTrace();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.info.wsdl.NEXUSe2EInfo#sendNewStringMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String sendNewStringMessage( String choreographyId,
            String businessPartnerId, String actionId, String payload ) {

        try {
            return new NEXUSe2eInterfaceImpl().sendNewStringMessage(choreographyId, businessPartnerId, actionId, null, payload);
        } catch (NexusException e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
