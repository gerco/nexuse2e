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
package org.nexuse2e.backend;

import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * @author gesch
 *
 */
public class DummyDirectoryScanner {

    private static Logger             LOG              = Logger.getLogger( DummyDirectoryScanner.class );

    private BackendPipelineDispatcher backendPipelineDispatcher;
    private String                    directory;
    private String                    interval;
    private Timer                     timer            = null;
    private DirectoryScanner          directoryScanner = new DirectoryScanner();
    private String                    partnerId        = "torino8080";
    private String                    choreographyId   = "GenericFile";
    private String                    actionId         = "SendFile";

    /**
     * 
     */
    public void stop() {

        if ( timer != null ) {
            timer.cancel();
        }
    } // stop

    /**
     * 
     */
    public void start() {

        LOG.debug( "directoryScanner.start" );

        File dir = new File( directory );
        if ( !dir.exists() || dir.isFile() ) {
            return;
        }
        directoryScanner.setTargetDirectory( dir );
        timer = new Timer();
        timer.schedule( directoryScanner, 0, 5000 );

    } // start

    /**
     * @return the backendPipelineDispatcher
     */
    public BackendPipelineDispatcher getBackendPipelineDispatcher() {

        return backendPipelineDispatcher;
    }

    /**
     * @param backendPipelineDispatcher the backendPipelineDispatcher to set
     */
    public void setBackendPipelineDispatcher( BackendPipelineDispatcher backendPipelineDispatcher ) {

        this.backendPipelineDispatcher = backendPipelineDispatcher;
    }

    /**
     * @return the directory
     */
    public String getDirectory() {

        return directory;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory( String directory ) {

        this.directory = directory;
    }

    /**
     * @return the interval
     */
    public String getInterval() {

        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval( String interval ) {

        this.interval = interval;
    }

    /**
     * @author gesch
     *
     */
    private class DirectoryScanner extends TimerTask {

        private File targetDirectory = null;

        /* (non-Javadoc)
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {

            try {
                // LOG.trace( "starting scanner" );
                if ( !targetDirectory.exists() || targetDirectory.isFile() ) {
                    return;
                }
                String[] entries = targetDirectory.list();
                if ( entries != null && entries.length > 0 ) {
                    for ( int i = 0; i < entries.length; i++ ) {
                        if ( entries[i].toLowerCase().endsWith( ".xml" ) ) {

                            File newfile = new File( targetDirectory, entries[i] );

                            if ( newfile.exists() && newfile.isFile() ) {
                                FileInputStream fis = new FileInputStream( newfile );
                                byte[] data = new byte[(int) newfile.length()];
                                fis.read( data );
                                backendPipelineDispatcher.processMessage( partnerId, choreographyId, actionId, null,
                                        null, null, data );
                                newfile.renameTo( new File( newfile.getAbsoluteFile() + "_done" ) );
                            }
                        }
                    }
                }

            } catch ( Exception e ) {
                e.printStackTrace();
            } catch ( Error e ) {
                e.printStackTrace();
            }

        }

        /**
         * @return the targetDirectory
         */
        public File getTargetDirectory() {

            return targetDirectory;
        }

        /**
         * @param targetDirectory the targetDirectory to set
         */
        public void setTargetDirectory( File targetDirectory ) {

            this.targetDirectory = targetDirectory;
        }
    }

    /**
     * @return the actionId
     */
    public String getActionId() {

        return actionId;
    }

    /**
     * @param actionId the actionId to set
     */
    public void setActionId( String actionId ) {

        this.actionId = actionId;
    }

    /**
     * @return the choreographyId
     */
    public String getChoreographyId() {

        return choreographyId;
    }

    /**
     * @param choreographyId the choreographyId to set
     */
    public void setChoreographyId( String choreographyId ) {

        this.choreographyId = choreographyId;
    }

    /**
     * @return the partnerId
     */
    public String getPartnerId() {

        return partnerId;
    }

    /**
     * @param partnerId the partnerId to set
     */
    public void setPartnerId( String partnerId ) {

        this.partnerId = partnerId;
    }
}
