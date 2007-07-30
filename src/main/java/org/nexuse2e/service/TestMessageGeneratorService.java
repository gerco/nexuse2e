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

package org.nexuse2e.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;

/**
 * @author mbreilmann
 *
 */
public class TestMessageGeneratorService extends AbstractService implements SchedulerClient {

    private static Logger             LOG                         = Logger.getLogger( TestMessageGeneratorService.class );

    // Time to wait after retrieving the list of files to send
    // This is to avoid timing problems when files are still being written
    private static final int          DIRECTORY_LISTING_WAIT_TIME = 5000;
    public final static String        SCHEDULING_SERVICE          = "scheduling_service";
    public static final String        DIRECTORY                   = "directory";
    public static final String        CHOREOGRAPHY                = "choreography";
    public static final String        ACTION                      = "action";
    public static final String        PARTNER                     = "partner";
    public final static String        INTERVAL                    = "interval";
    public final static String        FILTER                      = "filter";
    public static final String        MAPPING_SERVICE             = "mapping_service";

    private SchedulingService         schedulingService           = null;
    // Directory to scan.
    private String                    directory                   = null;
    // Scan interval in milliseconds.
    private int                       interval                    = 5000;

    private String                    partnerId                   = null;
    private String                    choreographyId              = null;
    private String                    actionId                    = null;
    private FilenameFilter            filenameFilter              = null;

    private DataConversionService     mappingService              = null;

