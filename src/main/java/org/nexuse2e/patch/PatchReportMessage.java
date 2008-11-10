package org.nexuse2e.patch;

/**
 * A single human-readable message that was created by a <code>Patch</code>.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PatchReportMessage {
    /**
     * This enumeration lists all types of messages that can be added to a
     * <code>PatchReport</code>.
     */
    public enum Type {
        DETAIL,
        INFO,
        WARNING,
        ERROR,
        FATAL
    }
    
    private String message;
    private Type type;

    /**
     * Constructs a new <code>PatchReportMessage</code>.
     * @param type The message type, must not be <code>null</code>.
     * @param message The message. Shall not be <code>null</code>.
     */
    public PatchReportMessage( Type type, String message ) {
        this.type = type;
        this.message = message;
    }
    
    /**
     * Gets the message type.
     * @return The type.
     */
    public Type getType() {
        return type;
    }
    
    /**
     * The message.
     * @return The message.
     */
    public String getMessage() {
        return message;
    }
}
