package org.nexuse2e.configuration;

public enum PipelineType {
    ALL(0), INBOUND(1), OUTBOUND(2);

    private final int typeOrdinal;

    PipelineType(int ordinal) {
        this.typeOrdinal = ordinal;
    }

    public int getOrdinal() {
        return typeOrdinal;
    }

    public PipelineType getByOrdinal(int ordinal) {
        if (0 <= ordinal) {
            for (PipelineType oneType : PipelineType.values()) {
                if (oneType.getOrdinal() == ordinal) {
                    return oneType;
                }
            }
        }
        throw new IllegalArgumentException("Parameter must be the ordinal of a valid PipelineType!");
    }
}