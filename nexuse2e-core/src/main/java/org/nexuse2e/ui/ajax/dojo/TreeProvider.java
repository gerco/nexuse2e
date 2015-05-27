/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2009, Tamgroup and X-ioma GmbH
 * <p>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation version 2.1 of
 * the License.
 * <p>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.ui.ajax.dojo;


import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.ajax.AjaxRequestHandler;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureException;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.StructureService;
import org.nexuse2e.ui.structure.impl.PageNode;
import org.springframework.beans.factory.BeanFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Converts the ui structure into a JSON format for the Tree widget of the Dojo Toolkit.
 * @author Sebastian Schulze
 * @date 14.12.2006
 */
public class TreeProvider implements AjaxRequestHandler {

    private static final Logger LOG = Logger.getLogger(TreeProvider.class);

    private static final String BEAN_STRUCTURE_SERVICE = "structureService";

    public String handleRequest(HttpServletRequest request) throws JSONException {

        // get applicable engine configuration
        UserPojo user = (UserPojo) request.getSession().getAttribute(NexusE2EAction.ATTRIBUTE_USER);
        EngineConfiguration engineConfiguration;
        if (user != null) {
            engineConfiguration = Engine.getInstance().getConfiguration(user.getNxUserId());
        } else {
            // no user, no configuration
            return "";
        }

        Map<?, ?> params = request.getParameterMap();

        String result = "";
        String action = (params.containsKey("action") ? ((String[]) params.get("action"))[0] : null);
        String parentId = (params.containsKey("parentId") ? ((String[]) params.get("parentId"))[0] : null);
        if (action != null) {
            // parse JSON request data
            result = "{ \"label\": \"title\"," +
                     "\"identifier\":\"objectId\"," +
                     "\"items\": " + getChildren(parentId, engineConfiguration).toString() + " }";

        } else {
            LOG.warn("Missing request parameter: action was null");
        }

        LOG.trace("RETURN REQUEST");

        return result;
    }

    protected JSONArray getChildren(String parentId, EngineConfiguration engineConfiguration) throws JSONException {

        JSONArray result = new JSONArray();

        BeanFactory beanFactory = Engine.getInstance().getBeanFactory();
        StructureService strSrv = (StructureService) beanFactory.getBean(BEAN_STRUCTURE_SERVICE);

        if (strSrv != null) {
            try {
                final List<StructureNode> nodes = strSrv.getMenuStructure(engineConfiguration);
                Map<String, StructureNode> nodeMap = buildNodeCatalog(nodes);
                // if no parent node is defined, return the root node
                StructureNode parentNode = (parentId != null ? nodeMap.get(parentId) : new PageNode("", "", "") {
                    @SuppressWarnings("unchecked")
                    public List<StructureNode> getChildren() {
                        return Collections.singletonList(nodes.get(0));
                    }
                });
                if (parentNode instanceof PageNode) {
                    List<StructureNode> children = ((ParentalStructureNode) parentNode).getChildren();
                    for (StructureNode currChild : children) {
                        if (!currChild.isPattern()) {
                            if (currChild instanceof PageNode) {
                                result.put(convert2DojoNode(currChild, true));
                            }
                        }
                    }
                }
            } catch (StructureException e) {
                LOG.error(e);
            }
        } else {
            LOG.warn("No StructureService instance found. Check bean factory (id=" + BEAN_STRUCTURE_SERVICE + ")!");
        }

        LOG.trace("RETURN CHILDREN");

        return result;

    }

    private JSONObject convert2DojoNode(StructureNode node, boolean ignoreCommands) throws JSONException {

        JSONObject dojoNode = new JSONObject();
        if (node instanceof PageNode && ((ParentalStructureNode) node).hasChildren()) {
            if (ignoreCommands) {
                List<StructureNode> children = ((ParentalStructureNode) node).getChildren();
                boolean isFolder = false;
                for (Iterator<StructureNode> childIter = children.iterator(); childIter.hasNext() && !isFolder; ) {
                    if (childIter.next() instanceof PageNode) {
                        dojoNode.put("type", "folder");
                        isFolder = true;
                    }
                }
            } else {
                dojoNode.put("type", "folder");
            }
        }
        // save in objectId whether node has dynamic children: "d" := dynamic, "s" := static
        dojoNode.put("objectId", buildObjectId(node));
        dojoNode.put("widgetId", node.getTarget());
        dojoNode.put("title", node.getLabel());
        dojoNode.put("childIconSrc", node.getIcon());

        return dojoNode;
    }

    private String buildObjectId(StructureNode node) {
        int hashCode = (node.getTarget() + node.getLabel() + node.getIcon()).hashCode();
        return (node instanceof PageNode && ((ParentalStructureNode) node).hasDynamicChildren() ? "d" : "s") + (hashCode < 0 ?
            "_n" + (hashCode * -1) :
            "_p" + hashCode);
    }

    private Map<String, StructureNode> buildNodeCatalog(List<StructureNode> nodes) {
        Map<String, StructureNode> catalog = new HashMap<String, StructureNode>();
        addToMap(nodes, catalog);

        return catalog;
    }

    private void addToMap(List<StructureNode> nodes, Map<String, StructureNode> map) {
        for (StructureNode currNode : nodes) {
            map.put(currNode.getTarget(), currNode);
            if (currNode instanceof PageNode && ((ParentalStructureNode) currNode).hasChildren() && !currNode.isPattern()) {
                addToMap(((ParentalStructureNode) currNode).getChildren(), map);
            }
        }
    }

}