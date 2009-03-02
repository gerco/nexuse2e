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
package org.nexuse2e.messaging.ebxml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;

/**
 * This <code>Pipelet</code> prepares an EBXML message for a MIME message sender
 * (e.g., an SMTP sender).
 * 
 * @author jonas.reese
 */
public class MimeMessagePackager extends AbstractPipelet {

    //private static Logger LOG = Logger.getLogger( MimeMessagePacker.class );

    private Map<String, Object> parameters;

    /**
     * Default constructor.
     */
    public MimeMessagePackager() {

        parameters = new HashMap<String, Object>();
        frontendPipelet = true;
    }

    public MessageContext processMessage( MessageContext messageContext )
            throws NexusException {

        return messageContext;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter( String name ) {

        return (T) parameters.get( name );
    }

    @SuppressWarnings("unchecked")
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.EMPTY_MAP;
    }

    public Map<String, Object> getParameters() {

        return Collections.unmodifiableMap( parameters );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
    }

    public void afterPropertiesSet() throws Exception {

    }

}
