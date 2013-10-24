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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.eclipse.jetty.util.log.Log;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Version;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.controller.TransactionService;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.integration.NEXUSe2eInterfaceImpl;
import org.nexuse2e.integration.info.wsdl.Choreographies;
import org.nexuse2e.integration.info.wsdl.Conversation;
import org.nexuse2e.integration.info.wsdl.ConversationFilter;
import org.nexuse2e.integration.info.wsdl.ConversationStatus;
import org.nexuse2e.integration.info.wsdl.EngineStatus;
import org.nexuse2e.integration.info.wsdl.GetConversationLogMessageCount;
import org.nexuse2e.integration.info.wsdl.GetConversationLogMessageCountResponse;
import org.nexuse2e.integration.info.wsdl.GetConversationLogMessages;
import org.nexuse2e.integration.info.wsdl.GetEngineStatusResponse;
import org.nexuse2e.integration.info.wsdl.LogLevel;
import org.nexuse2e.integration.info.wsdl.LogMessages;
import org.nexuse2e.integration.info.wsdl.Message;
import org.nexuse2e.integration.info.wsdl.MessageFilter;
import org.nexuse2e.integration.info.wsdl.MessagePayload;
import org.nexuse2e.integration.info.wsdl.MessagePayloads;
import org.nexuse2e.integration.info.wsdl.MessageStatus;
import org.nexuse2e.integration.info.wsdl.MessageType;
import org.nexuse2e.integration.info.wsdl.Messages;
import org.nexuse2e.integration.info.wsdl.NEXUSe2EInfo;
import org.nexuse2e.integration.info.wsdl.NexusUptime;
import org.nexuse2e.integration.info.wsdl.NexusVersion;
import org.nexuse2e.integration.info.wsdl.Partner;
import org.nexuse2e.integration.info.wsdl.Partners;
import org.nexuse2e.integration.info.wsdl.RestartEngineResponse;
import org.nexuse2e.integration.info.wsdl.RestartEngineResult;
import org.nexuse2e.integration.info.wsdl.StatisticsItem;
import org.nexuse2e.integration.info.wsdl.StatisticsResponse;
import org.nexuse2e.integration.info.wsdl.Trp;
import org.nexuse2e.integration.info.wsdl.Uptime;
import org.nexuse2e.integration.info.wsdl.LogMessageCounts.LogMessageCount;
import org.nexuse2e.integration.info.wsdl.LogMessages.LogMessage;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.MessageHandlingCenter;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * Implementation for NEXUSe2e info web service.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class NEXUSe2eInfoServiceImpl implements NEXUSe2EInfo {

    private static final Comparator<String> STRING_COMPARATOR = new Comparator<String>(){
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    };
    
    
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
        long dayLength = 1000*60*60*24;
        long hourlength = 1000*60*60;
        long minutelength = 1000*60;
        long secondlength = 1000;
        
        
        long days = millis / dayLength;
        long hours = (millis - (days*dayLength)) / hourlength;
        long minutes = (millis - (days*dayLength) - (hours*hourlength)) / minutelength;
        long seconds = (millis - (days*dayLength) - (hours*hourlength) - (minutes*minutelength)) / secondlength;
        Uptime uptime = new Uptime();
        uptime.setHours( (int) hours );
        uptime.setMinutes( (int) minutes );
        uptime.setSeconds( (int) seconds );
        uptime.setDays( (int) days );
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
                try {
                    count.setLogLevel( LogLevel.fromValue( level.toString() ) );
                } catch (IllegalArgumentException ignored) {
                    count.setLogLevel( LogLevel.INFO );
                }
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
                    try {
                        m.setLogLevel( LogLevel.fromValue( Level.toLevel( logPojo.getSeverity() ).toString() ) );
                    } catch (IllegalArgumentException ignored) {
                        m.setLogLevel( LogLevel.INFO );
                    }
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

    public Choreographies getChoreographies( Object getChoreographies ) {
        Choreographies c = new Choreographies();
        List<ChoreographyPojo> choreos = Engine.getInstance().getCurrentConfiguration().getChoreographies();
        List<String> result = c.getChoreography();
        if (choreos != null) {
            for (ChoreographyPojo cp : choreos) {
                result.add( cp.getName() );
            }
        }
        Collections.sort(result, STRING_COMPARATOR);
        return c;
    }

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

    public Partners getPartners(Object getPartners) {
        Partners p = new Partners();
        try {
            Comparator<PartnerPojo> comp = new GenericComparator<PartnerPojo>("name", true);
            List<PartnerPojo> partners = Engine.getInstance().getCurrentConfiguration().getPartners(
                    org.nexuse2e.configuration.Constants.PARTNER_TYPE_PARTNER, comp);
            List<Partner> result = p.getPartner();
            if (partners != null) {
                for (PartnerPojo pp : partners) {
                    Partner partner = new Partner();
                    partner.setId(pp.getPartnerId());
                    partner.setValue(pp.getName());
                    result.add(partner);
                }
            }
        } catch (NexusException e) {
            e.printStackTrace();
        }
        return p;
    }

    public Partners getLocalPartners(Object getPartners) {
        Partners p = new Partners();
        try {
            Comparator<PartnerPojo> comp = new GenericComparator<PartnerPojo>("name", true);
            List<PartnerPojo> partners = Engine.getInstance().getCurrentConfiguration().getPartners(
                    org.nexuse2e.configuration.Constants.PARTNER_TYPE_LOCAL, comp);
            List<Partner> result = p.getPartner();
            if (partners != null) {
                for (PartnerPojo pp : partners) {
                    Partner partner = new Partner();
                    partner.setId(pp.getPartnerId());
                    partner.setValue(pp.getName());
                    result.add(partner);
                }
            }
        } catch (NexusException e) {
            e.printStackTrace();
        }
        return p;
    }

    public List<Partner> getParticipants( String choreography ) {
        List<Partner> result = new ArrayList<Partner>();
        try {
            ChoreographyPojo cp = Engine.getInstance().getCurrentConfiguration().getChoreographyByChoreographyId( choreography );
            if (cp != null) {
                for (ParticipantPojo participant : cp.getParticipants()) {
                    if (participant != null && participant.getPartner() != null && participant.getPartner().getPartnerId() != null) {
                        Partner partner = new Partner();
                        partner.setId(participant.getPartner().getPartnerId());
                        partner.setValue(participant.getPartner().getName());
                        result.add(partner);
                    }
                }
                Collections.sort(result, new GenericComparator<Partner>("value", true));
            }
        } catch (NexusException nex) {
            nex.printStackTrace();
        }
        return result;
    }

    public String sendNewStringMessage( String choreographyId,
            String businessPartnerId, String actionId, String payload ) {

        try {
            return new NEXUSe2eInterfaceImpl().sendNewStringMessage(choreographyId, businessPartnerId, actionId, null, payload);
        } catch (NexusException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public GetEngineStatusResponse getEngineStatus( Object getEngineStatus ) {
        
        GetEngineStatusResponse response = new GetEngineStatusResponse();
        response.setStatus(EngineStatus.valueOf(Engine.getInstance().getStatus().toString()));
        return response;
    }

    public RestartEngineResponse restartEngine( Object restartEngine ) {
        RestartEngineResponse response = new RestartEngineResponse();
        
        new Thread() {
            public void run() {
                try {
                    Engine.getInstance().changeStatus( BeanStatus.INSTANTIATED );
                    Engine.getInstance().changeStatus( BeanStatus.STARTED );
                } catch (InstantiationException e) {
                    Log.warn(e);
                }
            }
        }.start();
        response.setResult(RestartEngineResult.SUCCESS);

        return response;
    }

    private static XMLGregorianCalendar date(Date date) throws DatatypeConfigurationException {
        if (date == null) {
            return null;
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    }
    
    private static Messages convert(List<MessagePojo> msgs, boolean includePayload, boolean includeOptionalAttributes)
    throws DatatypeConfigurationException {
        Messages messages = new Messages();
        
        for (MessagePojo mp : msgs) {
            Message message = new Message();
            messages.getMessage().add(message);
            message.setActionId(mp.getAction().getName());
            ParticipantPojo localPartner = Engine.getInstance().getCurrentConfiguration().getParticipantFromChoreographyByPartner(
                    mp.getConversation().getChoreography(), mp.getConversation().getPartner());
            if (localPartner != null && localPartner.getLocalPartner() != null && localPartner.getLocalPartner().getPartnerId() != null) {
                message.setLocalPartnerId(localPartner.getLocalPartner().getPartnerId());
            }
            message.setChoreographyId(mp.getAction().getChoreography().getName());
            message.setCreatedDate(date(mp.getCreatedDate()));
            message.setId(mp.getMessageId());
            message.setModifiedDate(date(mp.getModifiedDate()));
            message.setOutbound(mp.isOutbound());
            message.setRetries(mp.getRetries());
            if (includeOptionalAttributes) {
                message.setConversationId(mp.getConversation().getConversationId());
                message.setBusinessPartnerId(mp.getParticipant().getPartner().getPartnerId());
                message.setChoreographyId(mp.getAction().getChoreography().getName());
            }
            MessageStatus status = MessageStatus.UNKNOWN;
            try {
                status = MessageStatus.fromValue(MessagePojo.getStatusName(mp.getStatus()));
            } catch (IllegalArgumentException ignored) {
            }
            message.setStatus(status == null ? MessageStatus.UNKNOWN : status);
            Trp trp = new Trp();
            trp.setProtocol(mp.getTRP().getProtocol());
            trp.setTransport(mp.getTRP().getTransport());
            trp.setVersion(mp.getTRP().getVersion());
            message.setTrp(trp);
            MessageType type;
            // cannot use getTypeName, which returns human-readable string
            switch (mp.getType()) {
            case org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK:
                type = MessageType.ACK;
                break;
            case org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL:
                type = MessageType.NORMAL;
                break;
            case org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR:
                type = MessageType.ERROR;
                break;
            default:
                type = MessageType.UNKNOWN;
            }
            message.setType(type);
            MessagePayloads payloads = new MessagePayloads();
            for (MessagePayloadPojo mpp : mp.getMessagePayloads()) {
                MessagePayload payload = new MessagePayload();
                payload.setCharset(mpp.getCharset());
                payload.setId(mpp.getContentId());
                payload.setPayloadIncluded(includePayload);
                payload.setSequenceNumber(mpp.getSequenceNumber());
                if (includePayload && mpp.getPayloadData() != null) {
                    payload.setValue(mpp.getPayloadData());
                    payload.setSize(mpp.getPayloadData().length);
                } else {
                    payload.setSize(null);
                }
                payloads.getPayload().add(payload);
            }
            message.setPayloads(payloads);
        }
        
        return messages;
    }
    
    private static List<Conversation> convert(List<ConversationPojo> cps) throws DatatypeConfigurationException, NexusException {
        if (cps == null || cps.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Conversation> l = new ArrayList<Conversation>();
        for (ConversationPojo cp : cps) {
            Conversation c = new Conversation();
            c.setBusinessPartnerId(cp.getPartner().getPartnerId());
            c.setChoreographyId(cp.getChoreography().getName());
            c.setCreatedDate(date(cp.getCreatedDate()));
            c.setCurrentActionId((cp.getCurrentAction() == null ? null : cp.getCurrentAction().getName()));
            c.setId(cp.getConversationId());
            Messages messages = convert(Engine.getInstance().getTransactionService().getMessagesFromConversation(cp), false, false);
            c.setMessages(messages);
            c.setModifiedDate(date(cp.getModifiedDate()));
            ParticipantPojo localPartner = Engine.getInstance().getCurrentConfiguration().getParticipantFromChoreographyByPartner(cp.getChoreography(), cp.getPartner());
            if (localPartner != null && localPartner.getLocalPartner() != null && localPartner.getLocalPartner().getPartnerId() != null) {
                c.setLocalPartnerId(localPartner.getLocalPartner().getPartnerId());
            }
            ConversationStatus status = ConversationStatus.UNKNOWN;
            try {
                status = ConversationStatus.fromValue(ConversationPojo.getStatusName(cp.getStatus()));
            } catch (IllegalArgumentException ignored) {
            }
            c.setStatus(status == null ? ConversationStatus.UNKNOWN : status);
            l.add(c);
        }
        return l;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.integration.info.wsdl.NEXUSe2EInfo#getConversationCount(org.nexuse2e.integration.info.wsdl.ConversationFilter)
     */
    public int getConversationCount(ConversationFilter conversationFilter) {
        int count = 0;
        try {
            ConversationFilterParams cfp = new ConversationFilterParams(conversationFilter);
            count = Engine.getInstance().getTransactionService().getConversationsCount(
                    cfp.status, cfp.nxChoreographyId, cfp.nxPartnerId, cfp.conversationId, cfp.afterDate, cfp.beforeDate, 0, cfp.asc);
        } catch (NexusException nex) {
            nex.printStackTrace();
        }
        return count;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.info.wsdl.NEXUSe2EInfo#getConversations(org.nexuse2e.integration.info.wsdl.ConversationFilter)
     */
    public List<Conversation> getConversations(ConversationFilter conversationFilter) {
        try {
            ConversationFilterParams cfp = new ConversationFilterParams(conversationFilter);
            List<ConversationPojo> cp = Engine.getInstance().getTransactionService().getConversationsForReport(
                    cfp.status, cfp.nxChoreographyId, cfp.nxPartnerId, cfp.conversationId, cfp.afterDate, cfp.beforeDate, cfp.itemsPerPage, cfp.page, TransactionDAO.SORT_CREATED, cfp.asc);
            return convert(cp);
        } catch (NexusException nex) {
            nex.printStackTrace();
        } catch (DatatypeConfigurationException dcex) {
            dcex.printStackTrace();
        }
        return Collections.emptyList();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.info.wsdl.NEXUSe2EInfo#getMessageCount(org.nexuse2e.integration.info.wsdl.MessageFilter)
     */
    public int getMessageCount(MessageFilter messageFilter) {
        int count = 0;
        try {
            MessageFilterParams cfp = new MessageFilterParams(messageFilter);
            count = Engine.getInstance().getTransactionService().getMessagesCount(
                    cfp.status, cfp.nxChoreographyId, cfp.nxPartnerId, cfp.conversationId, cfp.messageId, cfp.afterDate, cfp.beforeDate);
        } catch (NexusException nex) {
            nex.printStackTrace();
        }
        return count;
    }

    public Messages getMessages(MessageFilter messageFilter) {
        try {
            MessageFilterParams cfp = new MessageFilterParams(messageFilter);
            List<MessagePojo> mp = Engine.getInstance().getTransactionService().getMessagesForReport(
                    cfp.status, cfp.nxChoreographyId, cfp.nxPartnerId, cfp.conversationId, cfp.messageId, null, cfp.afterDate, cfp.beforeDate, cfp.itemsPerPage, cfp.page, TransactionDAO.SORT_CREATED, cfp.asc);
            
            return convert(mp, false, true);
        } catch (NexusException nex) {
            nex.printStackTrace();
        } catch (DatatypeConfigurationException dcex) {
            dcex.printStackTrace();
        }
        return new Messages();
    }

    public GetConversationLogMessageCountResponse getConversationLogMessageCount(GetConversationLogMessageCount getConversationLogMessageCount) {
        GetConversationLogMessageCountResponse resp = new GetConversationLogMessageCountResponse();
        try {
            resp.setCount(Engine.getInstance().getTransactionService().getLogEntriesForReportCount(
                    null, getConversationLogMessageCount.getConversationId(), getConversationLogMessageCount.getMessageId()));
        } catch (NexusException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public LogMessages getConversationLogMessages(GetConversationLogMessages getConversationLogMessages) {
        LogMessages lm = null;
        try {
            int itemsPerPage = 100;
            int page = 0;
            if (getConversationLogMessages.getItemsPerPage() != null && getConversationLogMessages.getItemsPerPage().getValue() != null) {
                itemsPerPage = getConversationLogMessages.getItemsPerPage().getValue().intValue();
            }
            if (getConversationLogMessages.getPageNumber() != null && getConversationLogMessages.getPageNumber().getValue() != null) {
                page = getConversationLogMessages.getPageNumber().getValue().intValue();
            }
            List<LogPojo> entries = Engine.getInstance().getTransactionService().getLogEntriesForReport(
                    null, getConversationLogMessages.getConversationId(), getConversationLogMessages.getMessageId(), itemsPerPage, page, false);
            lm = new LogMessages();
            for (LogPojo logPojo : entries) {
                LogMessage m = new LogMessage();
                try {
                    m.setLogLevel(LogLevel.fromValue(Level.toLevel(logPojo.getSeverity()).toString()));
                } catch (IllegalArgumentException ignored) {
                    m.setLogLevel(LogLevel.INFO);
                }
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(logPojo.getCreatedDate());
                XMLGregorianCalendar xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
                m.setTimestamp(xc);
                m.setValue(logPojo.getDescription());
                lm.getLogMessage().add(m);
            }
        } catch (NexusException e) {
            e.printStackTrace();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return lm;
    }

    public void requeueMessage(String messageId, Holder<String> requeueResult, Holder<String> requeueResultMessage) {
        try {
            MessageContext messageContext = Engine.getInstance().getTransactionService().getMessageContext(messageId);
            if (messageContext != null) {
                MessageHandlingCenter.getInstance().requeueMessage(messageContext);
                requeueResult.value = "SUCCESS";
            } else {
                requeueResult.value = "NOT_FOUND";
                requeueResultMessage.value = "The message with ID " + messageId + " was not found";
            }
        } catch (NexusException e) {
            requeueResult.value = "ERROR";
            if (!StringUtils.isBlank(e.getMessage())) {
                requeueResultMessage.value = e.getMessage();
            } else {
                requeueResultMessage.value = e.getClass().getName();
            }
        }
    }

    public void stopMessage(String messageId, Holder<String> stopResult, Holder<String> stopResultMessage) {
        try {
            MessagePojo messagePojo = Engine.getInstance().getTransactionService().getMessage(messageId);
            if (messagePojo != null) {
                Engine.getInstance().getTransactionService().stopProcessingMessage(messageId);
                stopResult.value = "SUCCESS";
            } else {
                stopResult.value = "NOT_FOUND";
                stopResultMessage.value = "The message with ID " + messageId + " was not found";
            }
        } catch (NexusException e) {
            stopResult.value = "ERROR";
            if (!StringUtils.isBlank(e.getMessage())) {
                stopResultMessage.value = e.getMessage();
            } else {
                stopResultMessage.value = e.getClass().getName();
            }
        }
    }

    private static class MessageFilterParams {
        String status;
        String conversationId;
        int nxChoreographyId;
        int nxPartnerId;
        Date beforeDate;
        Date afterDate;
        boolean asc;
        String messageId;
        int itemsPerPage;
        int page;
        
        public MessageFilterParams(MessageFilter messageFilter) throws NexusException {
            status = null;
            if (messageFilter.getStatus() != null && messageFilter.getStatus().getValue() != null) {
                status = nullIfBlank(messageFilter.getStatus().getValue().value());
                if (status != null) {
                    try {
                        Field f = Constants.class.getField("MESSAGE_STATUS_" + status);
                        status = Integer.toString(f.getInt(null));
                    } catch (Exception e) {
                        status = null;
                    }
                }
            }
            conversationId = null;
            if (messageFilter.getConversationId() != null) {
                conversationId = nullIfBlank(messageFilter.getConversationId().getValue());
            }
            nxChoreographyId = 0;
            if (messageFilter.getChoreographyId() != null && messageFilter.getChoreographyId().getValue() != null) {
                ChoreographyPojo cp = Engine.getInstance().getCurrentConfiguration().getChoreographyByChoreographyId(messageFilter.getChoreographyId().getValue());
                if (cp != null) {
                    nxChoreographyId = cp.getNxChoreographyId();
                } else {
                    nxChoreographyId = Integer.MAX_VALUE; // assume this one doesn't exist
                }
            }
            
            nxPartnerId = 0;
            if (messageFilter.getBusinessPartnerId() != null && messageFilter.getBusinessPartnerId().getValue() != null) {
                PartnerPojo pp = Engine.getInstance().getCurrentConfiguration().getPartnerByPartnerId(messageFilter.getBusinessPartnerId().getValue());
                if (pp != null) {
                    nxPartnerId = pp.getNxPartnerId();
                } else {
                    nxPartnerId = Integer.MAX_VALUE; // assume this one doesn't exist
                }
            }

            afterDate = null;
            if (messageFilter.getAfterDate() != null && messageFilter.getAfterDate().getValue() != null) {
                afterDate = messageFilter.getAfterDate().getValue().toGregorianCalendar().getTime();
            }
            beforeDate = null;
            if (messageFilter.getBeforeDate() != null && messageFilter.getBeforeDate().getValue() != null) {
                beforeDate = messageFilter.getBeforeDate().getValue().toGregorianCalendar().getTime();
            }
            asc = false;
            if (messageFilter.getAscending() != null && messageFilter.getAscending().getValue() != null) {
                asc = messageFilter.getAscending().getValue();
            }
            
            itemsPerPage = 10;
            if (messageFilter.getItemsPerPage() != null && messageFilter.getItemsPerPage().getValue() != null) {
                itemsPerPage = messageFilter.getItemsPerPage().getValue();
            }
            
            page = 0;
            if (messageFilter.getPageNumber() != null && messageFilter.getPageNumber().getValue() != null) {
                page = messageFilter.getPageNumber().getValue();
            }
            messageId = null;
            if (messageFilter.getMessageId() != null) {
                messageId = messageFilter.getMessageId().getValue();
            }
        }
        
        private static String nullIfBlank(String s) {
            return (StringUtils.isBlank(s) ? null : s);
        }
    }

    private static class ConversationFilterParams {
        String status;
        String conversationId;
        int nxChoreographyId;
        int nxPartnerId;
        Date beforeDate;
        Date afterDate;
        boolean asc;
        int itemsPerPage;
        int page;
        
        public ConversationFilterParams(ConversationFilter conversationFilter) throws NexusException {
            status = null;
            if (conversationFilter.getStatus() != null && conversationFilter.getStatus().getValue() != null) {
                status = nullIfBlank(conversationFilter.getStatus().getValue().value());
                if (status != null) {
                    try {
                        Field f = Constants.class.getField("CONVERSATION_STATUS_" + status);
                        status = Integer.toString(f.getInt(null));
                    } catch (Exception e) {
                        status = null;
                    }
                }
            }
            conversationId = null;
            if (conversationFilter.getConversationId() != null) {
                conversationId = nullIfBlank(conversationFilter.getConversationId().getValue());
            }
            nxChoreographyId = 0;
            if (conversationFilter.getChoreographyId() != null && conversationFilter.getChoreographyId().getValue() != null) {
                ChoreographyPojo cp = Engine.getInstance().getCurrentConfiguration().getChoreographyByChoreographyId(conversationFilter.getChoreographyId().getValue());
                if (cp != null) {
                    nxChoreographyId = cp.getNxChoreographyId();
                } else {
                    nxChoreographyId = Integer.MAX_VALUE; // assume this one doesn't exist
                }
            }
            
            nxPartnerId = 0;
            if (conversationFilter.getBusinessPartnerId() != null && conversationFilter.getBusinessPartnerId().getValue() != null) {
                PartnerPojo pp = Engine.getInstance().getCurrentConfiguration().getPartnerByPartnerId(conversationFilter.getBusinessPartnerId().getValue());
                if (pp != null) {
                    nxPartnerId = pp.getNxPartnerId();
                } else {
                    nxPartnerId = Integer.MAX_VALUE; // assume this one doesn't exist
                }
            }

            afterDate = null;
            if (conversationFilter.getAfterDate() != null && conversationFilter.getAfterDate().getValue() != null) {
                afterDate = conversationFilter.getAfterDate().getValue().toGregorianCalendar().getTime();
            }
            beforeDate = null;
            if (conversationFilter.getBeforeDate() != null && conversationFilter.getBeforeDate().getValue() != null) {
                beforeDate = conversationFilter.getBeforeDate().getValue().toGregorianCalendar().getTime();
            }
            asc = false;
            if (conversationFilter.getAscending() != null && conversationFilter.getAscending().getValue() != null) {
                asc = conversationFilter.getAscending().getValue();
            }
            
            itemsPerPage = 10;
            if (conversationFilter.getItemsPerPage() != null && conversationFilter.getItemsPerPage().getValue() != null) {
                itemsPerPage = conversationFilter.getItemsPerPage().getValue();
            }
            
            page = 0;
            if (conversationFilter.getPageNumber() != null && conversationFilter.getPageNumber().getValue() != null) {
                page = conversationFilter.getPageNumber().getValue();
            }
        }
        
        private static String nullIfBlank(String s) {
            return (StringUtils.isBlank(s) ? null : s);
        }
    }
}
