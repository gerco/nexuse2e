package org.nexuse2e.service.ftp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.Configuration;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.BaseUser;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * Created: 16.07.2007
 * TODO Class documentation
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class FtpUserManager implements UserManager {
    
    private String baseDir;
    
    public FtpUserManager() throws NexusException {
    }
    
    public boolean authenticate( String login, String password ) throws FtpException {
        User user = getUserByName( login );
        boolean auth = (user != null && user.getPassword().equals( password ));
        return auth;
    }

    public void delete( String login ) throws FtpException {
    }

    public boolean doesExist( String login ) throws FtpException {
        try {
            ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
            return (cas.getPartnerByPartnerId( login ) != null);
        } catch (NexusException nex) {
            throw new FtpException( nex );
        }
    }

    public String getAdminName() throws FtpException {
        return null;
    }

    public Collection getAllUserNames() throws FtpException {
        try {
            ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
            List<PartnerPojo> partners = cas.getPartners( 0, null );
            List<String> userNames = new ArrayList<String>( partners.size() );
            for (PartnerPojo partner : partners) {
                userNames.add( partner.getPartnerId() );
            }
            return userNames;
        } catch (NexusException nex) {
            throw new FtpException( nex );
        }
    }

    /* (non-Javadoc)
     * @see org.apache.ftpserver.ftplet.UserManager#getUserByName(java.lang.String)
     */
    public User getUserByName( String login ) throws FtpException {
        try {
            ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
            PartnerPojo partner = cas.getPartnerByPartnerId( login );
            if (partner == null) {
                return null; 
            }
            BaseUser defaultUser = new BaseUser();
            defaultUser.setEnabled( true );
            defaultUser.setName( partner.getPartnerId() );
            defaultUser.setHomeDirectory( new File( baseDir, partner.getPartnerId() ).getAbsolutePath() );
            defaultUser.setPassword( partner.getName() );
            defaultUser.setWritePermission( true );
            return defaultUser;
        } catch (NexusException nex) {
            throw new FtpException( nex );
        }
    }

    public boolean isAdmin( String login ) throws FtpException {
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.ftpserver.ftplet.UserManager#save(org.apache.ftpserver.ftplet.User)
     */
    public void save( User user ) throws FtpException {
    }

    /* (non-Javadoc)
     * @see org.apache.ftpserver.ftplet.Component#configure(org.apache.ftpserver.ftplet.Configuration)
     */
    public void configure( Configuration conf ) throws FtpException {
        baseDir = conf.getString( "basedir" );
    }

    /* (non-Javadoc)
     * @see org.apache.ftpserver.ftplet.Component#dispose()
     */
    public void dispose() {
    }

    /* (non-Javadoc)
     * @see org.apache.ftpserver.ftplet.Component#setLogFactory(org.apache.commons.logging.LogFactory)
     */
    public void setLogFactory( LogFactory logFactory ) {
    }
}
