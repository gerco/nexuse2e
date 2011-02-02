/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.service.ftp;

import java.net.InetAddress;
import java.net.ServerSocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.Configuration;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.socketfactory.FtpSocketFactory;

/**
 * Created: 16.07.2007
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class NexusSslSocketFactory extends FtpSocketFactory {
    
    private NexusSsl ssl;
    private int sslPort;
    private LogFactory logFactory;
    private Log log;
    
    public NexusSslSocketFactory() {
        ssl = null;
    }
    
    /**
     * Configure secure server related properties. 
     */
    public void configure( Configuration conf ) throws FtpException {
        super.configure( conf );
        try {
            // check if ssl is enabled
            Configuration sslConf = conf.subset( "nxssl" );
            if (!sslConf.isEmpty()) {
                sslPort = sslConf.getInt( "port" );
                ssl = new NexusSsl();
                ssl.setLogFactory( logFactory );
                ssl.configure( sslConf );
            }
        }
        catch(FtpException ex) {
            throw ex;
        } catch(Exception ex) {
            log.fatal("FtpSocketFactory.configure()", ex);
            throw new FtpException("FtpSocketFactory.configure()", ex);
        }
    }
    
    public void setLogFactory( LogFactory logFactory )  {
        super.setLogFactory( logFactory );
        this.logFactory = logFactory;
        log = logFactory.getInstance(getClass());
    }
    
    /**
     * Create secure server socket.
     */
    public ServerSocket createServerSocket() throws Exception {
        InetAddress addr = getServerAddress();
        if (ssl != null) {
            return ssl.createServerSocket( null, addr, sslPort );
        }
        return super.createServerSocket();
        
    }
}
