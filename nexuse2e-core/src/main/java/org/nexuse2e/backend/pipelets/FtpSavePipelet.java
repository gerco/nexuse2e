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
package org.nexuse2e.backend.pipelets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import javax.net.ssl.SSLException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.UnixFTPEntryParser;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.ftp.AbstractFtpService;
import org.nexuse2e.util.CertificatePojoSocketFactory;
import org.nexuse2e.util.ServerPropertiesUtil;

/**
 * This pipelet implementation uploads an incoming message to an FTP(S) server on the
 * backend side.
 * 
 * @author Jonas Reese
 * @version $LastChangedRevision: $ - $LastChangedDate: $ by $LastChangedBy: $
 */
public class FtpSavePipelet extends AbstractPipelet {

    private static Logger      LOG                        = Logger.getLogger(FtpSavePipelet.class);

    public static final String FTP_TYPE_PARAM_NAME        = "ftpType";
    public static final String CERTIFICATE_PARAM_NAME     = "certificate";
    public static final String URL_PARAM_NAME             = "url";
    public static final String FILE_PATTERN_PARAM_NAME    = "filePattern";
    public static final String USER_PARAM_NAME            = "username";
    public static final String PASSWORD_PARAM_NAME        = "password";
    public static final String TRANSFER_MODE_PARAM_NAME   = "transferMode";
    public static final String RENAMING_PREFIX_PARAM_NAME = "prefix";
    public static final String CHANGE_FILE_PARAM_NAME     = "changeFile";

    private AbstractFtpService ftpService;

    public FtpSavePipelet() {

        ListParameter ftpTypeDrowdown = new ListParameter();
        ftpTypeDrowdown.addElement("FTPS (encrypted)", "ftps");
        ftpTypeDrowdown.addElement("Plain FTP (not encrypted)", "ftp");
        parameterMap.put(FTP_TYPE_PARAM_NAME, new ParameterDescriptor(ParameterType.LIST, "FTP type", "FTP type", ftpTypeDrowdown));

        parameterMap.put(URL_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "URL", "Polling URL (use ftp://host.com:[port]/dir/subdir format)", ""));

