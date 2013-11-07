package org.nexuse2e;

/**
 * The possible run levels of a bean that implements the <code>Manageable</code> interface. 
 * The concept of run levels is similar to that used in operating systems like Linux.
 * @see org.nexuse2e.Manageable
 */
public enum Layer {
    UNKNOWN, CREATED, CONFIGURATION, CORE, OUTBOUND_PIPELINES, INBOUND_PIPELINES, INTERFACES
}