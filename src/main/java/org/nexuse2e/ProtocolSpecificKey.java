/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e;

/**
 * Key identifying an entity that is specific to a protocol. A protocol is identified by
 * the identifier of
 * <ul>
 * <li>communication protocol (e.g. ebxml)</li>
 * <li>communication protocol version (e.g. 2.0)</li>
 * <li>transport protocol (e.g. http)</li>
 * </ul>
 *
 * @author gesch
 */
public class ProtocolSpecificKey {

    private String communicationProtocolId      = null;
    private String communicationProtocolVersion = null;
    private String transportProtocolId          = null;

    /**
     * Default constructor
     */
    public ProtocolSpecificKey() {

    }

    /**
     * @param communicationProtocolId
     * @param cummunicationProtocolVersion
     * @param transportProtocolId
     */
    public ProtocolSpecificKey( String communicationProtocolId, String cummunicationProtocolVersion,
            String transportProtocolId ) {

        this.communicationProtocolId = communicationProtocolId;
        this.communicationProtocolVersion = cummunicationProtocolVersion;
        this.transportProtocolId = transportProtocolId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        // TODO Auto-generated method stub
        return new StringBuilder().append( "ProtocolKey: " ).append( communicationProtocolId ).append( " " ).append(
                communicationProtocolVersion ).append( " (" ).append( transportProtocolId ).append( ")" ).toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return new String( communicationProtocolId + "/" + communicationProtocolVersion + "/" + transportProtocolId )
                .hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {

        if ( this == obj ) {
            return true;
        } else {
            if ( obj instanceof ProtocolSpecificKey ) {
                ProtocolSpecificKey keyObj = (ProtocolSpecificKey) obj;
                if ( this.communicationProtocolId == null ) {
                    return false;
                }
                if ( this.communicationProtocolVersion == null ) {
                    return false;
                }
                if ( this.transportProtocolId == null ) {
                    return false;
                }
                if ( keyObj.communicationProtocolId == null ) {
                    return false;
                }
                if ( keyObj.communicationProtocolVersion == null ) {
                    return false;
                }
                if ( keyObj.transportProtocolId == null ) {
                    return false;
                }
                if ( this.communicationProtocolId.equals( keyObj.communicationProtocolId )
                        && this.communicationProtocolVersion.equals( keyObj.communicationProtocolVersion )
                        && this.transportProtocolId.equals( keyObj.transportProtocolId ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param obj
     * @return
     */
    public boolean equalsIgnoreTransport( Object obj ) {

        if ( this == obj ) {
            return true;
        } else {
            if ( obj instanceof ProtocolSpecificKey ) {
                ProtocolSpecificKey keyObj = (ProtocolSpecificKey) obj;
                if ( this.communicationProtocolId == null ) {
                    return false;
                }
                if ( this.communicationProtocolVersion == null ) {
                    return false;
                }
                if ( keyObj.communicationProtocolId == null ) {
                    return false;
                }
                if ( keyObj.communicationProtocolVersion == null ) {
                    return false;
                }
                if ( this.communicationProtocolId.equals( keyObj.communicationProtocolId )
                        && this.communicationProtocolVersion.equals( keyObj.communicationProtocolVersion ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return the communication protocol identifier. 
     * @return The communication protocol identifier.
     */
    public String getCommunicationProtocolId() {

        return communicationProtocolId;
    }

    /**
     * Set the communication protocol identifier.
     * @param communicationProtocolId the communication protocol identifier to set.
     */
    public void setCommunicationProtocolId( String communicationProtocolId ) {

        this.communicationProtocolId = communicationProtocolId;
    }

    /**
     * Return the communication protocol version.
     * @return the communication protocol version.
     */
    public String getCommunicationProtocolVersion() {

        return communicationProtocolVersion;
    }

    /**
     * Set the communication protocol version.
     * @param cummunicationProtocolVersion The communication protocol to set.
     */
    public void setCommunicationProtocolVersion( String communicationProtocolVersion ) {

        this.communicationProtocolVersion = communicationProtocolVersion;
    }

    /**
     * Return the transport protocol identifier.
     * @return The transport protocol identifier.
     */
    public String getTransportProtocolId() {

        return transportProtocolId;
    }

    /**
     * Set the transport protocol identifier.
     * @param transportProtocolId The transport protocol identifier to set.
     */
    public void setTransportProtocolId( String transportProtocolId ) {

        this.transportProtocolId = transportProtocolId;
    }

} // ProtocolSpecificKey
