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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.Layer;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.integration.NEXUSe2eInterface;

/**
 * This service implementation acts as an FTP(S) client and receives files by
 * polling an FTP server from time to time. The file content will be processed by
 * the backend outbound pipeline that is associated with the choreography and action
 * provided to this service as configuration parameters.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision: 326 $ - $LastChangedDate: 2007-12-07 18:24:43 +0100 (Fr, 07 Dez 2007) $ by $LastChangedBy: j_reese $
 */
public class FtpDirectoryScannerService extends AbstractFtpService {

    private static Logger      LOG                     = Logger.getLogger( FtpDirectoryScannerService.class );

    public static final String CHOREOGRAPHY_PARAM_NAME = "choreographyId";
    public static final String ACTION_PARAM_NAME       = "actionId";

    private static int         counter                 = 0;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( CHOREOGRAPHY_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Choreography",
                "The choreography ID", "" ) );
        parameterMap.put( ACTION_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Action", "The action ID",
                "" ) );
        super.fillParameterMap( parameterMap );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    /**
     * Process any files found.
     * @param file File found to be processed.
     * @param errorDir The error directory.
     * @param partnerId The partner ID.
     */
    protected void processFile( File file, File errorDir, String partnerId ) throws NexusException {

        counter++;

        String choreographyId = (String) getParameter( CHOREOGRAPHY_PARAM_NAME );
        String actionId = (String) getParameter( ACTION_PARAM_NAME );

        if ( choreographyId != null && actionId != null && choreographyId.length() > 0 && actionId.length() > 0
                && file != null && file.exists() && file.length() != 0 ) {
            try {
                // Open the file to read one line at a time
                String content = FileUtils.readFileToString( file );

                LOG.debug( "Submitting file " + file.getAbsolutePath() + " , character size: " + content.length() );

                NEXUSe2eInterface nexusInterface = Engine.getInstance().getInProcessNEXUSe2eInterface();

                String conversationId = nexusInterface.sendNewStringMessage( choreographyId, partnerId, actionId,
                        content );

                LOG.debug( "Message sent ( choreography '" + choreographyId + "', conversation ID '" + conversationId
                        + "')!" );

                file.delete();
            } catch ( Exception ex ) {
                LOG.error( "An error occurred while processing file " + file, ex );
                try {
                    String postfix = "_" + System.currentTimeMillis() + "_" + counter;
                    FileUtils.copyFile( file, new File( errorDir, file.getName() + postfix ) );
                    file.delete();
                } catch ( IOException ioex ) {
                    LOG.error( "Could not copy file " + file + " to error directory " + errorDir, ioex );
                }
                throw new NexusException( "An error occurred while processing file " + file, ex );
            }
        } else {
            LOG.error( "FTP content not delivered to backend pipeline. "
                    + "Please verify that action ID and choreography ID are configured." );
        }
    }
}