    private BackendPipelineDispatcher backendPipelineDispatcher;

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( SCHEDULING_SERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Scheduling Service",
                "The name of the service that shall be used for scheduling.", "" ) );
        parameterMap.put( DIRECTORY, new ParameterDescriptor( ParameterType.STRING, "Directory",
                "The directory to scan for files.", "" ) );
        parameterMap.put( INTERVAL, new ParameterDescriptor( ParameterType.STRING, "Interval",
                "Interval inbetween directory scans (millseconds)", "5000" ) );
        parameterMap.put( CHOREOGRAPHY, new ParameterDescriptor( ParameterType.STRING, "Choreography",
                "The choreography to use.", "" ) );
        parameterMap.put( ACTION,
                new ParameterDescriptor( ParameterType.STRING, "Action", "The action to trigger.", "" ) );
        parameterMap.put( PARTNER, new ParameterDescriptor( ParameterType.STRING, "Partner",
                "The partner to send the message.", "" ) );
        parameterMap.put( FILTER, new ParameterDescriptor( ParameterType.STRING, "Extension",
                "File extension to limit processing to.", "" ) );

        parameterMap.put( MAPPING_SERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Mapping Service",
                "The Mapping and Conversion Service.", null ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#getActivationRunlevel()
     */
    @Override
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) {

        LOG.trace( "initializing" );
        String schedulingServiceName = getParameter( SCHEDULING_SERVICE );
        String directoryValue = getParameter( DIRECTORY );
        if ( ( directoryValue != null ) && ( directoryValue.length() != 0 ) ) {
            directory = directoryValue;

            File directoryFile = new File( directory );
            if ( !directoryFile.exists() || !directoryFile.isDirectory() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Value for setting 'directory' does not point to a directory!" );
                return;
            }
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'directory' provided!" );
            return;
        }

        String intervalValue = getParameter( INTERVAL );
        if ( ( intervalValue != null ) && ( intervalValue.length() != 0 ) ) {
            interval = Integer.parseInt( intervalValue );
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'interval' provided!" );
            return;
        }

        String choreographyValue = getParameter( CHOREOGRAPHY );
        if ( ( choreographyValue != null ) && ( choreographyValue.length() != 0 ) ) {
            choreographyId = choreographyValue;
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'choreography' provided!" );
            return;
        }

        String actionValue = getParameter( ACTION );
        if ( ( actionValue != null ) && ( actionValue.length() != 0 ) ) {
            actionId = actionValue;
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'action' provided!" );
            return;
        }

        String partnerValue = getParameter( PARTNER );
        if ( ( partnerValue != null ) && ( partnerValue.length() != 0 ) ) {
            partnerId = partnerValue;
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'partner' provided!" );
            return;
        }

        String filterValue = getParameter( FILTER );
        if ( ( filterValue != null ) && ( filterValue.length() != 0 ) ) {
            filenameFilter = new FilenameExtensionFilter( filterValue );
        }

       

        String serviceName = getParameter( MAPPING_SERVICE );
        if ( !StringUtils.isEmpty( serviceName ) ) {
            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService( serviceName );
            if ( service != null && service instanceof DataConversionService ) {
                mappingService = (DataConversionService) service;
            } else {
                LOG.error( "the selected serviceName: " + serviceName + " references no valid Data Conversion Service" );
            }
        } else {
            LOG.warn( "no mapping service configured!" );
        }

        if ( !StringUtils.isEmpty( schedulingServiceName ) ) {

            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService(
                    schedulingServiceName );
            if ( service == null ) {
                status = BeanStatus.ERROR;
                LOG.error( "Service not found in configuration: " + schedulingServiceName );
                return;
            }
            if ( !( service instanceof SchedulingService ) ) {
                status = BeanStatus.ERROR;
                LOG.error( schedulingServiceName + " is instance of " + service.getClass().getName()
                        + " but SchedulingService is required" );
                return;
            }
            schedulingService = (SchedulingService) service;

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "SchedulingService is not properly configured (schedulingServiceObj == null)!" );
            return;
        }

        backendPipelineDispatcher = (BackendPipelineDispatcher) Engine.getInstance().getBeanFactory().getBean(
                "backendPipelineDispatcher" );

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#start()
     */
    @Override
    public void start() {

        LOG.trace( "starting" );
        if ( schedulingService != null ) {
            schedulingService.registerClient( this, interval );
        } else {
            LOG.error( "No scheduling service configured!" );
        }

        super.start();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#stop()
     */
    @Override
    public void stop() {

        LOG.trace( "stopping" );
        if ( schedulingService != null ) {
            schedulingService.deregisterClient( this );
        } else {
            LOG.error( "No scheduling service configured!" );
        }
        super.stop();
    }

    /**
     * 
     */
    public void scheduleNotify() {

        LOG.trace( "Scanning directory " + directory + " ..." );

        try {
            File scanDirFile = new File( directory );
            if ( !scanDirFile.isDirectory() ) {
                LOG.error( "Scan directory parameter not pointing to a valid directory: " + directory );
                return;
            }
            File[] files = null;
            if ( filenameFilter != null ) {
                files = scanDirFile.listFiles( filenameFilter );
            } else {
                files = scanDirFile.listFiles();
            }

            // Work around for timing problems (Growmark)
            // Wait a few seconds to give other processes a change to finish writing the file
            try {
                // System.out.println("Waiting " + DIRECTORY_LISTING_WAIT_TIME + " ms for files to be completely written!");
                Thread.sleep( DIRECTORY_LISTING_WAIT_TIME );
            } catch ( InterruptedException e ) {
                LOG.error( "Waiting thread was interrupted:  " + e );
            }

            for ( int i = 0; i < files.length; i++ ) {
                if ( files[i].isFile() && files[i].canWrite()
                        && ( files[i].length() != 0 /* work around for files not completely ready/empty */) ) {
                    try {
                        LOG.trace( "Processing file: " + files[i].getAbsoluteFile() );
                        processFile( files[i].getAbsolutePath() );
                    } catch ( Exception ex ) {
                        LOG.error( "Exception submitting file: " + ex );
                    }
                } else {
                    System.out.println( "Skipping file: " + files[i].getAbsoluteFile() );
                }
            }
        } catch ( Exception ioEx ) {
            LOG.error( "Error reading directory:  " + ioEx );
        }

    } // scheduleNotify

    /**
     * Process any files found.
     * @param newFile File found by the scanner.
     */
    private void processFile( String newFile ) throws Exception {

        byte[] fileBuffer = null;

        if ( ( newFile != null ) && ( newFile.length() != 0 ) ) {
            try {
                // Open the file to read one line at a time
                BufferedInputStream bufferedInputStream = new BufferedInputStream( new FileInputStream( newFile ) );

                // Determine the size of the file
                int fileSize = bufferedInputStream.available();
                fileBuffer = new byte[fileSize]; // Create a buffer that will hold the data from the file

                bufferedInputStream.read( fileBuffer, 0, fileSize ); // Read the file content into the buffer
                bufferedInputStream.close();

                

                // Prepare the Payload and set the MIME content type
                /*
                 MimetypesFileTypeMap mimetypesFileTypeMap = (MimetypesFileTypeMap) FileTypeMap.getDefaultFileTypeMap();
                 String mimeType = mimetypesFileTypeMap.getContentType( newFile );

                 Payload newPayload = new Payload( fileBuffer );
                 if ( mimeType != null ) {
                 newPayload.setContentType( mimeType );
                 } else {
                 newPayload.setContentType( "text/xml" ); // Default to text/xml in case the time could not be determined
                 }
                 */

                Map<String, String> variables = new HashMap<String, String>();
                variables.put( "$filename", new File( newFile ).getName() );
                variables.put( "$pathname", newFile );

                String tempPartnerId = partnerId;
                if ( partnerId.startsWith( "${" ) ) {
                    if ( mappingService == null ) {
                        LOG.error( "no valid Mapping service configured. Mapping and conversion features are not available!" );
                    } else {
                        MappingDefinition mappingDef = new MappingDefinition();
                        mappingDef.setCommand( partnerId );
                        tempPartnerId = mappingService.processConversion( null, mappingDef, variables );
                    }
                }

                String tempChoreographyId = choreographyId;
                if ( choreographyId.startsWith( "${" ) ) {
                    if ( mappingService == null ) {
                        LOG.error( "no valid Mapping service configured. Mapping and conversion features are not available!" );
                    } else {
                        MappingDefinition mappingDef = new MappingDefinition();
                        mappingDef.setCommand( choreographyId );
                        tempChoreographyId = mappingService.processConversion( null, mappingDef, variables );
                    }
                }

                String tempActionId = actionId;
                if ( actionId.startsWith( "${" ) ) {
                    if ( mappingService == null ) {
                        LOG.error( "no valid Mapping service configured. Mapping and conversion features are not available!" );
                    } else {
                        MappingDefinition mappingDef = new MappingDefinition();
                        mappingDef.setCommand( actionId );
                        tempActionId = mappingService.processConversion( null, mappingDef, variables );
                    }
                }

                backendPipelineDispatcher.processMessage( tempPartnerId, tempChoreographyId, tempActionId, null, null,
                        null, fileBuffer );
                
                
            } catch ( Exception ex ) {
                LOG.error( "Exception: " + ex );
            }
        }
    }

    /**
     * Delete a file that was found by the scanner.
     * @param killFile
     */
    private void deleteFile( String killFile ) {

        File killFileObject = new File( killFile );

        if ( killFileObject.delete() ) {
            LOG.error( "File " + killFile + " deleted." );
        } else {
            LOG.error( "File " + killFile + " could not be deleted." );
        }
    }

    
    private class FilenameExtensionFilter implements FilenameFilter {

        String extension = null;

        FilenameExtensionFilter( String extension ) {

            this.extension = extension;
        }

        public boolean accept( File dir, String name ) {

            if ( name.endsWith( extension ) ) {
                return true;
            }
            return false;
        }
    }
} // DirectoryScannerService
