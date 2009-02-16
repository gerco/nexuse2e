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
    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    public abstract List<LogPojo> getLog() throws NexusException; // getLog

}