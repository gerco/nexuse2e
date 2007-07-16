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
 * TODO Class documentation
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
