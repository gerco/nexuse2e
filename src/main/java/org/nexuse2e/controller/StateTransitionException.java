package org.nexuse2e.controller;

/**
 * This exception signals invalid state transitions for stateful NEXUSe2e POJO types,
 * e.g. conversations or messages.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class StateTransitionException extends Exception {

    private static final long serialVersionUID = 1L;

    public StateTransitionException() {
        super();
    }

    public StateTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateTransitionException(String message) {
        super(message);
    }

    public StateTransitionException(Throwable cause) {
        super(cause);
    }
}
