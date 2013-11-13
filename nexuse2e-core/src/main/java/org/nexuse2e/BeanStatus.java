package org.nexuse2e;

/**
 * The possible states of a bean that implements the <code>Manageable</code> interface.
 * @see org.nexuse2e.Manageable
 */
public enum BeanStatus {
    ERROR(-1), UNDEFINED(0), INSTANTIATED(1), INITIALIZED(2), ACTIVATED(3), STARTED(4);

    private int value;

    BeanStatus( int value ) {

        this.value = value;
    }

    public int getValue() {

        return value;
    }
}