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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.UnixFTPEntryParser;
import org.apache.log4j.Logger;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SchedulerClient;
import org.nexuse2e.service.SchedulingService;
import org.nexuse2e.service.Service;
import org.nexuse2e.util.CertificatePojoSocketFactory;
import org.nexuse2e.util.FileUtil;

/**
 * Abstract superclass for FTP(S) client services.
 * 
 * @author Jonas Reese
 * @version $LastChangedRevision: $ - $LastChangedDate: $ by $LastChangedBy: $
 */
public abstract class AbstractFtpService extends AbstractService {

    private static Logger      LOG                                  = Logger.getLogger(AbstractFtpService.class);

    public static final String PARTNER_PARAM_NAME                   = "partnerId";
    public static final String DOWNLOAD_DIR_PARAM_NAME              = "downloadDir";
    public static final String ERROR_DIR_PARAM_NAME                 = "errorDir";
    public static final String FTP_TYPE_PARAM_NAME                  = "ftpType";
    public static final String CERTIFICATE_PARAM_NAME               = "certificate";
    public static final String URL_PARAM_NAME                       = "url";
    public static final String FILE_PATTERN_PARAM_NAME              = "filePattern";
    public static final String STATUS_FILE_MODE_PARAM_NAME          = "statusFileMode";
    public static final String STATUS_FILE_PARSER_REG_EX_PARAM_NAME = "statusFileParserRegEx";
    public static final String STATUS_FILE_PATTERN_PARAM_NAME       = "statusFilePattern";
    public static final String USER_PARAM_NAME                      = "username";
    public static final String PASSWORD_PARAM_NAME                  = "password";
    public static final String INTERVAL_PARAM_NAME                  = "interval";
    public static final String TRANSFER_MODE_PARAM_NAME             = "transferMode";
    public static final String RENAMING_PREFIX_PARAM_NAME           = "prefix";
    public static final String CHANGE_FILE_PARAM_NAME               = "changeFile";

    private SchedulingService  schedulingService;
    private SchedulerClient    schedulerClient;
    private boolean            fileChangeActive                     = true;

    private SimpleDateFormat   simpleDateFormat                     = new SimpleDateFormat("HH:mm");

    public AbstractFtpService() {

        schedulerClient = new FtpSchedulerClient();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap(Map<String, ParameterDescriptor> parameterMap) {

        parameterMap.put(PARTNER_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Partner", "The partner ID", ""));

        parameterMap.put(DOWNLOAD_DIR_PARAM_NAME,
                new ParameterDescriptor(ParameterType.STRING, "Download directory", "FTP download directory", new File("").getAbsolutePath()));
        parameterMap.put(ERROR_DIR_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Error directory",
                "Directory where files are stored if an error occurs", new File("").getAbsolutePath()));
        ListParameter ftpTypeDrowdown = new ListParameter();
        ftpTypeDrowdown.addElement("FTPS (encrypted)", "ftps");
        ftpTypeDrowdown.addElement("Plain FTP (not encrypted)", "ftp");
        parameterMap.put(FTP_TYPE_PARAM_NAME, new ParameterDescriptor(ParameterType.LIST, "FTP type", "FTP type", ftpTypeDrowdown));

        parameterMap.put(URL_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "URL", "Polling URL (use ftp://host.com:[port]/dir/subdir format)", ""));

        parameterMap.put(FILE_PATTERN_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "File pattern",
                "DOS-like file pattern with wildcards * and ? (examples: *.xml, ab?.*)", "*"));

        ListParameter statusFileModeDropdown = new ListParameter();
        statusFileModeDropdown.addElement("No Status File", "none");
        statusFileModeDropdown.addElement("File Per Directory", "filePerDir");
        statusFileModeDropdown.addElement("File Per File", "filePerFile");
        parameterMap.put(STATUS_FILE_MODE_PARAM_NAME, new ParameterDescriptor(ParameterType.LIST, "Status file mode",
                "The presence of a status file indicates, that it is safe to read the related data file.", statusFileModeDropdown));

