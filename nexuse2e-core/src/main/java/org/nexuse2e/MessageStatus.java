package org.nexuse2e;

public enum MessageStatus {
    FAILED(-1), UNKNOWN(0), RETRYING(1), QUEUED(2), SENT(3), STOPPED(4);

    int ordinal = 0;

    MessageStatus(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public static MessageStatus getByOrdinal(int ordinal) {
        if (0 <= ordinal) {
            for (MessageStatus oneType : MessageStatus.values()) {
                if (oneType.getOrdinal() == ordinal) {
                    return oneType;
                }
            }
        }
        throw new IllegalArgumentException("Parameter must be the ordinal of a valid MessageStatus!");
    }
}