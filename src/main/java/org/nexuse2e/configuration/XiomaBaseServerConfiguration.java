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
package org.nexuse2e.configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.GrantPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipeletParamPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.ServiceParamPojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.service.http.HttpReceiverService;
import org.nexuse2e.service.http.HttpSenderService;
import org.nexuse2e.service.mail.Pop3Receiver;
import org.nexuse2e.service.mail.SmtpSender;

/**
 * @author mbreilmann
 *
 */
public class XiomaBaseServerConfiguration implements BaseConfigurationProvider {

    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.BaseConfigurationProvider#createBaseConfiguration(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List)
     */
    public void createBaseConfiguration( List<ComponentPojo> components, List<ChoreographyPojo> choreographies,
            List<PartnerPojo> partners, List<PipelinePojo> backendPipelineTemplates,
            List<PipelinePojo> frontendPipelineTemplates, List<ServicePojo> services,
            List<CertificatePojo> caCertificates, List<TRPPojo> trps, List<UserPojo> users, List<RolePojo> roles,
            List<LoggerPojo> loggers ) throws InstantiationException {

        try {

            TRPPojo ebXML1HttpTRPPojo = new TRPPojo( "ebxml", "1.0", "http", new Date(), new Date(), 1 );
            TRPPojo ebXML2HttpTRPPojo = new TRPPojo( "ebxml", "2.0", "http", new Date(), new Date(), 1 );
            TRPPojo ebXML2MailTRPPojo = new TRPPojo( "ebxml", "2.0", "mail", new Date(), new Date(), 1 );
            TRPPojo httpPlainTRPPojo = new TRPPojo( "httpplain", "1.0", "http", new Date(), new Date(), 1 );

            /*
             trps = new ArrayList<TRPPojo>();
             partners = new ArrayList<PartnerPojo>();
             choreographies = new ArrayList<ChoreographyPojo>();
             components = new ArrayList<ComponentPojo>();
             backendPipelineTemplates = new ArrayList<PipelinePojo>();
             */

            trps.add( ebXML1HttpTRPPojo );
            trps.add( ebXML2HttpTRPPojo );
            trps.add( ebXML2MailTRPPojo );
            trps.add( httpPlainTRPPojo );

            ComponentPojo inboundComponentPojo = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "FileSystemSavePipelet", "org.nexuse2e.backend.pipelets.FileSystemSavePipelet",
                    "FileSystemSavePipelet" );
            ComponentPojo outboundComponentPojo = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "FileSystemLoadPipelet", "org.nexuse2e.backend.pipelets.FileSystemLoadPipelet",
                    "FileSystemLoadPipelet" );

            ComponentPojo dbLoggerComponentPojo = new ComponentPojo( new Date(), new Date(), 1, ComponentType.LOGGER
                    .getValue(), "DBLogger", "org.nexuse2e.logging.DatabaseLogger", "Database Root Logger" );

            LoggerPojo dbRootLogger = new LoggerPojo( dbLoggerComponentPojo, new Date(), new Date(), 1,
                    "DB Root Logger", true, "group_mail,group_core,group_outbound,group_inbound," );

            loggers.add( dbRootLogger );

            PipelinePojo fileSaveInboundPipelinePojo = new PipelinePojo();
            PipelinePojo fileLoadOutboundPipelinePojo = new PipelinePojo();

            // Backend pipeline definition
            PipeletPojo fileSaveInboundPipeletPojo = new PipeletPojo( fileSaveInboundPipelinePojo,
                    inboundComponentPojo, new Date(), new Date(), 1, 0, "FileSave", "FileSave", null );
            fileSaveInboundPipeletPojo.setFrontend( false );
            PipeletPojo fileLoadOutboundPipeletPojo = new PipeletPojo( fileLoadOutboundPipelinePojo,
                    outboundComponentPojo, new Date(), new Date(), 1, 0, "FileLoad", "FileLoad", null );
            fileLoadOutboundPipeletPojo.setFrontend( false );

            ArrayList<PipeletPojo> fileSaveInboundPipelets = new ArrayList<PipeletPojo>();
            fileSaveInboundPipelets.add( fileSaveInboundPipeletPojo );

            ArrayList<PipeletPojo> fileLoadOutboundPipelets = new ArrayList<PipeletPojo>();
            fileLoadOutboundPipelets.add( fileLoadOutboundPipeletPojo );

            fileSaveInboundPipelinePojo.setOutbound( false );
            fileSaveInboundPipelinePojo.setCreatedDate( new Date() );
            fileSaveInboundPipelinePojo.setModifiedDate( new Date() );
            fileSaveInboundPipelinePojo.setModifiedNxUserId( 1 );
            fileSaveInboundPipelinePojo.setDescription( "FileSaveInboundPipeline" );
            fileSaveInboundPipelinePojo.setName( "FileSaveInboundPipeline" );
            fileSaveInboundPipelinePojo.setFrontend( false );
            fileSaveInboundPipelinePojo.setPipelets( fileSaveInboundPipelets );

