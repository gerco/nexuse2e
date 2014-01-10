package org.nexuse2e;

public enum MessageBackendStatus {
    FAILED(-1), UNKNOWN(0), SENT(1);

    int ordinal = 0;

    MessageBackendStatus(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public static MessageBackendStatus getByOrdinal(int ordinal) {
        if (-1 <= ordinal) {
            for (MessageBackendStatus oneType : MessageBackendStatus.values()) {
                if (oneType.getOrdinal() == ordinal) {
                    return oneType;
                }
            }
        }
        throw new IllegalArgumentException("Parameter must be the ordinal of a valid MessageStatus!");
    }
}