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
package org.nexuse2e.ui.form;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.TRPPojo;

public class PartnerConnectionForm extends ActionForm {

    /**
     * 
     */
    private static final long    serialVersionUID = 6252275396500984748L;
    private String               url;
    private String               description;
    private String               partnerId;
    private int                  nxPartnerId;
    private int                  nxConnectionId;

    private Set<CertificatePojo> certificates;
    private int                  nxCertificateId;
    private List<TRPPojo>        trps;
    private int                  nxTrpId;
    private int                  timeout;
    private int                  messageInterval;
    private boolean              secure;
    private boolean              reliable;
    private boolean              synchronous;
    private int                  synchronousTimeout;
    private int                  retries;
    private String               name;

    public void cleanSettings() {

        setUrl( null );
        setDescription( null );
        setNxCertificateId( 0 );
        setNxConnectionId( 0 );
        setNxPartnerId( 0 );
        setNxTrpId( 0 );
        setTimeout( 0 );
        setMessageInterval( 0 );
        setSecure( false );
        setReliable( false );
        setSynchronous( false );
        setSynchronousTimeout( 0 );
        setRetries( 0 );
        setName( null );
        setCertificates( null );
        setTrps( null );
    }

    public ConnectionPojo getProperties( ConnectionPojo con ) {

        con.setUri( getUrl() );
        con.setDescription( getDescription() );
        con.setNxConnectionId( getNxConnectionId() );
        con.setTimeout( getTimeout() );
        con.setMessageInterval( getMessageInterval() );
        con.setSecure( isSecure() );
        con.setReliable( isReliable() );
        con.setSynchronous( isSynchronous() );
        con.setSynchronousTimeout( getSynchronousTimeout() );
        con.setRetries( getRetries() );
        con.setName( getName() );
        con.setCertificate( null );
        for ( CertificatePojo certificatePojo : certificates ) {
            if ( nxCertificateId == certificatePojo.getNxCertificateId() ) {
                con.setCertificate( certificatePojo );
            }
        }
        for ( TRPPojo trpPojo : trps ) {
            if ( nxTrpId == trpPojo.getNxTRPId() ) {
                con.setTrp( trpPojo );
            }
        }
        return con;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription( String description ) {

        this.description = description;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl( String url ) {

        this.url = url;
    }

    /**
     * @param con
     */
    public void setProperties( ConnectionPojo con ) {

        setUrl( con.getUri() );
        setDescription( con.getDescription() );
        setNxConnectionId( con.getNxConnectionId() );
        setNxPartnerId( con.getPartner().getNxPartnerId() );
        setNxCertificateId( ( con.getCertificate() == null ? 0 : con.getCertificate().getNxCertificateId() ) );
        setNxTrpId( con.getTrp().getNxTRPId() );
        setTimeout( con.getTimeout() );
        setMessageInterval( con.getMessageInterval() );
        setSecure( con.isSecure() );
        setReliable( con.isReliable() );
        setSynchronous( con.isSynchronous() );
        setSynchronousTimeout( con.getSynchronousTimeout() );
        setRetries( con.getRetries() );
        setName( con.getName() );
    }

    public String getPartnerId() {

        return partnerId;
    }

    public void setPartnerId( String partnerId ) {

        this.partnerId = partnerId;
    }

    /**
     * @return the nxConnectionId
     */
    public int getNxConnectionId() {

        return nxConnectionId;
    }

    /**
     * @param nxConnectionId the nxConnectionId to set
     */
    public void setNxConnectionId( int nxConnectionId ) {

        this.nxConnectionId = nxConnectionId;
    }

    /**
     * @return the certificates
     */
    public Set<CertificatePojo> getCertificates() {

        return certificates;
    }

    /**
     * @param certificates the certificates to set
     */
    public void setCertificates( Set<CertificatePojo> certificates ) {

        this.certificates = certificates;
    }

    /**
     * @return the messageInterval
     */
    public int getMessageInterval() {

        return messageInterval;
    }

    /**
     * @param messageInterval the messageInterval to set
     */
    public void setMessageInterval( int messageInterval ) {

        this.messageInterval = messageInterval;
    }

    /**
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName( String name ) {

        this.name = name;
    }

    /**
     * @return the nxCertificateId
     */
    public int getNxCertificateId() {

        return nxCertificateId;
    }

    /**
     * @param nxCertificateId the nxCertificateId to set
     */
    public void setNxCertificateId( int nxCertificateId ) {

        this.nxCertificateId = nxCertificateId;
    }

    /**
     * @return the nxTrpId
     */
    public int getNxTrpId() {

        return nxTrpId;
    }

    /**
     * @param nxTrpId the nxTrpId to set
     */
    public void setNxTrpId( int nxTrpId ) {

        this.nxTrpId = nxTrpId;
    }

    /**
     * @return the reliable
     */
    public boolean isReliable() {

        return reliable;
    }

    /**
     * @param reliable the reliable to set
     */
    public void setReliable( boolean reliable ) {

        this.reliable = reliable;
    }

    /**
     * @return the retries
     */
    public int getRetries() {

        return retries;
    }

    /**
     * @param retries the retries to set
     */
    public void setRetries( int retries ) {

        this.retries = retries;
    }

    /**
     * @return the secure
     */
    public boolean isSecure() {

        return secure;
    }

    /**
     * @param secure the secure to set
     */
    public void setSecure( boolean secure ) {

        this.secure = secure;
    }

    /**
     * @return the synchronous
     */
    public boolean isSynchronous() {

        return synchronous;
    }

    /**
     * @param synchronous the synchronous to set
     */
    public void setSynchronous( boolean synchronous ) {

        this.synchronous = synchronous;
    }

    /**
     * @return the synchronousTimeout
     */
    public int getSynchronousTimeout() {

        return synchronousTimeout;
    }

    /**
     * @param synchronousTimeout the synchronousTimeout to set
     */
    public void setSynchronousTimeout( int synchronousTimeout ) {

        this.synchronousTimeout = synchronousTimeout;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {

        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout( int timeout ) {

        this.timeout = timeout;
    }

    /**
     * @return the trps
     */
    public List<TRPPojo> getTrps() {

        return trps;
    }

    /**
     * @param trps the trps to set
     */
    public void setTrps( List<TRPPojo> trps ) {

        this.trps = trps;
    }

    public int getNxPartnerId() {

        return nxPartnerId;
    }

    public void setNxPartnerId( int nxPartnerId ) {

        this.nxPartnerId = nxPartnerId;
    }

    @Override
    public void reset( ActionMapping arg0, HttpServletRequest arg1 ) {

        secure = false;
        reliable = false;
        synchronous = false;
    }

}