            fileLoadOutboundPipelinePojo.setOutbound( true );
            fileLoadOutboundPipelinePojo.setCreatedDate( new Date() );
            fileLoadOutboundPipelinePojo.setModifiedDate( new Date() );
            fileLoadOutboundPipelinePojo.setModifiedNxUserId( 1 );
            fileLoadOutboundPipelinePojo.setDescription( "FileLoadOutboundPipeline" );
            fileLoadOutboundPipelinePojo.setName( "FileLoadOutboundPipeline" );
            fileLoadOutboundPipelinePojo.setFrontend( false );
            fileLoadOutboundPipelinePojo.setPipelets( fileLoadOutboundPipelets );

            // Frontend pipeline definition
            /*
             <bean id="ebxmlHttpUnpacker" class="org.nexuse2e.messaging.ebxml.HTTPMessageUnpackager" />
             <bean id="ebxmlPop3Unpacker" class="org.nexuse2e.messaging.ebxml.MimeMessageUnpackager" />
             <bean id="ebxml20deserializer" class="org.nexuse2e.messaging.ebxml.v20.HeaderDeserializer" />
             <bean id="ebxmlPatcher" class="org.nexuse2e.messaging.ebxml.MessagePojoPatcher" />

             <bean id="ebxml20serializer" class="org.nexuse2e.messaging.ebxml.v20.HeaderSerializer" />
             <bean id="ebxmlHttpPacker" class="org.nexuse2e.messaging.ebxml.HTTPMessagePackager" />
             <bean id="ebxmlSmtpPacker" class="org.nexuse2e.messaging.ebxml.MimeMessagePackager" />

             <bean id="httpPlainMessageUnpacker" class="org.nexuse2e.messaging.httpplain.HTTPPlainMessageUnpacker" />
             <bean id="httpPlainMessagePacker" class="org.nexuse2e.messaging.httpplain.HTTPPlainMessagePacker" />
             <bean id="httpPlainHeaderDeserializer" class="org.nexuse2e.messaging.httpplain.HTTPPlainHeaderDeserializer" />
             */
            ComponentPojo transportReceiver = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "TransportReceiver", "org.nexuse2e.transport.TransportReceiver", "TransportReceiver" );
            ComponentPojo ebxmlHttpUnpacker = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "ebXMLHttpUnpacker", "org.nexuse2e.messaging.ebxml.HTTPMessageUnpackager",
                    "ebXMLHttpUnpacker Pipelet" );
            ComponentPojo ebxml20Deserializer = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "ebXML20Deserializer", "org.nexuse2e.messaging.ebxml.v20.HeaderDeserializer",
                    "ebXML 2.0 Deserializer Pipelet" );
            ComponentPojo ebxmlPatcher = new ComponentPojo( new Date(), new Date(), 1,
                    ComponentType.PIPELET.getValue(), "ebXMLPatcher",
                    "org.nexuse2e.messaging.ebxml.MessagePojoPatcher", "ebXMLPatcher Pipelet" );

            ComponentPojo ebxml20Serializer = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "ebXML20Serializer", "org.nexuse2e.messaging.ebxml.v20.HeaderSerializer",
                    "ebXML 2.0 Serializer Pipelet" );
            ComponentPojo ebxmlHttpPacker = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "ebXMLHttpPacker", "org.nexuse2e.messaging.ebxml.HTTPMessagePackager",
                    "ebXMLHttpPacker Pipelet" );
            ComponentPojo transportSender = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "TransportSender", "org.nexuse2e.transport.TransportSender", "TransportSender" );

            ComponentPojo ebxmlPop3Unpacker = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "ebXMLPop3Unpacker", "org.nexuse2e.messaging.ebxml.MimeMessageUnpackager",
                    "ebXMLPop3Unpacker Pipelet" );
            ComponentPojo ebxmlSmtpPacker = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "ebXMLSmtpPacker", "org.nexuse2e.messaging.ebxml.MimeMessagePackager",
                    "ebXMLSmtpPacker Pipelet" );

            ComponentPojo ebxml10Deserializer = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "ebXML10Deserializer", "org.nexuse2e.messaging.ebxml.v10.HeaderDeserializer",
                    "ebXML 1.0 Deserializer Pipelet" );
            ComponentPojo ebxml10Serializer = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "ebXML10Serializer", "org.nexuse2e.messaging.ebxml.v10.HeaderSerializer",
                    "ebXML 1.0 Serializer Pipelet" );

            ComponentPojo httpPlainMessageUnpacker = new ComponentPojo( new Date(), new Date(), 1,
                    ComponentType.PIPELET.getValue(), "httpPlainMessageUnpacker",
                    "org.nexuse2e.messaging.httpplain.HTTPPlainMessageUnpacker", "httpPlainMessageUnpacker Pipelet" );
            ComponentPojo httpPlainHeaderDeserializer = new ComponentPojo( new Date(), new Date(), 1,
                    ComponentType.PIPELET.getValue(), "httpPlainHeaderDeserializer",
                    "org.nexuse2e.messaging.httpplain.HTTPPlainHeaderDeserializer",
                    "httpPlainHeaderDeserializer Pipelet" );
            ComponentPojo httpPlainMessagePacker = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "httpPlainMessagePacker", "org.nexuse2e.messaging.httpplain.HTTPPlainMessagePacker",
                    "httpPlainMessagePacker Pipelet" );
            components.add( transportReceiver );
            components.add( ebxmlHttpUnpacker );
            components.add( ebxml20Deserializer );
            components.add( ebxmlPatcher );

            components.add( ebxml20Serializer );
            components.add( ebxmlHttpPacker );
            components.add( transportSender );

            components.add( ebxml10Deserializer );
            components.add( ebxml10Serializer );

            components.add( ebxmlPop3Unpacker );
            components.add( ebxmlSmtpPacker );

            components.add( httpPlainMessageUnpacker );
            components.add( httpPlainHeaderDeserializer );
            components.add( httpPlainMessagePacker );

            List<PipeletParamPojo> params = null;
            PipeletParamPojo param = null;
            
            /* --------------------------------------------------------------
             * ebXML 1.0
             * --------------------------------------------------------------
             */
            
            // HTTP
            PipelinePojo ebXML10InboundPipelinePojo = new PipelinePojo();
            PipelinePojo ebXML10OutboundPipelinePojo = new PipelinePojo();

            // inbound
            PipeletPojo ebxml10HTTPTransportReceiverPipeletPojo = new PipeletPojo( ebXML10InboundPipelinePojo,
                    transportReceiver, new Date(), new Date(), 1, 0, "ebxml10", "ebxmlHTTPTransportReceiver", null );
            ebxml10HTTPTransportReceiverPipeletPojo.setFrontend( true );
            ebxml10HTTPTransportReceiverPipeletPojo.setPosition( 0 );
            params = new ArrayList<PipeletParamPojo>();
            param = new PipeletParamPojo( ebxml10HTTPTransportReceiverPipeletPojo, new Date(),
                    new Date(), 1, "service", "HttpReceiverService_ebXML_1.0" );
            params.add( param );
            ebxml10HTTPTransportReceiverPipeletPojo.setPipeletParams( params );
            PipeletPojo ebxml10HttpUnpackerPipeletPojo = new PipeletPojo( ebXML10InboundPipelinePojo, ebxmlHttpUnpacker,
                    new Date(), new Date(), 1, 0, "ebxmlHttpUnpacker", "ebxmlHttpUnpacker", null );
            ebxml10HttpUnpackerPipeletPojo.setFrontend( true );
            ebxml10HttpUnpackerPipeletPojo.setPosition( 1 );
            PipeletPojo ebxml10DeserializerPipeletPojo = new PipeletPojo( ebXML10InboundPipelinePojo,
                    ebxml10Deserializer, new Date(), new Date(), 1, 0, "ebxml10Deserializer", "ebxml10Deserializer",
                    null );
            ebxml10DeserializerPipeletPojo.setFrontend( true );
            ebxml10DeserializerPipeletPojo.setPosition( 2 );
            PipeletPojo ebxml10PatcherPipeletPojo = new PipeletPojo( ebXML10InboundPipelinePojo, ebxmlPatcher,
                    new Date(), new Date(), 1, 0, "ebxmlPatcher", "ebxmlPatcher", null );
            ebxml10PatcherPipeletPojo.setFrontend( true );
            ebxml10PatcherPipeletPojo.setPosition( 3 );
            // outbound
            PipeletPojo ebxml10SerializerPipeletPojo = new PipeletPojo( ebXML10OutboundPipelinePojo, ebxml10Serializer,
                    new Date(), new Date(), 1, 0, "ebxml10Serializer", "ebxml10Serializer", null );
            ebxml10SerializerPipeletPojo.setFrontend( true );
            ebxml10SerializerPipeletPojo.setPosition( 0 );
            PipeletPojo ebxml10HttpPackerPipeletPojo = new PipeletPojo( ebXML10OutboundPipelinePojo, ebxmlHttpPacker,
                    new Date(), new Date(), 1, 0, "ebxmlHttpPacker", "ebxmlHttpPacker", null );
            ebxml10HttpPackerPipeletPojo.setFrontend( true );
            ebxml10HttpPackerPipeletPojo.setPosition( 1 );
            PipeletPojo ebxml10HttpTransportSenderPipeletPojo = new PipeletPojo( ebXML10OutboundPipelinePojo,
                    transportSender, new Date(), new Date(), 1, 0, "ebxmlHttpTransportSender",
                    "ebxmlHttpTransportSender", null );
            ebxml10HttpTransportSenderPipeletPojo.setFrontend( true );
            ebxml10HttpTransportSenderPipeletPojo.setPosition( 2 );
            params = new ArrayList<PipeletParamPojo>();
            param = new PipeletParamPojo( ebxml10HttpTransportSenderPipeletPojo, new Date(), new Date(), 1, "service",
                    "HttpSenderService_ebXML_1.0" );
            params.add( param );
            ebxml10HttpTransportSenderPipeletPojo.setPipeletParams( params );

            ArrayList<PipeletPojo> ebXML10InboundPipelets = new ArrayList<PipeletPojo>();
            ebXML10InboundPipelets.add( ebxml10HTTPTransportReceiverPipeletPojo );
            ebXML10InboundPipelets.add( ebxml10HttpUnpackerPipeletPojo );
            ebXML10InboundPipelets.add( ebxml10DeserializerPipeletPojo );
            ebXML10InboundPipelets.add( ebxml10PatcherPipeletPojo );

            ArrayList<PipeletPojo> ebXML10OutboundPipelets = new ArrayList<PipeletPojo>();
            ebXML10OutboundPipelets.add( ebxml10SerializerPipeletPojo );
            ebXML10OutboundPipelets.add( ebxml10HttpPackerPipeletPojo );
            ebXML10OutboundPipelets.add( ebxml10HttpTransportSenderPipeletPojo );

            ebXML10InboundPipelinePojo.setOutbound( false );
            ebXML10InboundPipelinePojo.setCreatedDate( new Date() );
            ebXML10InboundPipelinePojo.setModifiedDate( new Date() );
            ebXML10InboundPipelinePojo.setModifiedNxUserId( 1 );
            ebXML10InboundPipelinePojo.setDescription( "ebXML 1.0 Inbound Pipeline" );
            ebXML10InboundPipelinePojo.setName( "ebXML10InboundPipeline" );
            ebXML10InboundPipelinePojo.setFrontend( true );
            ebXML10InboundPipelinePojo.setPipelets( ebXML10InboundPipelets );
            ebXML10InboundPipelinePojo.setTrp( ebXML1HttpTRPPojo );

            ebXML10OutboundPipelinePojo.setOutbound( true );
            ebXML10OutboundPipelinePojo.setCreatedDate( new Date() );
            ebXML10OutboundPipelinePojo.setModifiedDate( new Date() );
            ebXML10OutboundPipelinePojo.setModifiedNxUserId( 1 );
            ebXML10OutboundPipelinePojo.setDescription( "ebXML 1.0 Outbound Pipeline" );
            ebXML10OutboundPipelinePojo.setName( "ebXML10OutboundPipeline" );
            ebXML10OutboundPipelinePojo.setFrontend( true );
            ebXML10OutboundPipelinePojo.setPipelets( ebXML10OutboundPipelets );
            ebXML10OutboundPipelinePojo.setTrp( ebXML1HttpTRPPojo );

            frontendPipelineTemplates.add( ebXML10InboundPipelinePojo );
            frontendPipelineTemplates.add( ebXML10OutboundPipelinePojo );

            /* --------------------------------------------------------------
             * ebXML 2.0
             * --------------------------------------------------------------
             */
            
            // HTTP
            PipelinePojo ebXML20InboundPipelinePojo = new PipelinePojo();
            PipelinePojo ebXML20OutboundPipelinePojo = new PipelinePojo();

            // inbound
            PipeletPojo ebxmlHTTPTransportReceiverPipeletPojo = new PipeletPojo( ebXML20InboundPipelinePojo,
                    transportReceiver, new Date(), new Date(), 1, 0, "ebxml20", "ebxmlHTTPTransportReceiver", null );
            ebxmlHTTPTransportReceiverPipeletPojo.setFrontend( true );
            ebxmlHTTPTransportReceiverPipeletPojo.setPosition( 0 );
            params = new ArrayList<PipeletParamPojo>();
            param = new PipeletParamPojo( ebxmlHTTPTransportReceiverPipeletPojo, new Date(),
                    new Date(), 1, "service", "HttpReceiverService" );
            params.add( param );
            ebxmlHTTPTransportReceiverPipeletPojo.setPipeletParams( params );
            PipeletPojo ebxmlHttpUnpackerPipeletPojo = new PipeletPojo( ebXML20InboundPipelinePojo, ebxmlHttpUnpacker,
                    new Date(), new Date(), 1, 0, "ebxmlHttpUnpacker", "ebxmlHttpUnpacker", null );
            ebxmlHttpUnpackerPipeletPojo.setFrontend( true );
            ebxmlHttpUnpackerPipeletPojo.setPosition( 1 );
            PipeletPojo ebxml20DeserializerPipeletPojo = new PipeletPojo( ebXML20InboundPipelinePojo,
                    ebxml20Deserializer, new Date(), new Date(), 1, 0, "ebxml20Deserializer", "ebxml20Deserializer",
                    null );
            ebxml20DeserializerPipeletPojo.setFrontend( true );
            ebxml20DeserializerPipeletPojo.setPosition( 2 );
            PipeletPojo ebxmlPatcherPipeletPojo = new PipeletPojo( ebXML20InboundPipelinePojo, ebxmlPatcher,
                    new Date(), new Date(), 1, 0, "ebxmlPatcher", "ebxmlPatcher", null );
            ebxmlPatcherPipeletPojo.setFrontend( true );
            ebxmlPatcherPipeletPojo.setPosition( 3 );
            // outbound
            PipeletPojo ebxml20SerializerPipeletPojo = new PipeletPojo( ebXML20OutboundPipelinePojo, ebxml20Serializer,
                    new Date(), new Date(), 1, 0, "ebxml20Serializer", "ebxml20Serializer", null );
            ebxml20SerializerPipeletPojo.setFrontend( true );
            ebxml20SerializerPipeletPojo.setPosition( 0 );
            PipeletPojo ebxmlHttpPackerPipeletPojo = new PipeletPojo( ebXML20OutboundPipelinePojo, ebxmlHttpPacker,
                    new Date(), new Date(), 1, 0, "ebxmlHttpPacker", "ebxmlHttpPacker", null );
            ebxmlHttpPackerPipeletPojo.setFrontend( true );
            ebxmlHttpPackerPipeletPojo.setPosition( 1 );
            PipeletPojo ebxmlHttpTransportSenderPipeletPojo = new PipeletPojo( ebXML20OutboundPipelinePojo,
                    transportSender, new Date(), new Date(), 1, 0, "ebxmlHttpTransportSender",
                    "ebxmlHttpTransportSender", null );
            ebxmlHttpTransportSenderPipeletPojo.setFrontend( true );
            ebxmlHttpTransportSenderPipeletPojo.setPosition( 2 );
            params = new ArrayList<PipeletParamPojo>();
            param = new PipeletParamPojo( ebxmlHttpTransportSenderPipeletPojo, new Date(), new Date(), 1, "service",
                    "HttpSenderService" );
            params.add( param );
            ebxmlHttpTransportSenderPipeletPojo.setPipeletParams( params );

            ArrayList<PipeletPojo> ebXML20InboundPipelets = new ArrayList<PipeletPojo>();
            ebXML20InboundPipelets.add( ebxmlHTTPTransportReceiverPipeletPojo );
            ebXML20InboundPipelets.add( ebxmlHttpUnpackerPipeletPojo );
            ebXML20InboundPipelets.add( ebxml20DeserializerPipeletPojo );
            ebXML20InboundPipelets.add( ebxmlPatcherPipeletPojo );

            ArrayList<PipeletPojo> ebXML20OutboundPipelets = new ArrayList<PipeletPojo>();
            ebXML20OutboundPipelets.add( ebxml20SerializerPipeletPojo );
            ebXML20OutboundPipelets.add( ebxmlHttpPackerPipeletPojo );
            ebXML20OutboundPipelets.add( ebxmlHttpTransportSenderPipeletPojo );

            ebXML20InboundPipelinePojo.setOutbound( false );
            ebXML20InboundPipelinePojo.setCreatedDate( new Date() );
            ebXML20InboundPipelinePojo.setModifiedDate( new Date() );
            ebXML20InboundPipelinePojo.setModifiedNxUserId( 1 );
            ebXML20InboundPipelinePojo.setDescription( "ebXML 2.0 Inbound Pipeline" );
            ebXML20InboundPipelinePojo.setName( "ebXML20InboundPipeline" );
            ebXML20InboundPipelinePojo.setFrontend( true );
            ebXML20InboundPipelinePojo.setPipelets( ebXML20InboundPipelets );
            ebXML20InboundPipelinePojo.setTrp( ebXML2HttpTRPPojo );

            ebXML20OutboundPipelinePojo.setOutbound( true );
            ebXML20OutboundPipelinePojo.setCreatedDate( new Date() );
            ebXML20OutboundPipelinePojo.setModifiedDate( new Date() );
            ebXML20OutboundPipelinePojo.setModifiedNxUserId( 1 );
            ebXML20OutboundPipelinePojo.setDescription( "ebXML 2.0 Outbound Pipeline" );
            ebXML20OutboundPipelinePojo.setName( "ebXML20OutboundPipeline" );
            ebXML20OutboundPipelinePojo.setFrontend( true );
            ebXML20OutboundPipelinePojo.setPipelets( ebXML20OutboundPipelets );
            ebXML20OutboundPipelinePojo.setTrp( ebXML2HttpTRPPojo );

            frontendPipelineTemplates.add( ebXML20InboundPipelinePojo );
            frontendPipelineTemplates.add( ebXML20OutboundPipelinePojo );

            // SMTP
            PipelinePojo ebXML20POP3InboundPipelinePojo = new PipelinePojo();
            PipelinePojo ebXML20SMTPOutboundPipelinePojo = new PipelinePojo();

            PipeletPojo ebxmlPOP3ransportReceiverPipeletPojo = new PipeletPojo( ebXML20POP3InboundPipelinePojo,
                    transportReceiver, new Date(), new Date(), 1, 0, "ebxml20POP3", "ebxmlPOP3TransportReceiver", null );
            ebxmlHTTPTransportReceiverPipeletPojo.setFrontend( true );
            params = new ArrayList<PipeletParamPojo>();
            param = new PipeletParamPojo( ebxmlPOP3ransportReceiverPipeletPojo, new Date(), new Date(), 1, "service",
                    "Pop3ReceiverService" );
            params.add( param );
            ebxmlPOP3ransportReceiverPipeletPojo.setPipeletParams( params );
            PipeletPojo ebxmlPOP3UnpackerPipeletPojo = new PipeletPojo( ebXML20POP3InboundPipelinePojo,
                    ebxmlPop3Unpacker, new Date(), new Date(), 1, 1, "ebxmlPOP3Unpacker", "ebxmlPOP3Unpacker", null );
            ebxml20DeserializerPipeletPojo.setFrontend( true );
            PipeletPojo ebxml20POP3DeserializerPipeletPojo = new PipeletPojo( ebXML20POP3InboundPipelinePojo,
                    ebxml20Deserializer, new Date(), new Date(), 1, 2, "ebxml20Deserializer", "ebxml20Deserializer",
                    null );
            ebxml20DeserializerPipeletPojo.setFrontend( true );
            PipeletPojo ebxmlPOP3PatcherPipeletPojo = new PipeletPojo( ebXML20POP3InboundPipelinePojo, ebxmlPatcher,
                    new Date(), new Date(), 1, 3, "ebxmlPatcher", "ebxmlPatcher", null );
            ebxmlPatcherPipeletPojo.setFrontend( true );

            ArrayList<PipeletPojo> ebXML20POP3InboundPipelets = new ArrayList<PipeletPojo>();
            ebXML20POP3InboundPipelets.add( ebxmlPOP3ransportReceiverPipeletPojo );
            ebXML20POP3InboundPipelets.add( ebxmlPOP3UnpackerPipeletPojo );
            ebXML20POP3InboundPipelets.add( ebxml20POP3DeserializerPipeletPojo );
            ebXML20POP3InboundPipelets.add( ebxmlPOP3PatcherPipeletPojo );

            ebXML20POP3InboundPipelinePojo.setOutbound( false );
            ebXML20POP3InboundPipelinePojo.setCreatedDate( new Date() );
            ebXML20POP3InboundPipelinePojo.setModifiedDate( new Date() );
            ebXML20POP3InboundPipelinePojo.setModifiedNxUserId( 1 );
            ebXML20POP3InboundPipelinePojo.setDescription( "ebXML 2.0 POP3 Inbound Pipeline" );
            ebXML20POP3InboundPipelinePojo.setName( "ebXML20POP3InboundPipeline" );
            ebXML20POP3InboundPipelinePojo.setFrontend( true );
            ebXML20POP3InboundPipelinePojo.setPipelets( ebXML20POP3InboundPipelets );
            ebXML20POP3InboundPipelinePojo.setTrp( ebXML2MailTRPPojo );

            PipeletPojo ebxml20SMTPSerializerPipeletPojo = new PipeletPojo( ebXML20SMTPOutboundPipelinePojo,
                    ebxml20Serializer, new Date(), new Date(), 1, 0, "ebxml20SMTPSerializer", "ebxml20SMTPSerializer",
                    null );
            ebxml20SMTPSerializerPipeletPojo.setFrontend( true );
            PipeletPojo ebxmlSMTPPackerPipeletPojo = new PipeletPojo( ebXML20SMTPOutboundPipelinePojo, ebxmlSmtpPacker,
                    new Date(), new Date(), 1, 1, "ebxmlSMTPPacker", "ebxmlSMTPPacker", null );
            ebxmlSMTPPackerPipeletPojo.setFrontend( true );
            PipeletPojo ebxmlSMTPTransportSenderPipeletPojo = new PipeletPojo( ebXML20SMTPOutboundPipelinePojo,
                    transportSender, new Date(), new Date(), 1, 2, "ebxmlSMTPTransportSender",
                    "ebxmlSMTPTransportSender", null );
            ebxmlSMTPTransportSenderPipeletPojo.setFrontend( true );
            params = new ArrayList<PipeletParamPojo>();
            param = new PipeletParamPojo( ebxmlSMTPTransportSenderPipeletPojo, new Date(), new Date(), 1, "service",
                    "SmtpSenderService" );
            params.add( param );
            ebxmlSMTPTransportSenderPipeletPojo.setPipeletParams( params );

            ArrayList<PipeletPojo> ebXML20SMTPOutboundPipelets = new ArrayList<PipeletPojo>();
            ebXML20SMTPOutboundPipelets.add( ebxml20SMTPSerializerPipeletPojo );
            ebXML20SMTPOutboundPipelets.add( ebxmlSMTPPackerPipeletPojo );
            ebXML20SMTPOutboundPipelets.add( ebxmlSMTPTransportSenderPipeletPojo );

            ebXML20SMTPOutboundPipelinePojo.setOutbound( true );
            ebXML20SMTPOutboundPipelinePojo.setCreatedDate( new Date() );
            ebXML20SMTPOutboundPipelinePojo.setModifiedDate( new Date() );
            ebXML20SMTPOutboundPipelinePojo.setModifiedNxUserId( 1 );
            ebXML20SMTPOutboundPipelinePojo.setDescription( "ebXML 2.0 SMTP Outbound Pipeline" );
            ebXML20SMTPOutboundPipelinePojo.setName( "ebXML20SMTPOutboundPipeline" );
            ebXML20SMTPOutboundPipelinePojo.setFrontend( true );
            ebXML20SMTPOutboundPipelinePojo.setPipelets( ebXML20SMTPOutboundPipelets );
            ebXML20SMTPOutboundPipelinePojo.setTrp( ebXML2MailTRPPojo );

            frontendPipelineTemplates.add( ebXML20POP3InboundPipelinePojo );
            frontendPipelineTemplates.add( ebXML20SMTPOutboundPipelinePojo );

            ChoreographyPojo httpChoreographyPojo = new ChoreographyPojo();

            PartnerPojo partnerPojo = new PartnerPojo( Constants.PARTNER_TYPE_PARTNER, new Date(), new Date(), 1,
                    "Xioma", "Custom" );
            partnerPojo.setName( "Xioma" );
            PartnerPojo localPartnerPojo = new PartnerPojo( Constants.PARTNER_TYPE_LOCAL, new Date(), new Date(), 1,
                    "Localhost", "Custom" );
            localPartnerPojo.setName( "Localhost" );

            ConnectionPojo httpConnectionPojo = new ConnectionPojo( ebXML2HttpTRPPojo, partnerPojo, 30000, 3000, true,
                    true, false, 0, 3, new Date(), new Date(), 1, "http://localhost:8080/NEXUSe2e/handler/ebxml20",
                    "Xioma HTTP" );
            partnerPojo.getConnections().add( httpConnectionPojo );

            /*
            ConnectionPojo mailConnectionPojo = new ConnectionPojo( ebXML2MailTRPPojo, partnerPojo, 30000, 3000, true,
                    true, false, 0, 3, new Date(), new Date(), 1, "test@dummy", "Xioma Mail" );
                    */
            // partnerPojo.getConnections().add( mailConnectionPojo );

            ParticipantPojo httpParticipantPojo = new ParticipantPojo( partnerPojo, httpChoreographyPojo,
                    localPartnerPojo, httpConnectionPojo, new Date(), new Date(), 1, "XiomaHttp" );
            /*
            ParticipantPojo mailParticipantPojo = new ParticipantPojo( partnerPojo, httpChoreographyPojo,
                    localPartnerPojo, mailConnectionPojo, new Date(), new Date(), 1, "XiomaMail" );
                    */

            ActionPojo sendFileActionPojo = new ActionPojo( httpChoreographyPojo, new Date(), new Date(), 1, true,
                    true, fileSaveInboundPipelinePojo, fileLoadOutboundPipelinePojo, "SendFile" );

            // Sender and Receiver components/services
            ComponentPojo httpSenderComponent = new ComponentPojo( new Date(), new Date(), 1, ComponentType.SERVICE
                    .getValue(), "HttpSender", HttpSenderService.class.getName(), "The sender service for HTTP" );
            ComponentPojo httpReceiverComponent = new ComponentPojo( new Date(), new Date(), 1, ComponentType.SERVICE
                    .getValue(), "HttpReceiver", HttpReceiverService.class.getName(), "The receiver service for HTTP" );
            ComponentPojo smtpSenderComponent = new ComponentPojo( new Date(), new Date(), 1, ComponentType.SERVICE
                    .getValue(), "SmtpSender", SmtpSender.class.getName(), "The sender service for SMTP" );
            ComponentPojo pop3ReceiverComponent = new ComponentPojo( new Date(), new Date(), 1, ComponentType.SERVICE
                    .getValue(), "Pop3Receiver", Pop3Receiver.class.getName(), "The receiver service for POP3" );

            List<ServiceParamPojo> serviceParams = null;
            ServiceParamPojo serviceParam = null;
            
            ServicePojo httpSenderService10 = new ServicePojo( httpSenderComponent, new Date(), new Date(), 1, 0,
                    "HttpSenderService_ebXML_1.0", "A sender service for HTTP", new ArrayList<ServiceParamPojo>() );
            ServicePojo httpReceiverService10 = new ServicePojo( httpReceiverComponent, new Date(), new Date(), 1, 0,
                    "HttpReceiverService_ebXML_1.0", "A receiver service for HTTP", new ArrayList<ServiceParamPojo>() );
            serviceParams = new ArrayList<ServiceParamPojo>();
            serviceParam = new ServiceParamPojo( httpReceiverService10, new Date(), new Date(), 1, "logical_name",
                    "ebxml10" );
            serviceParams.add( serviceParam );
            httpReceiverService10.setServiceParams( serviceParams );
            
            ServicePojo httpSenderService20 = new ServicePojo( httpSenderComponent, new Date(), new Date(), 1, 0,
                    "HttpSenderService", "The default sender service for HTTP", new ArrayList<ServiceParamPojo>() );
            ServicePojo httpPlainSenderService = new ServicePojo( httpSenderComponent, new Date(), new Date(), 1, 0,
                    "HttpPlainSenderService", "The default sender service for HTTPPlain",
                    new ArrayList<ServiceParamPojo>() );
            ServicePojo smtpSenderService = new ServicePojo( smtpSenderComponent, new Date(), new Date(), 1, 0,
                    "SmtpSenderService", "The default sender service for SMTP", new ArrayList<ServiceParamPojo>() );

            ServicePojo httpReceiverService20 = new ServicePojo( httpReceiverComponent, new Date(), new Date(), 1, 0,
                    "HttpReceiverService", "The default receiver service for HTTP", new ArrayList<ServiceParamPojo>() );
            serviceParams = new ArrayList<ServiceParamPojo>();
            serviceParam = new ServiceParamPojo( httpReceiverService20, new Date(), new Date(), 1, "logical_name",
                    "ebxml20" );
            serviceParams.add( serviceParam );
            httpReceiverService20.setServiceParams( serviceParams );
            
            ServicePojo httpPlainReceiverService = new ServicePojo( httpReceiverComponent, new Date(), new Date(), 1,
                    0, "HttpPlainReceiverService", "The default receiver service for HTTPPlain",
                    new ArrayList<ServiceParamPojo>() );
            serviceParams = new ArrayList<ServiceParamPojo>();
            serviceParam = new ServiceParamPojo( httpPlainReceiverService, new Date(), new Date(), 1, "logical_name",
                    "httpplain" );
            serviceParams.add( serviceParam );
            httpPlainReceiverService.setServiceParams( serviceParams );

            ServicePojo pop3ReceiverService = new ServicePojo( pop3ReceiverComponent, new Date(), new Date(), 1, 0,
                    "Pop3ReceiverService", "The default receiver service for POP3", new ArrayList<ServiceParamPojo>() );

            List<ParticipantPojo> httpParticipants = new ArrayList<ParticipantPojo>();
            Set<ActionPojo> httpActions = new HashSet<ActionPojo>();

            httpParticipants.add( httpParticipantPojo );
            // httpParticipants.add( mailParticipantPojo );
            httpActions.add( sendFileActionPojo );

            httpChoreographyPojo.setName( "GenericFile" );
            httpChoreographyPojo.setDescription( "GenericFile" );
            httpChoreographyPojo.setModifiedDate( new Date() );
            httpChoreographyPojo.setModifiedNxUserId( 1 );
            httpChoreographyPojo.setCreatedDate( new Date() );
            httpChoreographyPojo.setParticipants( httpParticipants );
            httpChoreographyPojo.setActions( httpActions );
            partnerPojo.setParticipants( httpParticipants );

            partners.add( localPartnerPojo );
            partners.add( partnerPojo );
            // LOG.trace( "partner.con.size: " + partnerPojo.getConnections().size() );
            choreographies.add( httpChoreographyPojo );
            components.add( inboundComponentPojo );
            components.add( outboundComponentPojo );
            components.add( httpSenderComponent );
            components.add( httpReceiverComponent );
            components.add( smtpSenderComponent );
            components.add( pop3ReceiverComponent );
            components.add( dbLoggerComponentPojo );

            backendPipelineTemplates.add( fileSaveInboundPipelinePojo );
            backendPipelineTemplates.add( fileLoadOutboundPipelinePojo );
            services.add( httpSenderService10 );
            services.add( httpReceiverService10 );
            services.add( httpSenderService20 );
            services.add( httpReceiverService20 );
            services.add( httpPlainSenderService );
            services.add( httpPlainReceiverService );
            services.add( smtpSenderService );
            services.add( pop3ReceiverService );

            /*
             * User configuration
             */

            // create invisible system user
            Date now = new Date();
            UserPojo systemUser = new UserPojo( "system", "System", "User", "system", now, now, 1, true,
                    false );
            // create visible admin user
            UserPojo adminUser = new UserPojo( "admin", "Administrator", "User", "0DPiKuNIrrVmD8IUCuw1hQxNqZc=", now, now, 1,
                    true, true );

            // create administrator role with wildcard access
            RolePojo adminRole = new RolePojo( "Admin", now, now, 1 );
            adminRole.setDescription( "Administrator Role" );
            Map<String, GrantPojo> grants = new HashMap<String, GrantPojo>();
            grants.put( "*", new GrantPojo( "*", now, now, 1, adminRole ) );
            adminRole.setGrants( grants );

            // set admin role
            adminUser.setRole( adminRole );

            // collect the user stuff
            users.add( systemUser );
            users.add( adminUser );
            roles.add( adminRole );

        } catch ( Exception e ) {
            e.printStackTrace();
            throw new InstantiationException( "Error creating X-ioma base system configuration - " + e.getMessage() );
        } // try/catch

    } // createBaseConfiguration

} // XiomaBaseServerConfiguration
