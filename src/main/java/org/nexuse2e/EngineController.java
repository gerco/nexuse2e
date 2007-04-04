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
package org.nexuse2e;

import org.apache.log4j.Logger;

/**
 * @author mbreilmann
 *
 */
public class EngineController {

    private static Logger        LOG                       = Logger.getLogger( EngineController.class );

    private EngineControllerStub engineControllerStub      = null;
    private String               engineControllerStubClass = "org.nexuse2e.DefaultEngineControllerStub";

    /**
     * 
     */
    public void initialize() {
        LOG.debug( "Initializing..." );
        if ( engineControllerStub == null ) {
            try {
                engineControllerStub = (EngineControllerStub) Class.forName( engineControllerStubClass ).newInstance();
            } catch ( InstantiationException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch ( ClassNotFoundException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    } // initialize

    /**
     * 
     */
    public void teardown() {

    } // teardown

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

} // EngineController
