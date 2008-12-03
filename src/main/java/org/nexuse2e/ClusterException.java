package org.nexuse2e;


public class ClusterException extends NexusException {

    /**
     * 
     */
    private static final long serialVersionUID = -7308656795219872919L;
    int responseCode = -1;
    public ClusterException( String message, int responseCode ) {

        super( message );
        this.responseCode = responseCode;
    }
    
    /**
     * @return the responseCode
     */
    public int getResponseCode() {
    
        return responseCode;
    }
    
    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode( int responseCode ) {
    
        this.responseCode = responseCode;
    }
    
}
