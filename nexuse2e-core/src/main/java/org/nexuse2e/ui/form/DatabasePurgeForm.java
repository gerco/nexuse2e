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
package org.nexuse2e.ui.form;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class DatabasePurgeForm extends ActionForm {

    private static Logger     LOG              = Logger.getLogger( DatabasePurgeForm.class );
    /**
     * 
     */
    private static final long serialVersionUID = 4372214059774003553L;

    /**
     * valid values: select, preview, remove
     */
    private String            type             = "select";

    private boolean           purgeMessages    = false;
    private boolean           purgeLog         = false;
    private String            startYear        = null;
    private String            startMonth       = null;
    private String            startDay         = null;
    private String            startHour        = null;
    private String            startMin         = null;
    private String            endYear          = null;
    private String            endMonth         = null;
    private String            endDay           = null;
    private String            endHour          = null;
    private String            endMin           = null;
    private boolean           startEnabled     = false;
    private boolean           endEnabled       = false;

    
    private int               convCount        = 0;
    private int               messageCount     = 0;
    private int               logEntryCount    = 0;

    public DatabasePurgeForm() {

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.add( Calendar.WEEK_OF_YEAR, -24 );
        end.add( Calendar.WEEK_OF_YEAR, -12 );
        //        end.setTime( start.getTime() + ( 24 * 60 * 60 * 1000 ) );

        LOG.debug( "start.get(Calendar.YEAR): " + start.get( Calendar.YEAR ) );
        setStartYear( "" + ( start.get( Calendar.YEAR ) ) );
        setEndYear( "" + ( end.get( Calendar.YEAR ) ) );
        if ( ( start.get( Calendar.MONTH ) + 1 ) < 10 ) {
            setStartMonth( "0" + ( start.get( Calendar.MONTH ) + 1 ) );
        } else {
            setStartMonth( "" + ( start.get( Calendar.MONTH ) + 1 ) );
        }
        if ( ( end.get( Calendar.MONTH ) + 1 ) < 10 ) {
            setEndMonth( "0" + ( end.get( Calendar.MONTH ) + 1 ) );
        } else {
            setEndMonth( "" + ( end.get( Calendar.MONTH ) + 1 ) );
        }
        if ( start.get( Calendar.DAY_OF_MONTH ) < 10 ) {
            setStartDay( "0" + start.get( Calendar.DAY_OF_MONTH ) );

        } else {
            setStartDay( "" + start.get( Calendar.DAY_OF_MONTH ) );

        }
        if ( end.get( Calendar.DAY_OF_MONTH ) < 10 ) {
            setEndDay( "0" + end.get( Calendar.DAY_OF_MONTH ) );
        } else {
            setEndDay( "" + end.get( Calendar.DAY_OF_MONTH ) );
        }
        setStartHour( "00" );
        setEndHour( "00" );

        setStartMin( "00" );
        setEndMin( "00" );
    }

    
    /**
     * @param mapping
     * @param request
     */
    @Override
    public void reset( ActionMapping mapping, HttpServletRequest request ) {
        setStartEnabled( false );
        setEndEnabled( false );
        setPurgeLog( false );
        setPurgeMessages( false );
    }

    /**
     * 
     */
    public void cleanSettings() {

        setStartEnabled( false );
        setEndEnabled( false );
        setPurgeLog( false );
        setPurgeMessages( false );

    }
    
    
    /**
     * @return the endDay
     */
    public String getEndDay() {

        return endDay;
    }

    /**
     * @param endDay the endDay to set
     */
    public void setEndDay( String endDay ) {

        this.endDay = endDay;
    }

    /**
     * @return the endEnabled
     */
    public boolean isEndEnabled() {

        return endEnabled;
    }

    /**
     * @param endEnabled the endEnabled to set
     */
    public void setEndEnabled( boolean endEnabled ) {

        this.endEnabled = endEnabled;
    }

    /**
     * @return the endHour
     */
    public String getEndHour() {

        return endHour;
    }

    /**
     * @param endHour the endHour to set
     */
    public void setEndHour( String endHour ) {

        this.endHour = endHour;
    }

    /**
     * @return the endMin
     */
    public String getEndMin() {

        return endMin;
    }

    /**
     * @param endMin the endMin to set
     */
    public void setEndMin( String endMin ) {

        this.endMin = endMin;
    }

    /**
     * @return the endMonth
     */
    public String getEndMonth() {

        return endMonth;
    }

    /**
     * @param endMonth the endMonth to set
     */
    public void setEndMonth( String endMonth ) {

        this.endMonth = endMonth;
    }

    /**
     * @return the endYear
     */
    public String getEndYear() {

        return endYear;
    }

    /**
     * @param endYear the endYear to set
     */
    public void setEndYear( String endYear ) {

        this.endYear = endYear;
    }

    /**
     * @return the purgeLog
     */
    public boolean isPurgeLog() {

        return purgeLog;
    }

    /**
     * @param purgeLog the purgeLog to set
     */
    public void setPurgeLog( boolean purgeLog ) {

        this.purgeLog = purgeLog;
    }

    /**
     * @return the purgeMessages
     */
    public boolean isPurgeMessages() {

        return purgeMessages;
    }

    /**
     * @param purgeMessages the purgeMessages to set
     */
    public void setPurgeMessages( boolean purgeMessages ) {

        this.purgeMessages = purgeMessages;
    }

    /**
     * @return the startDay
     */
    public String getStartDay() {

        return startDay;
    }

    /**
     * @param startDay the startDay to set
     */
    public void setStartDay( String startDay ) {

        this.startDay = startDay;
    }

    /**
     * @return the startEnabled
     */
    public boolean isStartEnabled() {

        return startEnabled;
    }

    /**
     * @param startEnabled the startEnabled to set
     */
    public void setStartEnabled( boolean startEnabled ) {

        this.startEnabled = startEnabled;
    }

    /**
     * @return the startHour
     */
    public String getStartHour() {

        return startHour;
    }

    /**
     * @param startHour the startHour to set
     */
    public void setStartHour( String startHour ) {

        this.startHour = startHour;
    }

    /**
     * @return the startMin
     */
    public String getStartMin() {

        return startMin;
    }

    /**
     * @param startMin the startMin to set
     */
    public void setStartMin( String startMin ) {

        this.startMin = startMin;
    }

    /**
     * @return the startMonth
     */
    public String getStartMonth() {

        return startMonth;
    }

    /**
     * @param startMonth the startMonth to set
     */
    public void setStartMonth( String startMonth ) {

        this.startMonth = startMonth;
    }

    /**
     * @return the startYear
     */
    public String getStartYear() {

        return startYear;
    }

    /**
     * @param startYear the startYear to set
     */
    public void setStartYear( String startYear ) {

        this.startYear = startYear;
    }

    /**
     * @return the type
     */
    public String getType() {

        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType( String type ) {

        this.type = type;
    }

    /**
     * @return the convCount
     */
    public int getConvCount() {

        return convCount;
    }

    /**
     * @param convCount the convCount to set
     */
    public void setConvCount( int convCount ) {

        this.convCount = convCount;
    }

    /**
     * @return the logEntryCount
     */
    public int getLogEntryCount() {

        return logEntryCount;
    }

    /**
     * @param logEntryCount the logEntryCount to set
     */
    public void setLogEntryCount( int logEntryCount ) {

        this.logEntryCount = logEntryCount;
    }

    /**
     * @return the messageCount
     */
    public int getMessageCount() {

        return messageCount;
    }

    /**
     * @param messageCount the messageCount to set
     */
    public void setMessageCount( int messageCount ) {

        this.messageCount = messageCount;
    }

}
