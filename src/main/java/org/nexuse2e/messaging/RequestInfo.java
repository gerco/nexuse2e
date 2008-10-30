package org.nexuse2e.messaging;

import java.util.Date;

/**
 * This class contains meta-information about document requesting.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class RequestInfo {
    private String documentType;
    private String documentReference;
    private Date from;
    private Date to;
    private int maxMessages;
    
    /**
     * Minimal constructor.
     * @param documentType The document type requested. Should not be <code>null</code>.
     */
    public RequestInfo( String documentType ) {
        this( documentType, null, null, null, -1 );
    }
    
    /**
     * Full constructor.
     * @param documentType The document type requested. Should not be <code>null</code>.
     * @param documentReference The document reference ID. May be <code>null</code>.
     * @param from The earliest date to receive messages for. <code>null</code> means stone age.
     * @param to The latest date to receive messages for. <code>null</code> means Luke Skywalker's bithday.
     * @param maxMessages The maximum number of messages to receive. A value less than or equal to 0
     * means no limit.
     */
    public RequestInfo( String documentType, String documentReference, Date from, Date to, int maxMessages ) {
        this.documentType = documentType;
        this.documentReference = documentReference;
        this.from = from;
        this.to = to;
        this.maxMessages = maxMessages;
    }

    /**
     * Gets the requested document type.
     * @return The document type being requested.
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Gets the document reference.
     * @return The reference to another document whose response is requested now, or <code>null</code>.
     */
    public String getDocumentReference() {
        return documentReference;
    }

    /**
     * Gets the earliest date to receive messages for.
     * @return The earliest request data, or <code>null</code>.
     */
    public Date getFrom() {
        return from;
    }

    /**
     * Gets the latest date to receive messages for.
     * @return The latest date, or <code>null</code>.
     */
    public Date getTo() {
        return to;
    }

    /**
     * Gets the maximum number of requested messages.
     * @return The maximum message number. A value less than or equal to 0 indicates
     * that all available messages are requested.
     */
    public int getMaxMessages() {
        return maxMessages;
    }
}
