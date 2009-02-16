package org.nexuse2e.patches;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.patch.Patch;
import org.nexuse2e.patch.PatchException;
import org.nexuse2e.patch.PatchReporter;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;


public class DatabaseReportingPerformance implements Patch {

    private PatchReporter reporter = null;
    
    @SuppressWarnings("unchecked")
    public void executePatch() throws PatchException {
        Session session = null;
       try {
           long min = -1;
           long max = -1;
           long start = -1;
           long end = -1;
           long dif = -1;
           long sum = 0;
           int count = 20;
           List<MessagePojo> msgs = null;
           List<ConversationPojo> convs = null;
           List<MessagePojo> repMsgs = null;
           
           session = Engine.getInstance().getTransactionDAO().getDBSession();
           session.setCacheMode( CacheMode.IGNORE );
           
           for(int i = 0; i < count; i++) {
               start = System.currentTimeMillis();
               msgs =  (List<MessagePojo>) session.createQuery( "select message from MessagePojo as message where (message.status = "
                       + Constants.MESSAGE_STATUS_RETRYING + " or message.status = " + Constants.MESSAGE_STATUS_QUEUED
                       + ") and message.outbound=true" ).list();
               end = System.currentTimeMillis();
               
               dif = end-start;
               sum += dif;
               max = dif > max ? dif : max;
               min = dif < min || min == -1 ? dif : min;
           }
           
           reporter.info( "("+min+" ms/"+(sum/count)+" ms/"+max+" ms) Active Messages: "+msgs.size() );
           
           sum = 0;
           min = -1;
           max = -1;
           Date endDate = null;
           
           Calendar c = Calendar.getInstance();
           c.set( 2008, 10, 25, 0, 0, 0 );
           
           endDate = c.getTime();
           reporter.info( "EndDate: "+endDate );
           for(int i = 0; i < count; i++) {
           
               start = System.currentTimeMillis();
               convs = Engine.getInstance().getTransactionDAO().getConversationsForReport( "9", 0,0, null, null, endDate, 100, 0, 0, true );
               end = System.currentTimeMillis();
               
               dif = end-start;
               sum += dif;
               max = dif > max ? dif : max;
               min = dif < min || min == -1 ? dif : min;
           }
           reporter.info(  "("+min+" ms/"+(sum/count)+" ms/"+max+" ms) Conversation Reporting: "+convs.size() );
           
           sum = 0;
           min = -1;
           max = -1;
           for(int i = 0; i < count; i++) {
               start = System.currentTimeMillis();
               
               Query query = session.createQuery( "select message from MessagePojo as message where message.status=3" );
               query.setFirstResult( 0 );
               query.setMaxResults( 100 );
               repMsgs = (List<MessagePojo>) query.list();
               
               Engine.getInstance().getTransactionDAO().getMessagesForReport( "3", 0,0, null, null, null,null,endDate, 100, 0, 0, true );
               end = System.currentTimeMillis();
               
               dif = end-start;
               sum += dif;
               max = dif > max ? dif : max;
               min = dif < min || min == -1 ? dif : min;
           }
           
           reporter.info(  "("+min+" ms/"+(sum/count)+" ms/"+max+" ms) Message Reporting: "+repMsgs.size() );
           
           
           
           
           
           
        } catch ( NexusException e ) {
            e.printStackTrace();
        }finally {
            if(session != null) {
                try {
                    Engine.getInstance().getTransactionDAO().releaseDBSession( session );
                } catch ( NexusException e ) {
                    e.printStackTrace();
                }
            }
            
        }
       
    }

    public String getPatchDescription() {

        return "simple performance test, uses some reporting queries";
    }

    public String getPatchName() {

        return "Database Performance Test";
    }

    public String getVersionInformation() {

        return "0.1";
    }

    public boolean isExecutedSuccessfully() {

        return true;
    }

    public void setPatchReporter( PatchReporter patchReporter ) {

        reporter = patchReporter;

    }

}
