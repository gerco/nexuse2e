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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.ebxml.v20.Constants;
import org.nexuse2e.pojo.MessageLabelPojo;
import org.nexuse2e.pojo.MessagePojo;

public class StaticEbmsHeaderPipelet extends AbstractPipelet {

	public static final String ROLE_ELEMENT_VALUE_FROM = "roleFrom";
	public static final String ROLE_ELEMENT_VALUE_TO = "roleTo";
	public static final String SERVICE_ELEMENT_VALUE = "service";

	public StaticEbmsHeaderPipelet() {
		parameterMap.put(ROLE_ELEMENT_VALUE_FROM, new ParameterDescriptor(ParameterType.STRING, "Role From",
				"Sets the value for the header element role of the party sending", ""));
		parameterMap.put(ROLE_ELEMENT_VALUE_TO, new ParameterDescriptor(ParameterType.STRING, "Role to",
				"Sets the value for the header element role of the party receiving", ""));
		parameterMap.put(SERVICE_ELEMENT_VALUE, new ParameterDescriptor(ParameterType.STRING, "Service",
				"Sets the value for the header element service", ""));
	}

	@Override
	public MessageContext processMessage(MessageContext messageContext)
			throws IllegalArgumentException, IllegalStateException, NexusException {
		if (messageContext != null) {
			MessagePojo currentMassagePojo = messageContext.getMessagePojo();
			if (currentMassagePojo != null) {
				
				List<MessageLabelPojo> messageLabels = currentMassagePojo.getMessageLabels();
		        if ( messageLabels == null ) {
		            messageLabels = new ArrayList<MessageLabelPojo>();
		            currentMassagePojo.setMessageLabels( messageLabels );
		        }
				
				if (validRoleParameters()) {					
					messageLabels.add(createLabel(currentMassagePojo, Constants.PROTOCOLSPECIFIC_ROLE_FROM, getParameter(ROLE_ELEMENT_VALUE_FROM).toString()));
					messageLabels.add(createLabel(currentMassagePojo, Constants.PROTOCOLSPECIFIC_ROLE_TO, getParameter(ROLE_ELEMENT_VALUE_TO).toString()));					
				}
				
				if (validServiceParameter()) {					
					messageLabels.add(createLabel(currentMassagePojo, Constants.PROTOCOLSPECIFIC_SERVICE, getParameter(SERVICE_ELEMENT_VALUE).toString()));					
				}

			} else {
				throw new NexusException("MessagePojo is null");
			}

		} else {
			throw new NexusException("MessageContext is null");
		}
		return messageContext;
	}

	private boolean validRoleParameters() throws NexusException {
		boolean valid = false;
		String newRoleFrom = getParameter(ROLE_ELEMENT_VALUE_FROM).toString();
		String newRoleTo = getParameter(ROLE_ELEMENT_VALUE_TO).toString();

		if (StringUtils.isNotEmpty(newRoleFrom) && StringUtils.isNotEmpty(newRoleTo)) {
			valid = true;
		}
		return valid;
	}

	private boolean validServiceParameter() throws NexusException {
		boolean valid = false;
		String newService = getParameter(SERVICE_ELEMENT_VALUE).toString();

		if (StringUtils.isNotEmpty(newService)) {
			valid = true;
		}
		return valid;
	}
	
	/**
	 * Creates a messageLabelPojo to be added to a List.
	 * @param currentMassagePojo
	 * @param label
	 * @param value
	 * @return
	 * @throws NexusException
	 */
	private MessageLabelPojo createLabel(MessagePojo currentMassagePojo, String label, String value) throws NexusException {
		
		MessageLabelPojo messageLabelPojo = new MessageLabelPojo(currentMassagePojo, new Date(), new Date(), 1, label, value);
		
		return messageLabelPojo;
	}

}
