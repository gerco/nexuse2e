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
package org.nexuse2e.dao;

import java.util.Date;
import java.util.List;

import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.LogPojo;

public interface LogDAO {

    public static final int SORT_NONE        = 0;
    public static final int SORT_SEVERITY    = 1;
    public static final int SORT_DESCRIPTION = 2;
    public static final int SORT_CREATED     = 3;
    public static final int SORT_MODIFIED    = 4;
    public static final int SORT_METHODNAME  = 5;
    public static final int SORT_CLASSNAME   = 6;

    /**
     * @param log 
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public abstract void saveLog( LogPojo log ) throws NexusException;

    /**
     * @param severity
     * @param messageText
     * @param start
     * @param end
     * @param itemsPerPage
     * @param page
     * @param field
     * @param ascending
     * @return
     * @throws PersistenceException
     */
    public abstract List<LogPojo> getLogEntriesForReport( String severity, String messageText, Date start, Date end,
            int itemsPerPage, int page, int field, boolean ascending ) throws NexusException;

    /**
     * @param origin
     * @param severity
     * @param messageText
     * @param start
     * @param end
     * @param field
     * @param ascending
     * @return
     * @throws PersistenceException
     */
    public abstract int getLogEntriesForReportCount( String severity, String messageText, Date start, Date end,
            int field, boolean ascending ) throws NexusException;
    
    
    public abstract List<LogPojo> getLogEntriesForReport( String severity, String conversationId, String messageId, boolean ascending ) throws NexusException;

    public abstract List<LogPojo> getLog() throws NexusException; // getLog

}