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
package org.nexuse2e.backend.pipelets;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.MappingPojo;

/**
 * This <code>Pipelet</code> implementation uses the global NEXUS mapping table in order
 * to map choreography, action and partner IDs to different values.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class MessageMappingPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( MessageMappingPipelet.class );


    public static final String ACTION_MAP_CATEGORY_PARAMETER_NAME = "actionMapCategory";
    public static final String CHOREOGRAPHY_MAP_CATEGORY_PARAMETER_NAME = "choreographyMapCategory";
    public static final String PARTNER_MAP_CATEGORY_PARAMETER_NAME = "partnerMapCategory";
    
    
    public MessageMappingPipelet() {
        setFrontendPipelet( false );
        
        parameterMap.put( ACTION_MAP_CATEGORY_PARAMETER_NAME,
                new ParameterDescriptor(
                        ParameterType.STRING,
                        "Action map category name",
                        "Enter category name or leave blank if actions shall not be mapped",
                        "" ) );
        parameterMap.put( CHOREOGRAPHY_MAP_CATEGORY_PARAMETER_NAME,
                new ParameterDescriptor(
                        ParameterType.STRING,
                        "Choreography map category name",
                        "Enter category name or leave blank if choreographies shall not be mapped",
                        "" ) );
        parameterMap.put( PARTNER_MAP_CATEGORY_PARAMETER_NAME,
                new ParameterDescriptor(
                        ParameterType.STRING,
                        "Partner map category name",
                        "Enter category name or leave blank if partners shall not be mapped",
                        "" ) );
    }
    
    
    @Override
    public MessageContext processMessage( MessageContext messageContext )
    throws IllegalArgumentException, IllegalStateException, NexusException {

        if (messageContext.getMessagePojo() == null) {
            // nothing we can do
            LOG.warn( getClass().getSimpleName() + " cannot process message because message is not set on message context" );
            return messageContext;
        }
        
        String actionCategory = getParameter( ACTION_MAP_CATEGORY_PARAMETER_NAME );
        String choreographyCategory = getParameter( CHOREOGRAPHY_MAP_CATEGORY_PARAMETER_NAME );
        String partnerCategory = getParameter( PARTNER_MAP_CATEGORY_PARAMETER_NAME );
        
        // map choreography
        if (!StringUtils.isBlank( choreographyCategory )) {
            choreographyCategory = choreographyCategory.trim();
            if (messageContext.getMessagePojo().getConversation() != null) {
                ChoreographyPojo choreography = messageContext.getMessagePojo().getConversation().getChoreography();
                if (choreography != null) {
                    MappingPojo mp = Engine.getInstance().getCurrentConfiguration().getMappingByCategoryDirectionAndKey(
                            choreographyCategory, true, choreography.getName() );
                    String mappedChoreography = (mp == null ? null : mp.getRightValue());
                    if (!StringUtils.isBlank( mappedChoreography )) {
                        // do it
                        ChoreographyPojo newChoreography = Engine.getInstance().getCurrentConfiguration().getChoreographyByChoreographyId(
                                mappedChoreography );
                        if (newChoreography != null) {
                            // set new choreography on message and message context
                            messageContext.setActionSpecificKey(
                                    new ActionSpecificKey( messageContext.getActionSpecificKey().getActionId(), mappedChoreography ) );
                            if (messageContext.getMessagePojo().getConversation() != null) {
                                messageContext.getMessagePojo().getConversation().setChoreography( newChoreography );
                                messageContext.getConversation().setChoreography( newChoreography );
                            }
                            messageContext.setChoreography( newChoreography );
                        } else {
                            LOG.warn(
                                    getClass().getSimpleName() +
                                    " cannot map choreography because no choreography with ID " +
                                    mappedChoreography + " was found" );
                        }
                    } else {
                        LOG.warn( getClass().getSimpleName() + " cannot map action because mapping for key " +
                                choreography.getName() + " and category " + choreographyCategory + " was not found" );
                    }
                } else {
                    LOG.warn( getClass().getSimpleName() + " cannot map choreography because no choreography is set on conversation" );
                }
            } else {
                LOG.warn( getClass().getSimpleName() + " cannot map choreography because no conversation is set on message" );
            }
        }
        
        // map action
        if (!StringUtils.isBlank( actionCategory )) {
            actionCategory = actionCategory.trim();
            ActionPojo action = messageContext.getMessagePojo().getAction();
            ChoreographyPojo choreography = messageContext.getMessagePojo().getConversation() != null ?
                    messageContext.getMessagePojo().getConversation().getChoreography() : null;
            if (action != null && choreography != null) {
                MappingPojo mp = Engine.getInstance().getCurrentConfiguration().getMappingByCategoryDirectionAndKey(
                        actionCategory, true, action.getName() );
                String mappedAction = (mp == null ? null : mp.getRightValue());
                if (!StringUtils.isBlank( mappedAction )) {
                    // do it
                    ActionPojo newAction = Engine.getInstance().getCurrentConfiguration().getActionFromChoreographyByActionId(
                            choreography, mappedAction );
                    if (newAction != null) {
                        // set new action on message and message context
                        messageContext.setActionSpecificKey(
                                new ActionSpecificKey( mappedAction, messageContext.getActionSpecificKey().getChoreographyId() ) );
                        messageContext.getMessagePojo().setAction( newAction );
                        messageContext.getConversation().setCurrentAction( newAction );
                    } else {
                        LOG.warn(
                                getClass().getSimpleName() +
                                " cannot map action because no action with ID " +
                                mappedAction + " was found in choreography " +
                                messageContext.getMessagePojo().getConversation().getChoreography().getName() );
                    }
                } else {
                    LOG.warn( getClass().getSimpleName() + " cannot map action because mapping for key " + action.getName() + " and category " + actionCategory + " was not found" );
                }
            } else {
                LOG.warn( getClass().getSimpleName() + " cannot map action because action is not set on message" );
            }
        }
        
        // map partner
        if (!StringUtils.isBlank( partnerCategory )) {
            
        }
        
        return messageContext;
    }
}