        parameterMap.put(FILE_PATTERN_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "File name pattern",
                "Pattern for uploaded file name. Variable expressions allowed.", "${nexus.message.partnerid}_${nexus.message.createdDate}.xml"));

        parameterMap.put(USER_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "User name", "The FTP user name", "anonymous"));

        parameterMap.put(PASSWORD_PARAM_NAME, new ParameterDescriptor(ParameterType.PASSWORD, "Password", "The FTP password", ""));

        ListParameter transferModeListParam = new ListParameter();
        transferModeListParam.addElement("Auto", "auto");
        transferModeListParam.addElement("Binary", "binary");
        transferModeListParam.addElement("ASCII", "ascii");
        parameterMap.put(TRANSFER_MODE_PARAM_NAME, new ParameterDescriptor(ParameterType.LIST, "Transfer mode",
                "Use Automatic/Binary/ASCII transfer mode (default is Auto)", transferModeListParam));

        final ParameterDescriptor certsParamDesc = new ParameterDescriptor(ParameterType.LIST, "Client certificate",
                "Use this certificate for client authentication", new ListParameter());
        certsParamDesc.setUpdater(new Runnable() {

            public void run() {

                addCertificatesToDropdown((ListParameter) certsParamDesc.getDefaultValue());
            }
        });
        parameterMap.put(CERTIFICATE_PARAM_NAME, certsParamDesc);
    }

    private void addCertificatesToDropdown(ListParameter certsDropdown) {

        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        try {
            List<CertificatePojo> certs = cas.getCertificates(CertificateType.LOCAL.getOrdinal(), null);
            if (certs != null) {
                for (CertificatePojo cert : certs) {
                    String label = cert.getName();
                    if (label == null || "".equals(label.trim())) {
                        label = "Certificate #" + cert.getNxCertificateId();
                    }
                    if (cert.getDescription() != null && !"".equals(cert.getDescription())) {
                        label += " (" + cert.getDescription() + ")";
                    }
                    String value = Integer.toString(cert.getNxCertificateId());
                    if (certsDropdown.getElement(value) == null) {
                        certsDropdown.addElement(label, value);
                    }
                }
            }
        } catch (NexusException nex) {
            LOG.error("Could not retrieve local certificate list", nex);
        }
    }

    @Override
    public MessageContext processMessage(MessageContext messageContext) throws IllegalArgumentException, IllegalStateException, NexusException {

        boolean ssl = false;
        ListParameter ftpTypeSel = getParameter(AbstractFtpService.FTP_TYPE_PARAM_NAME);
        if (!"ftp".equals(ftpTypeSel.getSelectedValue())) {
            ssl = true;
        }

        FTPClient ftp = new FTPClient();
        ftp.setParserFactory(new DefaultFTPFileEntryParserFactory() {

            @Override
            public FTPFileEntryParser createUnixFTPEntryParser() {

                return (FTPFileEntryParser) new UnixFTPEntryParser() {

                    @Override
                    public String readNextEntry(BufferedReader reader) throws IOException {

                        try {
                            return super.readNextEntry(reader);
                        } catch (SSLException e) {
                            // since the SSL input stream seems to be throwing
                            // this exception when trying to read from a zero-length
                            // stream, we avoid this exception by catching it
                            return null;
                        }
                    }
                };
            }
        });
        try {
            URL url = new URL((String) getParameter(AbstractFtpService.URL_PARAM_NAME));
            int port = url.getPort() >= 0 ? url.getPort() : (ssl ? 990 : 21);

            if (ssl) {
                ListParameter certSel = getParameter(AbstractFtpService.CERTIFICATE_PARAM_NAME);
                String certId = certSel.getSelectedValue();
                ftpService.getSelectedCertificate(certId);
                ftp.setSocketFactory(new CertificatePojoSocketFactory(ftpService.getSelectedCertificate(certId)));
            }

            ftp.connect(url.getHost(), port);
            LOG.trace(new LogMessage("Connected to " + url.getHost() + ".", messageContext.getMessagePojo()));

            int reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new NexusException("FTP server refused connection.");
            }
            ftp.enterLocalPassiveMode();

            String user = getParameter(AbstractFtpService.USER_PARAM_NAME);
            String password = getParameter(AbstractFtpService.PASSWORD_PARAM_NAME);
            boolean success = ftp.login(user, password);
            if (!success) {
                throw new NexusException("FTP authentication failed: " + ftp.getReplyString());
            }
            LOG.debug(new LogMessage("Connected to " + url.getHost() + ", successfully logged in user " + user, messageContext.getMessagePojo()));
            if (ssl) {
                reply = ftp.sendCommand("PROT P");
                if (!FTPReply.isPositiveCompletion(reply)) {
                    throw new NexusException("PROT P command failed with code " + reply);
                }
            }

            LOG.trace(new LogMessage("Directory URL Path: " + url.getPath(), messageContext.getMessagePojo()));
            String directory = url.getPath();
            if (directory.startsWith("/")) {
                directory = directory.substring(1);
            }

            if (StringUtils.isNotEmpty(directory)) {
                LOG.trace(new LogMessage("Directory requested: " + directory, messageContext.getMessagePojo()));
                success = ftp.changeWorkingDirectory(directory);
                if (!success) {
                    throw new NexusException("FTP server did not change directory!");
                }
            }

            String fileNamePattern = (String) getParameter(FILE_PATTERN_PARAM_NAME);
            if (StringUtils.isEmpty(fileNamePattern)) {
                throw new NexusException("FTP remote file name pattern must not be empty");
            }

            for (MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads()) {
                try {
                    String fileName = writePayload(ftp, fileNamePattern, payload, messageContext);
                    LOG.trace(new LogMessage("Wrote output file: " + fileName.toString(), messageContext.getMessagePojo()));
                } catch (FileNotFoundException e) {
                    throw new NexusException(e);
                } catch (IOException e) {
                    throw new NexusException(e);
                }
            }
            LOG.trace(new LogMessage("Working Directory: " + ftp.printWorkingDirectory(), messageContext.getMessagePojo()));
            return messageContext;
            // process files
        } catch (Exception e) {
            throw new NexusException(e);
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.logout();
                    LOG.trace(new LogMessage("Logged out.", messageContext.getMessagePojo()));
                } catch (IOException ioe) {
                }
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
    }

    private String writePayload(FTPClient ftp, String fileNamePattern, MessagePayloadPojo payload, MessageContext messageContext) throws FileNotFoundException,
            IOException {

        String fileName = ServerPropertiesUtil.replaceServerProperties(fileNamePattern, messageContext);

        OutputStream fileOutputStream = ftp.appendFileStream(fileName);
        fileOutputStream.write(payload.getPayloadData());
        fileOutputStream.flush();
        fileOutputStream.close();

        return fileName;
    }
}
