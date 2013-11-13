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
package org.nexuse2e.messaging.generic;

import java.util.Map;
import java.util.regex.Pattern;

import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.MessageContext;

/**
 * Routes a message, if a custom parameter matches the given pattern.
 * 
 * @author Sebastian Schulze
 * @date 13.04.2010
 */
public class CustomParameterRoutingPipelet extends StaticRoutingPipelet {

    public static final String CUSTOM_PARAMETER_PARAM_NAME = "customParameter";
    public static final String MATCH_PATTERN_PARAM_NAME    = "matchPattern";

    public CustomParameterRoutingPipelet() {

        parameterMap.put( CUSTOM_PARAMETER_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Custom Paramter",
                "The parameter, that must match the match pattern to route", "" ) );
        parameterMap.put( MATCH_PATTERN_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Match Pattern",
                "The pattern the custom parameter must match to route", "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {
        
        String customParameter = getParameter( CUSTOM_PARAMETER_PARAM_NAME );
        String matchPattern = getParameter( MATCH_PATTERN_PARAM_NAME );
        
        if ( customParameter != null && matchPattern != null ) {
            Map<String,String> customParameterMap = messageContext.getMessagePojo().getCustomParameters();
            if ( customParameterMap != null ) {
                String customParameterValue = customParameterMap.get( customParameter );
                if ( customParameterValue != null ) {
                    if ( Pattern.matches( matchPattern, customParameterValue ) ) {
                        super.processMessage( messageContext );
                    }
                }
            }
        }
        
        return messageContext;
    }

}
