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
package org.nexuse2e.backend.pipelets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.util.ServerPropertiesUtil;

public class FileSystemSavePipelet extends AbstractPipelet {

    private static Logger      LOG                          = Logger.getLogger( FileSystemSavePipelet.class );

    public static final String DIRECTORY_PARAM_NAME         = "directory";
    public static final String FILE_NAME_PATTERN_PARAM_NAME = "fileNamePattern";
    public static final String USE_CONTENT_ID               = "useContentId";

    public static final String SEQUENCE_PARAM               = "${nexus.message.payload.sequence}";

    private String             targetDirectory              = null;
    private String             fileNamePattern              = null;
    private boolean            useContentId              = false;

    /**
     * Default constructor.
     */
    public FileSystemSavePipelet() {

        parameterMap.put( DIRECTORY_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Save directory",
                "Path to directory where to store files", "" ) );
        parameterMap.put( FILE_NAME_PATTERN_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "File Name",
                "File Name Pattern", "${nexus.message.message}" ) );
        parameterMap.put( USE_CONTENT_ID, new ParameterDescriptor( ParameterType.BOOLEAN, "Use Content ID",
                "Flag whether to use the content ID as the file name", Boolean.FALSE ) );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        targetDirectory = (String) getParameter( DIRECTORY_PARAM_NAME );
        if ( StringUtils.isEmpty( targetDirectory ) ) {
            LOG.error( "Output directory not defined, can not store inbound message!" );
        } else {
            targetDirectory = ServerPropertiesUtil.replacePathSeparators( targetDirectory );
        }
        LOG.trace( "targetDirectory : " + targetDirectory );

        fileNamePattern = (String) getParameter( FILE_NAME_PATTERN_PARAM_NAME );
        if ( StringUtils.isEmpty( fileNamePattern ) ) {
            LOG.warn( "Output file name pattern not defined, using default!" );
            fileNamePattern = "${nexus.message.message}";
        }

        Boolean tempFlag = getParameter( USE_CONTENT_ID );
        if ( tempFlag != null && tempFlag.equals( Boolean.TRUE ) ) {
            useContentId = true;
            LOG.info( "Using content ID in file name." );
        }

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        String tempDirectory = ServerPropertiesUtil.replaceServerProperties( targetDirectory, messageContext );
        for ( MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads() ) {
            try {
                String fileName = writePayloadToUniqueFile( tempDirectory, payload, messageContext );
                LOG.trace( new LogMessage( "Wrote output file: " + fileName.toString(),messageContext.getMessagePojo()) );
            } catch ( FileNotFoundException e ) {
                throw new NexusException( "Could not create output file in target directory: " + targetDirectory, e );
            } catch ( IOException e ) {
                throw new NexusException( "Could not create output file in target directory: " + targetDirectory, e );
            }
        }

        // TODO Auto-generated method stub
        return messageContext;
    }

    /**
     * Generate a unique file name for a payload, finding it's file extension
     * and write data to that file.
     * @param destinationDirectory where the file gets written
     * @param msgInfo about message.
     * @param payload the contents of the message
     * returns the full name of the file written.
     */
    public String writePayloadToUniqueFile( String destinationDirectory, MessagePayloadPojo payload,
            MessageContext messageContext ) throws FileNotFoundException, IOException {

        String retVal = null;
        boolean success = false;

        // Workaround, sometimes filesystem are 'sleeping' and need two tries to
        // get things rolling. Try once and ignore failure.
        try {
            retVal = writePayload( destinationDirectory, payload, messageContext );
            success = true;
        } catch ( Exception ex ) {
            // ignore
            success = false;
        }

        // If 1st time failed, try again
        if ( success == false ) {
            retVal = writePayload( destinationDirectory, payload, messageContext );
        }

        return retVal;

    }

    /**
     * @param destinationDirectory
     * @param messageContext
     * @param includeSender
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String writePayload( String destinationDirectory, MessagePayloadPojo payload, MessageContext messageContext )
            throws FileNotFoundException, IOException {

        File destDirFile = new File( destinationDirectory );
        // StringBuffer fileName = new StringBuffer();

        if ( destDirFile.isDirectory() ) {
            if ( !destDirFile.exists() ) {
                destDirFile.mkdirs();
            }
        } else {
            throw new FileNotFoundException( "Not a directory: " + destDirFile );
        }
        
        String fileName = null;
        if ( useContentId ) {
            fileName = destinationDirectory + File.separatorChar + payload.getContentId();
        } else {
            String baseFileName = ServerPropertiesUtil.replaceServerProperties( fileNamePattern, messageContext );

            String extension = Engine.getInstance().getFileExtensionFromMime( payload.getMimeType().toLowerCase() );
            if ( StringUtils.isEmpty( extension ) ) {
                extension = "dat";
            }
            fileName = destinationDirectory + File.separatorChar + baseFileName + "_" + payload.getSequenceNumber()
                    + "." + extension;
        }


        BufferedOutputStream fileOutputStream = new BufferedOutputStream( new FileOutputStream( fileName.toString() ) );

        fileOutputStream.write( payload.getPayloadData() );
        fileOutputStream.flush();
        fileOutputStream.close();

        return fileName.toString();
    }
} // FileSystemSavePipelet
