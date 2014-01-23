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
package org.nexuse2e;

import org.apache.log4j.Logger;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.service.Service;

/**
 * @author mbreilmann
 *
 */
public class EngineController {

    private static Logger        LOG                       = Logger.getLogger( EngineController.class );

    private EngineControllerStub engineControllerStub      = null;
    private String               engineControllerStubClass = "org.nexuse2e.DefaultEngineControllerStub";
    private Engine               engine                    = null;

    private EngineMonitor        engineMonitor             = null;
   
    private AdvancedControllerInterface advancedController = null;
    private String               advancedControllerClass   = "org.nexuse2e.DefaultAdvancedController";
    
    

    /**
     * This method is called by spring, and does some pre-start initialization work.
     */
    public void initialize() {
        LOG.debug( "Initializing..." );
        if ( engineControllerStub == null ) {
            try {
                engineControllerStub = (EngineControllerStub) Class.forName( engineControllerStubClass ).newInstance();
                LOG.debug( "EngineControllerStub instantiated" );

                advancedController = (AdvancedControllerInterface) Class.forName( advancedControllerClass ).newInstance();
                LOG.debug( "AdvancedController instantiated" );

                if ( engine != null ) {
                    engine.setEngineController( this );
                    engine.changeStatus( BeanStatus.INSTANTIATED );
                } else {
                    LOG.error( "No Engine instance found, exiting..." );
                    return;
                }

                if(engineMonitor == null) {
                    LOG.error("engine monitor not configured properly");
                    return;
                }
                if ( engineMonitor.isAutoStart() ) {
                    engineMonitor.start();
                }
                engineControllerStub.initialize();
                engine.changeStatus( BeanStatus.STARTED );

            } catch ( InstantiationException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            } catch ( ClassNotFoundException e ) {
                e.printStackTrace();
            }
        }

    } // initialize

    /**
     * Return a TransportReceiver for the specified controller ID.
     * The call will be handled by the <code>EngineControllerStub</code>.
     * @param controllerId The ID of the controller to wrap.
     * @return The wrapper for the specified controller.
     */
    public Pipelet getTransportReceiver( String controllerId, String className ) {

        Pipelet receiver = null;

        if ( engineControllerStub != null ) {
            receiver = engineControllerStub.getTransportReceiver( controllerId, className );
        }

        return receiver;
    }

    /**
     * @param transportService
     * @return
     */
    public Service getServiceWrapper(Service transportService) {
        
        Service service = null;

        if ( engineControllerStub != null ) {
            service = engineControllerStub.getServiceWrapper( transportService );
        }
        return service;
    }
    
    /**
     * 
     */
    public void shutdown() {

        engineMonitor.stop();
        if ( engine != null ) {
            engine.shutdown();
        }

    } // shutdown

    /**
     * @return
     */
    public EngineControllerStub getEngineControllerStub() {

        return engineControllerStub;
    }

    /**
     * @param engineControllerStub
     */
    public void setEngineControllerStub( EngineControllerStub engineControllerStub ) {

        this.engineControllerStub = engineControllerStub;
    }

    /**
     * @return
     */
    public String getEngineControllerStubClass() {

        return engineControllerStubClass;
    }

    /**
     * @param engineControllerStubClass
     */
    public void setEngineControllerStubClass( String engineControllerStubClass ) {

        this.engineControllerStubClass = engineControllerStubClass;
    }

    /**
     * @return
     */
    public Engine getEngine() {

        return engine;
    }

    /**
     * @param engine
     */
    public void setEngine( Engine engine ) {

        this.engine = engine;
    }

    /**
     * @return the engineMonitor
     */
    public EngineMonitor getEngineMonitor() throws NexusException{

        if(engineMonitor == null) {
            throw new NexusException("engine monitor not configured");
        }
        return engineMonitor;
    }

    /**
     * @param engineMonitor the engineMonitor to set
     */
    public void setEngineMonitor( EngineMonitor engineMonitor ) {

        this.engineMonitor = engineMonitor;
    }
    
    public AdvancedControllerInterface getAdvancedController() {
		return advancedController;
	}

	public void setAdvancedController(AdvancedControllerInterface advancedController) {
		this.advancedController = advancedController;
	}

	public String getAdvancedControllerClass() {
		return advancedControllerClass;
	}

	public void setAdvancedControllerClass(String advancedControllerClass) {
		this.advancedControllerClass = advancedControllerClass;
	}

} // EngineController
