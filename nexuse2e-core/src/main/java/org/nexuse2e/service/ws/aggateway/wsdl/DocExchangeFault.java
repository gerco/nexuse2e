
package org.nexuse2e.service.ws.aggateway.wsdl;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.1.2
 * Fri Jan 28 13:47:48 CET 2011
 * Generated source version: 2.1.2
 * 
 */

@WebFault(name = "problem", targetNamespace = "http://www.w3.org/2001/XMLSchema")
public class DocExchangeFault extends Exception {
    public static final long serialVersionUID = 20110128134748L;
    
    private java.lang.String problem;

    public DocExchangeFault() {
        super();
    }
    
    public DocExchangeFault(String message) {
        super(message);
    }
    
    public DocExchangeFault(String message, Throwable cause) {
        super(message, cause);
    }

    public DocExchangeFault(String message, java.lang.String problem) {
        super(message);
        this.problem = problem;
    }

    public DocExchangeFault(String message, java.lang.String problem, Throwable cause) {
        super(message, cause);
        this.problem = problem;
    }

    public java.lang.String getFaultInfo() {
        return this.problem;
    }
}