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
package org.nexuse2e.backend.pipelets;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This backend pipelet implementation processes a configured freemarker template and
 * sets the template output as {@code MessageContext.data}.
 * 
 * The template context consists of the message context and some convenience fields also
 * available through the message context:
 * <ul>
 *   <li>{@code messageContext}: The message context (see {@link MessageContext}).
 *   <li>{@code message}: Shortcut to current message.
 *   <li>{@code conversation}: Shortcut to current conversation.
 *   <li>{@code choreography}: Shortcut to current choreography.
 *   <li>{@code participant}: Shortcut to current participant.
 *   <li>{@code data}: Shortcut to message context data.
 *         This is usually what you want to process in the template.
 *   <li>{@code participant}: Shortcut to message context routing data.
 * </ul>
 * 
 * @author Jonas Reese
 */
public class FreemarkerBackendPipelet extends AbstractPipelet {
    private static Logger LOG = Logger.getLogger(FreemarkerBackendPipelet.class);

    public static final String USE_EXTERNAL_TEMPLATE_PARAM_NAME = "externalTemplate";
    public static final String TEMPLATE_PARAM_NAME = "template";
    public static final String TEMPLATE_LOCATION_PARAM_NAME = "templateLocation";
    public static final String LOCALE_PARAM_NAME = "locale";
    
    /**
     * Default constructor.
     */
    public FreemarkerBackendPipelet() {
        parameterMap.put(USE_EXTERNAL_TEMPLATE_PARAM_NAME, new ParameterDescriptor(ParameterType.BOOLEAN, "Use external template",
                "Use template in file system instead of the template text box below.", false));
        parameterMap.put(TEMPLATE_LOCATION_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Template Path",
                "The file system path to the template. Ignored if external template is disabled.", ""));
        ListParameter localeDropdown = new ListParameter();
        // sort locales
        List<LocaleAndDisplayName> l = new ArrayList<LocaleAndDisplayName>();
        for (Locale loc : Locale.getAvailableLocales()) {
            l.add(new LocaleAndDisplayName(loc));
        }
        Collections.sort(l);
        
        localeDropdown.addElement("Default", "");
        for (LocaleAndDisplayName loc : l) {
            localeDropdown.addElement(loc.locale.getDisplayName(), loc.locale.toString());
        }
        parameterMap.put(LOCALE_PARAM_NAME, new ParameterDescriptor(
                ParameterType.LIST, "Locale", "The locale to be used for template processing", localeDropdown));
        parameterMap.put(TEMPLATE_PARAM_NAME, new ParameterDescriptor(ParameterType.TEXT, "Template",
                "The template markup. Ignored if external template is enabled.", ""));

    }
    
    @Override
    public MessageContext processMessage(MessageContext messageContext) throws NexusException {
        try {
            // create freemarker template
            String templateLocation;
            Reader templateReader = null;
            if (getParameter(USE_EXTERNAL_TEMPLATE_PARAM_NAME)) {
                templateLocation = getParameter(TEMPLATE_LOCATION_PARAM_NAME);
                LOG.debug("Processing freemarker template " + templateLocation);
            } else {
                // create reader from pipelet configuration
                templateReader = new StringReader((String) getParameter(TEMPLATE_PARAM_NAME));
                templateLocation = "inline";
                LOG.debug("Processing inline freemarker template");
            }
            Configuration configuration = new Configuration(); // use defaults
            Locale locale = Locale.getDefault();
            ListParameter localeParam = getParameter(LOCALE_PARAM_NAME);
            if (!StringUtils.isBlank(localeParam.getSelectedValue())) {
                locale = LocaleUtils.toLocale(localeParam.getSelectedValue());
            }
            configuration.setLocale(locale);

            Template template = new Template(templateLocation, templateReader, configuration);

            // process template
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("messageContext", messageContext);
            context.put("message", messageContext.getMessagePojo());
            context.put("conversation", messageContext.getConversation());
            context.put("choreography", messageContext.getChoreography());
            context.put("participant", messageContext.getParticipant());
            context.put("data", messageContext.getData());
            context.put("routingData", messageContext.getRoutingData());
            
            StringWriter sw = new StringWriter();
            Environment env = template.createProcessingEnvironment(context, sw);
            env.process();

            // set result as messageContext.data
            messageContext.setData(sw.toString());
            if (messageContext.getMessagePojo().getMessagePayloads() == null) {
                messageContext.getMessagePojo().setMessagePayloads(new ArrayList<MessagePayloadPojo>());
            }
            MessagePayloadPojo messagePayload;
            if (!messageContext.getMessagePojo().getMessagePayloads().isEmpty()) {
                messagePayload = messageContext.getMessagePojo().getMessagePayloads().get(0);
                messagePayload.setMimeType("text/html");
                messagePayload.setPayloadData(sw.toString().getBytes(messageContext.getEncoding()));
            } else {
                messagePayload = new MessagePayloadPojo(
                        messageContext.getMessagePojo(), 1, "text/xml", "xml_body_1", sw.toString().getBytes(messageContext.getEncoding()), null, null, 0);
                messageContext.getMessagePojo().getMessagePayloads().add(messagePayload);
            }
            
            return messageContext;
        } catch (IOException e) {
            throw new NexusException(e);
        } catch (TemplateException e) {
            throw new NexusException(e);
        }
    }
    
    class LocaleAndDisplayName implements Comparable<LocaleAndDisplayName> {
        Locale locale;
        
        public LocaleAndDisplayName(Locale locale) {
            this.locale = locale;
        }
        
        @Override
        public int compareTo(LocaleAndDisplayName o) {
            if (locale == null) {
                return -1;
            } else if (o.locale == null) {
                return 1;
            }
            return locale.getDisplayName().compareTo(o.locale.getDisplayName());
        }
    }
}
