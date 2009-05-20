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
package org.nexuse2e.integration.info;

import java.util.Map;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.nexuse2e.DynamicWSDispatcherServlet;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.service.AbstractService;

/**
 * A service that dynamically registers the NEXUSe2e statistics web service.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class WSDispatcherService extends AbstractService {

    private static Logger       LOG                     = Logger.getLogger( WSDispatcherService.class );

    private static final String URL_PARAM_NAME          = "url";

    private Endpoint            endpoint;

    
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Web service URL",
                "The last part of the web service URL (e.g. /sendMessage)", "" ) );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    public void start() {

        if ( getStatus() == BeanStatus.STARTED ) {
            return;
        }

        String url = (String) getParameter( URL_PARAM_NAME );
        LOG.debug( "Web service URL extension: " + url );

        try {
            Object implementor = new NEXUSe2eInfoServiceImpl();
            endpoint = Endpoint.publish( url, implementor );
            super.start();
        } catch ( Exception ex ) {
            ex.printStackTrace();
            LOG.error( ex );
        }
    }

    public void stop() {

        if ( endpoint != null ) {
            endpoint.stop();
            endpoint = null;
            DynamicWSDispatcherServlet.getInstance().reinitialize();
        }
        super.stop();
    }
}
