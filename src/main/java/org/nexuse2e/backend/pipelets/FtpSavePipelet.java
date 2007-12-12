package org.nexuse2e.backend.pipelets;

import java.io.File;

import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.service.ftp.AbstractFtpService;

/**
 * This pipelet implementation uploads an incoming message to an FTP(S) server on the
 * backend side. 
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class FtpSavePipelet extends AbstractPipelet {

    private AbstractFtpService ftpService;
    
    
    public FtpSavePipelet() {
        ftpService = new AbstractFtpService() {
            @Override
            protected void processFile(
                    File file, File errorDir, String partnerId) throws NexusException {
                // nothing to do
            }
        };
        ftpService.fillParameterMap( parameterMap );
        parameterMap.remove( AbstractFtpService.PARTNER_PARAM_NAME );
        setFrontendPipelet( false );
    }
    
    
    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException, NexusException {

        return null;
    }

}
