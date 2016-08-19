package org.nexuse2e.configuration;

public enum ParameterType {
    UNKNOWN(0, Object.class),
    STRING(1, String.class),
    PASSWORD(2, String.class),
    ENUMERATION(3, EnumerationParameter.class),
    LIST(4, ListParameter.class),
    BOOLEAN(5, Boolean.class),
    SERVICE(6, String.class),
    TEXT(7, String.class);

    private final int      value;
    private final Class<?> type;

    public int getValue() {

        return value;
    }

    public Class<?> getType() {

        return type;
    }

    ParameterType(int value, Class<?> type) {

        this.value = value;
        this.type = type;
    }
}