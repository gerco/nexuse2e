/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2009, Tamgroup and X-ioma GmbH
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
package org.nexuse2e.service.ftp;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.Configuration;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.interfaces.ISsl;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * Created: 16.07.2007
 * <p>
 * The <code>ISsl</code> implementation for nexus secure FTP.
 * 
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class NexusSsl implements ISsl {

    private static Logger           LOG = Logger.getLogger(NexusSsl.class);
    private Log                     log;

    private String                  sslProtocol;
    private boolean                 clientAuthReqd;
    private CertificatePojo         cert;
    private KeyStore                keystore;
    private KeyStore                truststore;

    private Map<String, SSLContext> sslContextMap;

    public NexusSsl() {
    }

    /**
     * Set the log actory.
     */
    public void setLogFactory(LogFactory factory) {
        log = factory.getInstance(getClass());
    }

    /**
     * Configure secure server related properties.
     */
    public void configure(Configuration conf) throws FtpException {

        try {

            // get configuration parameters
            String certId = conf.getString("certificate-id", null);
            sslProtocol = conf.getString("ssl-protocol", "TLS");
            clientAuthReqd = conf.getBoolean("client-authentication", false);

            // initialize keystore
            ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
            if (certId == null || certId.length() == 0) {
                List<CertificatePojo> certs = cas.getCertificates(CertificateType.LOCAL.getOrdinal(), null);
                if (certs == null || certs.isEmpty() || certs.get(0) == null) {
                    throw new FtpException("No appropriate certificate found for SFTP server authentication");
                }
                cert = certs.get(0);
            } else {
                cert = cas.getCertificateByNxCertificateId(CertificateType.LOCAL.getOrdinal(), Integer.parseInt(certId));
                if (cert == null) {
                    throw new FtpException("No local certificate with ID " + certId + " could be found for SFTP server authentication");
                }
            }

            keystore = CertificateUtil.getPKCS12KeyStore(cert);
            truststore = cas.getCacertsKeyStore();

            // initialize key manager factory

            // create ssl context map - the key is the
            // SSL protocol and the value is SSLContext.
            sslContextMap = new HashMap<String, SSLContext>();
        } catch (Exception ex) {
            log.fatal("Ssl.configure()", ex);
            throw new FtpException("Ssl.configure()", ex);
        }
    }

    /**
     * Get SSL Context.
     */
    private synchronized SSLContext getSSLContext(String protocol) throws Exception {

        // null value check
        if (protocol == null) {
            protocol = sslProtocol;
        }

        // if already stored - return it
        SSLContext ctx = sslContextMap.get(protocol);
        if (ctx != null) {
            return ctx;
        }

        try {
            KeyManager[] keymanagers = null;
            TrustManager[] trustmanagers = null;
            if (keystore != null) {
                keymanagers = CertificateUtil.createKeyManagers(keystore, EncryptionUtil.decryptString(cert.getPassword()));
            }
            if (truststore != null) {
                trustmanagers = CertificateUtil.createTrustManagers(truststore, null);
            }
            SSLContext sslcontext = SSLContext.getInstance(protocol);
            sslcontext.init(keymanagers, trustmanagers, null);
            return sslcontext;
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
            throw new Error("Unsupported algorithm exception: " + e.getMessage());
        } catch (KeyStoreException e) {
            LOG.error(e.getMessage(), e);
            throw new Error("Keystore exception: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            LOG.error(e.getMessage(), e);
            throw new Error("Key management exception: " + e.getMessage());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new Error("error reading keystore/truststore file: " + e.getMessage());
        }
    }

    /**
     * Create secure server socket.
     */
    public ServerSocket createServerSocket(String protocol, InetAddress addr, int port) throws Exception {

        // get server socket factory
        SSLContext ctx = getSSLContext(protocol);
        SSLServerSocketFactory ssocketFactory = ctx.getServerSocketFactory();

        // create server socket
        SSLServerSocket serverSocket = null;
        if (addr == null) {
            serverSocket = (SSLServerSocket) ssocketFactory.createServerSocket(port, 100);
        } else {
            serverSocket = (SSLServerSocket) ssocketFactory.createServerSocket(port, 100, addr);
        }

        // initialize server socket
        String cipherSuites[] = serverSocket.getSupportedCipherSuites();
        serverSocket.setEnabledCipherSuites(cipherSuites);
        serverSocket.setNeedClientAuth(clientAuthReqd);
        return serverSocket;
    }

    /**
     * Returns a socket layered over an existing socket.
     */
    public Socket createSocket(String protocol, Socket soc, boolean clientMode) throws Exception {

        // already wrapped - no need to do anything
        if (soc instanceof SSLSocket) {
            return soc;
        }

        // get socket factory
        SSLContext ctx = getSSLContext(protocol);
        SSLSocketFactory socFactory = ctx.getSocketFactory();

        // create socket
        String host = soc.getInetAddress().getHostAddress();
        int port = soc.getLocalPort();
        SSLSocket ssoc = (SSLSocket) socFactory.createSocket(soc, host, port, true);
        ssoc.setUseClientMode(clientMode);

        // initialize socket
        String cipherSuites[] = ssoc.getSupportedCipherSuites();
        ssoc.setEnabledCipherSuites(cipherSuites);
        ssoc.setNeedClientAuth(clientAuthReqd);

        return ssoc;
    }

    /**
     * Create a secure socket.
     */
    public Socket createSocket(String protocol, InetAddress addr, int port, boolean clientMode) throws Exception {

        // get socket factory
        SSLContext ctx = getSSLContext(protocol);
        SSLSocketFactory socFactory = ctx.getSocketFactory();

        // create socket
        SSLSocket ssoc = (SSLSocket) socFactory.createSocket(addr, port);
        ssoc.setUseClientMode(clientMode);

        // initialize socket
        String cipherSuites[] = ssoc.getSupportedCipherSuites();
        ssoc.setEnabledCipherSuites(cipherSuites);
        return ssoc;
    }

    /**
     * Create a secure socket.
     */
    public Socket createSocket(String protocol, InetAddress host, int port, InetAddress localhost, int localport, boolean clientMode) throws Exception {

        // get socket factory
        SSLContext ctx = getSSLContext(protocol);
        SSLSocketFactory socFactory = ctx.getSocketFactory();

        // create socket
        SSLSocket ssoc = (SSLSocket) socFactory.createSocket(host, port, localhost, localport);
        ssoc.setUseClientMode(clientMode);

        // initialize socket
        String cipherSuites[] = ssoc.getSupportedCipherSuites();
        ssoc.setEnabledCipherSuites(cipherSuites);
        return ssoc;
    }

    /**
     * Dispose - does nothing.
     */
    public void dispose() {
    }
}