        parameterMap
                .put(STATUS_FILE_PARSER_REG_EX_PARAM_NAME,
                        new ParameterDescriptor(
                                ParameterType.STRING,
                                "File name parser regex",
                                "This regular expression (syntax: java.util.regex.Pattern) is applied to each file name that matches the File Pattern in order to get substrings (back references) for status file pattern composition.",
                                ""));

        parameterMap
                .put(STATUS_FILE_PATTERN_PARAM_NAME,
                        new ParameterDescriptor(
                                ParameterType.STRING,
                                "Status file pattern",
                                "Can be a static file name like 'status.ok', or a combination of static strings and back references to a group (syntax: ${zeroBasedGroupNumber}) result of the given regular expression applied to the file name of the data file.",
                                ""));

        parameterMap.put(USER_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "User name", "The FTP user name", "anonymous"));

        parameterMap.put(PASSWORD_PARAM_NAME, new ParameterDescriptor(ParameterType.PASSWORD, "Password", "The FTP password", ""));

        parameterMap.put(INTERVAL_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Interval (minutes)",
                "Connect every n minutes to look for new files", "5"));

        parameterMap.put(RENAMING_PREFIX_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "File Prefix",
                "Prefix is prepended to successfully transfered files, instead of file delete", "5"));

        parameterMap.put(CHANGE_FILE_PARAM_NAME,
                new ParameterDescriptor(ParameterType.BOOLEAN, "Change/Delete File", "Rename/Delete file active", Boolean.TRUE));

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
        if (cas != null) {
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
    }

    @Override
    public void initialize(EngineConfiguration config) throws InstantiationException {

        Boolean tempFileChangeActive = (Boolean) getParameter(CHANGE_FILE_PARAM_NAME);
        if (tempFileChangeActive != null) {
            fileChangeActive = tempFileChangeActive.booleanValue();
            LOG.debug("fileChangeActive: " + fileChangeActive);
        }

        super.initialize(config);
    }

    @Override
    public void start() {

        if (getStatus() == BeanStatus.ACTIVATED) {

            try {
                ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
                schedulingService = null;
                for (Service service : cas.getServiceInstances()) {
                    if (service instanceof SchedulingService) {
                        schedulingService = (SchedulingService) service;
                    }
                }
                if (schedulingService == null) {
                    throw new NexusException("No ScheduleService implementation found");
                }

                String intervalString = getParameter(INTERVAL_PARAM_NAME);
                if (StringUtils.isEmpty(intervalString)) {
                    LOG.warn("No interval definition found!");
                    schedulingService.registerClient(schedulerClient, (5 * 60000));
                } else if (intervalString.indexOf(":") != -1) {
                    StringTokenizer st = new StringTokenizer(intervalString, ",");
                    ArrayList<Date> times = new ArrayList<Date>();
                    while (st.hasMoreTokens()) {
                        String time = st.nextToken();
                        LOG.debug("Parsing scheduled time: " + time);
                        Date scheduledTime = simpleDateFormat.parse(time);
                        Calendar tempCalendar = new GregorianCalendar();
                        tempCalendar.setTime(scheduledTime);
                        Calendar scheduledCalendar = new GregorianCalendar();
                        scheduledCalendar.set(Calendar.HOUR_OF_DAY, tempCalendar.get(Calendar.HOUR_OF_DAY));
                        scheduledCalendar.set(Calendar.MINUTE, tempCalendar.get(Calendar.MINUTE));
                        scheduledCalendar.set(Calendar.SECOND, 0);
                        scheduledCalendar.set(Calendar.MILLISECOND, 0);
                        scheduledTime = new Date(scheduledCalendar.getTimeInMillis());
                        times.add(scheduledTime);
                        LOG.debug("Scheduled time: " + scheduledTime);
                    }
                    if (!times.isEmpty()) {
                        schedulingService.registerClient(schedulerClient, times);

                    }
                } else {
                    int interval = Integer.parseInt(intervalString) * 60000;
                    LOG.debug("Using interval: " + interval);
                    schedulingService.registerClient(schedulerClient, interval);
                }

                super.start();
                LOG.debug("FtpPollingReceiver service started");
            } catch (Exception ex) {
                LOG.error("FtpPollingReceiver service could not be started", ex);
                status = BeanStatus.ERROR;
            }
        } else {
            LOG.error("Service not in correct state to be started: " + status);
        }
    }

    @Override
    public void stop() {

        if ((getStatus() == BeanStatus.STARTED) || (getStatus() == BeanStatus.ERROR)) {
            if (schedulingService != null) {
                schedulingService.deregisterClient(schedulerClient);
            }
            LOG.debug("FtpPollingReceiver service stopped");
            super.stop();
        }
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    /**
     * Process any files found.
     * 
     * @param file
     *            File found to be processed.
     * @param errorDir
     *            The error directory.
     * @param partnerId
     *            The partner ID.
     */
    protected abstract void processFile(File file, File errorDir, String partnerId) throws NexusException;

    public CertificatePojo getSelectedCertificate(String certId) throws NexusException {

        CertificatePojo cert;
        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        if (certId == null || certId.length() == 0) {
            List<CertificatePojo> certs = cas.getCertificates(CertificateType.LOCAL.getOrdinal(), null);
            if (certs == null || certs.isEmpty() || certs.get(0) == null) {
                throw new NexusException("No appropriate certificate found for FTPS server authentication");
            }
            cert = certs.get(0);
        } else {
            cert = cas.getCertificateByNxCertificateId(CertificateType.LOCAL.getOrdinal(), Integer.parseInt(certId));
            if (cert == null) {
                throw new NexusException("No local certificate with ID " + certId + " could be found for FTPS server authentication");
            }
        }
        return cert;
    }

    class FtpSchedulerClient implements SchedulerClient {

        protected static final int STATUS_FILE_MODE_NONE     = 0;
        protected static final int STATUS_FILE_MODE_PER_DIR  = 1;
        protected static final int STATUS_FILE_MODE_PER_FILE = 2;

        public void scheduleNotify() {

            boolean ssl = false;
            ListParameter ftpTypeSel = getParameter(FTP_TYPE_PARAM_NAME);
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
                URL url = new URL((String) getParameter(URL_PARAM_NAME));
                int port = url.getPort() >= 0 ? url.getPort() : (ssl ? 990 : 21);

                if (ssl) {
                    ListParameter certSel = getParameter(CERTIFICATE_PARAM_NAME);
                    String certId = certSel.getSelectedValue();
                    getSelectedCertificate(certId);
                    ftp.setSocketFactory(new CertificatePojoSocketFactory(getSelectedCertificate(certId)));
                }

                ftp.connect(url.getHost(), port);
                LOG.trace("Connected to " + url.getHost() + ".");

                int reply = ftp.getReplyCode();

                if (!FTPReply.isPositiveCompletion(reply)) {
                    throw new NexusException("FTP server refused connection.");
                }
                ftp.enterLocalPassiveMode();

                String user = getParameter(USER_PARAM_NAME);
                String password = getParameter(PASSWORD_PARAM_NAME);
                boolean success = ftp.login(user, password);
                if (!success) {
                    throw new NexusException("FTP authentication failed: " + ftp.getReplyString());
                }
                LOG.debug("Connected to " + url.getHost() + ", successfully logged in user " + user);

                ListParameter transfermode = getParameter(TRANSFER_MODE_PARAM_NAME);
                if (transfermode != null && "binary".equals(transfermode.getSelectedValue())) {
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Entered binary mode");
                    }
                }

                if (ssl) {
                    reply = ftp.sendCommand("PROT P");
                    if (!FTPReply.isPositiveCompletion(reply)) {
                        throw new NexusException("PROT P command failed with code " + reply);
                    }
                }

                LOG.trace("Directory URL Path: " + url.getPath());
                String directory = url.getPath();
                if (directory.startsWith("/")) {
                    directory = directory.substring(1);
                }

                if (StringUtils.isNotEmpty(directory)) {
                    LOG.trace("Directory requested: " + directory);
                    success = ftp.changeWorkingDirectory(directory);
                    if (!success) {
                        LOG.error("FTP server did not change directory!");
                    }
                }
                LOG.trace("Working Directory: " + ftp.printWorkingDirectory());

                String localDir = getParameter(DOWNLOAD_DIR_PARAM_NAME);

                String filePattern = getParameter(FILE_PATTERN_PARAM_NAME);
                List<File> localFiles = new ArrayList<File>();
                FTPFile[] files = ftp.listFiles();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Number of files in directory: " + files.length + ", checking against pattern " + filePattern);
                }
                if (LOG.isTraceEnabled()) {
                    for (FTPFile file : files) {
                        LOG.trace(file.getName());
                    }
                }

                File errorDir = new File((String) getParameter(ERROR_DIR_PARAM_NAME));

                // status file info
                ListParameter statusFileModeSel = getParameter(STATUS_FILE_MODE_PARAM_NAME);
                int statusFileMode = STATUS_FILE_MODE_NONE;
                if (statusFileModeSel != null) {
                    if ("filePerDir".equals(statusFileModeSel.getSelectedValue())) {
                        statusFileMode = STATUS_FILE_MODE_PER_DIR;
                    } else if ("filePerFile".equals(statusFileModeSel.getSelectedValue())) {
                        statusFileMode = STATUS_FILE_MODE_PER_FILE;
                    }
                }
                String statusFileName = null;
                boolean statusFileExists = false;

                // rename prefix
                String prefix = getParameter(RENAMING_PREFIX_PARAM_NAME);

                String regEx = FileUtil.dosStyleToRegEx(filePattern);
                for (FTPFile file : files) {
                    if ((file != null) && (file.getName() != null)) {
                        if (Pattern.matches(regEx, file.getName())) {
                            // create status file name, if necessary
                            boolean loadFile = false;
                            if (statusFileMode > STATUS_FILE_MODE_NONE) {
                                statusFileName = buildStatusFileName(file.getName(), (String) getParameter(STATUS_FILE_PARSER_REG_EX_PARAM_NAME),
                                        (String) getParameter(STATUS_FILE_PATTERN_PARAM_NAME));
                            }
                            // if status file check is enabled, find the file
                            if (statusFileName != null) {
                                // need current file list, because status files can be deleted each iteration
                                FTPFile[] tmpStatusFiles = ftp.listFiles();
                                statusFileExists = false; // reset even in dir mode, cause we will find it again
                                for (int i = 0; !statusFileExists && i < tmpStatusFiles.length; i++) {
                                    statusFileExists = statusFileName.equals(tmpStatusFiles[i].getName());
                                }

                                if (LOG.isDebugEnabled()) {
                                    if (statusFileExists) {
                                        LOG.debug("Found status file: " + statusFileName);
                                    } else {
                                        LOG.debug("Status file not found: " + statusFileName);
                                    }
                                }
                                // should i stay or should i go go go?
                                loadFile = statusFileExists;
                            } else {
                                loadFile = true;
                            }
                            // if and only if
                            if (loadFile) {
                                File localFile = new File(localDir, file.getName());
                                FileOutputStream fout = new FileOutputStream(localFile);
                                success = ftp.retrieveFile(file.getName(), fout);
                                fout.flush();
                                fout.close();
                                if (!success) {
                                    LOG.error("Could not retrieve file " + file.getName());
                                } else {
                                    try {

                                        processFile(localFile, errorDir, (String) getParameter(PARTNER_PARAM_NAME));

                                        boolean error = false;
                                        if (fileChangeActive) {
                                            if (StringUtils.isEmpty(prefix)) {
                                                if (!ftp.deleteFile(file.getName())) {
                                                    LOG.error("Could not delete file " + file.getName());
                                                    error = true;
                                                }
                                                // delete status file
                                                if (statusFileExists && statusFileMode == STATUS_FILE_MODE_PER_FILE && !ftp.deleteFile(statusFileName)) {
                                                    LOG.error("Could not delete status file " + statusFileName);
                                                    error = true;
                                                }
                                            } else {
                                                if (!ftp.rename(file.getName(), prefix + file.getName())) {
                                                    LOG.error("Could not rename file from " + file.getName() + " to " + prefix + file.getName());
                                                    error = true;
                                                }
                                                // rename status file
                                                if (statusFileExists && statusFileMode == STATUS_FILE_MODE_PER_FILE
                                                        && !ftp.rename(statusFileName, prefix + statusFileName)) {
                                                    LOG.error("Could not rename status file from " + statusFileName + " to " + prefix + statusFileName);
                                                    error = true;
                                                }
                                            }
                                        }

                                        if (!error) {
                                            localFiles.add(localFile);
                                        }
                                    } catch (Exception e) {
                                        LOG.error("Error processing file " + file.getName() + ": " + e, e);
                                    }
                                }
                            }
                        }
                    }
                }

                // delete/rename status file, if in STATUS_FILE_MODE_PER_DIR mode
                if (statusFileExists && statusFileMode == STATUS_FILE_MODE_PER_DIR && fileChangeActive) {
                    if (StringUtils.isEmpty(prefix)) {
                        // delete status file
                        if (!ftp.deleteFile(statusFileName)) {
                            LOG.error("Could not delete status file " + statusFileName);
                        }
                    } else {
                        // rename status file
                        if (!ftp.rename(statusFileName, prefix + statusFileName)) {
                            LOG.error("Could not rename status file from " + statusFileName + " to " + prefix + statusFileName);
                        }
                    }
                }

                /*
                 * for ( File localFile : localFiles ) {
                 * processFile( localFile, errorDir, (String) getParameter( PARTNER_PARAM_NAME ) );
                 * }
                 */

                // process files
            } catch (Exception e) {
                LOG.error("Error polling FTP account (" + getParameter(URL_PARAM_NAME) + "): ", e);
            } finally {
                if (ftp.isConnected()) {
                    try {
                        ftp.logout();
                        LOG.trace("Logged out.");
                    } catch (IOException ioe) {
                    }
                    try {
                        ftp.disconnect();
                    } catch (IOException ioe) {
                    }
                }
            }
        }

        protected final Pattern BACK_REFERENCE_PATTERN = Pattern.compile("\\$\\{\\d+\\}");

        protected String buildStatusFileName(String fileName, String parserRegEx, String statusFilePattern) {
            String result = statusFilePattern;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Building status file name from fileName='" + fileName + "', parserRegEx='" + parserRegEx + "', statusFilePattern='"
                        + statusFilePattern + "'");
            }
            // this only makes sense, if every parameter is set
            if (fileName != null && statusFilePattern != null && parserRegEx != null) {
                // let's see, whether the file pattern contains a back-reference to the file name
                Matcher m1 = BACK_REFERENCE_PATTERN.matcher(statusFilePattern);
                // and whether the file name matches the parser regex
                Matcher m2 = Pattern.compile(parserRegEx).matcher(fileName);
                if (m2.matches()) {
                    // for each back-reference get the group number, extract the group from the file name, and substitute it
                    while (m1.find()) {
                        MatchResult mr = m1.toMatchResult();
                        String backRef = mr.group();
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("backRef: " + backRef);
                        }
                        Integer groupNo = Integer.parseInt(backRef.substring(2, backRef.length() - 1));
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("groupNo: " + groupNo);
                        }
                        if (m2.groupCount() >= groupNo) {
                            if (LOG.isTraceEnabled()) {
                                LOG.trace("group: " + m2.group(groupNo));
                            }
                            result = result.replace(backRef, m2.group(groupNo));
                        }
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("result: " + result);
                        }
                    }
                } else {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("parser regex does not match file name");
                    }
                }
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("statusFileName: " + result);
            }
            // done
            return result;
        }
    }
}
