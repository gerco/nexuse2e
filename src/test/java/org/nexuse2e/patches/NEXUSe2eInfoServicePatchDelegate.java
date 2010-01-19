package org.nexuse2e.patches;

import java.util.ArrayList;
import java.util.Date;

import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.patch.PatchException;
import org.nexuse2e.patch.PatchReporter;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ServiceParamPojo;
import org.nexuse2e.pojo.ServicePojo;

/**
 * Delegate implementation for 
 * 
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class NEXUSe2eInfoServicePatchDelegate {
    public static final String COMPONENT_NAME = "NEXUSe2eInfoWebServiceDispatcher";
    public static final String COMPONENT_DESCRIPTION = "Dispatcher component for NEXUSe2eInfoService web service";
    public static final String SERVICE_DESCRIPTION = "NEXUSe2eInfoWebServiceMountPoint";
    public static final String SERVICE_NAME = "NEXUSe2eInfoWebServiceMountPoint";
    public static final String MOUNT_POINT = "/NEXUSe2eInfoService";
    
    private PatchReporter patchReporter;
    private boolean autostart;
    private boolean success;
    

    public void executePatch() throws PatchException {
        success = false;
        
        Engine.getInstance().invalidateConfigurations();
        String key = getClass().getName();
        EngineConfiguration config = Engine.getInstance().getConfiguration( key );
        
        try {
            Date now = new Date();
            patchReporter.info( "Checking component ..." );
            String className = org.nexuse2e.integration.info.WSDispatcherService.class.getName();
            ComponentPojo component = null;
            for (ComponentPojo cp : config.getComponents()) {
                if (className.equals( cp.getClassName() )) {
                    component = cp;
                    break;
                }
            }
            if (component == null) {
                component = new ComponentPojo();
                component.setClassName( className );
                component.setName( COMPONENT_NAME );
                component.setCreatedDate( now );
                component.setDescription( COMPONENT_DESCRIPTION );
                component.setType( ComponentType.SERVICE.getValue() );
                config.updateComponent( component );
                patchReporter.info( "Component " + component.getName() + " (" + component.getDescription() + ") has been created" );
            } else {
                patchReporter.info( "Component of required type already exists: " + component.getName() + " (" + component.getDescription() );
            }

            patchReporter.info( "Creating service " + SERVICE_NAME + " (" + SERVICE_DESCRIPTION + ") ..." );
            ServicePojo service = config.getServicePojoName( SERVICE_NAME );
            if (service == null) {
                service = new ServicePojo( component, now, now, 0, 0, SERVICE_NAME );
                service.setAutostart( isAutostart() );
                service.setDescription( SERVICE_DESCRIPTION );
                ServiceParamPojo param = new ServiceParamPojo( service, now, now, 0, "url", MOUNT_POINT );
                if (service.getServiceParams() == null) {
                    service.setServiceParams( new ArrayList<ServiceParamPojo>() );
                }
                service.getServiceParams().add( param );
                config.updateService( service );
                patchReporter.info( "Created service with name " + service.getName() + " and mount point " + MOUNT_POINT + ". This is " + (isAutostart() ? "" : "NOT ") + "an autostart service." );
            } else {
                patchReporter.info( "Service with name " + service.getName() + " already exists. Please check service configuration for mount point (WS URL)." );
            }
            
            patchReporter.info( "Applying configuration ..." );
            Engine.getInstance().setCurrentConfiguration( config );
            
            patchReporter.info( "Done." );
            
            success = true;
        } catch (NexusException e) {
            patchReporter.error( e.getMessage() );
            throw new PatchException( e );
        }
    }

    public String getPatchDescription() {
        return "Creates a mount point " + (isAutostart() ? "autostart " : "") + "service with name " + SERVICE_NAME +
        " for the NEXUSe2eInfoService web service. The mount point URL will be /wshandler" + MOUNT_POINT + " by default.";
    }

    public String getPatchName() {
        return "NEXUSe2eInfoService" + (isAutostart() ? "Autostart" : "") + "Patch";
    }

    public String getVersionInformation() {
        return "1.0";
    }

    public boolean isExecutedSuccessfully() {
        return success;
    }

    public void setPatchReporter( PatchReporter patchReporter ) {
        this.patchReporter = patchReporter;
    }

    public boolean isAutostart() {
        return autostart;
    }

    public void setAutostart( boolean autostart ) {
        this.autostart = autostart;
    }
}
