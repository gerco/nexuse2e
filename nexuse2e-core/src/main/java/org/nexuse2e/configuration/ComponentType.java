package org.nexuse2e.configuration;

/**
 * Used to store incomplete Certificate chain parts in rcp client
 */
public enum ComponentType {
    ALL(0), PIPELET(1), LOGGER(2), SERVICE(3);

    private final int value;

    public int getValue() {

        return value;
    }

    ComponentType(int value) {

        this.value = value;
    }
}