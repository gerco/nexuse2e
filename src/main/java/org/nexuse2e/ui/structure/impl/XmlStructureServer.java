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
package org.nexuse2e.ui.structure.impl;

import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureException;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.StructureService;
import org.nexuse2e.ui.structure.TargetProvider;
import org.nexuse2e.ui.structure.TargetProviderManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implements the StructureService interface.
 * The structure is build from an XML specification based on the "structure.dtd".
 * The methods of the public interface are thread save. This is assumed to be necessary,
 * because always the same XPath instance is used.
 * @author Sebastian Schulze
 * @date 05.12.2006
 */
public class XmlStructureServer implements StructureService {

    private static Logger           LOG                             = Logger.getLogger( XmlStructureServer.class );

    private static String           XPATH_MENU                      = "/structure/menu";

    private static String           XPATH_SITE                      = "/structure/site";

    private static String           XPATH_PAGE_AND_COMMAND_CHILDREN = "./page | ./command";

    private static String           XPATH_ATTRIBUTE_TYPE            = "./@type";

    private static String           XPATH_ATTRIBUTE_PROVIDER        = "./@provider";
    
    private static String           XPATH_ATTRIBUTE_TARGET          = "./@target";

    private static String           XPATH_ATTRIBUTE_LABEL           = "./@label";

    private static String           XPATH_ATTRIBUTE_ICON            = "./@icon";

    private static String           NODE_NAME_PAGE                  = "nxs:page";

    private static String           NODE_NAME_COMMAND               = "nxs:command";

    private static String           TYPE_URL                        = "url";

    private static String           TYPE_PROVIDER                   = "provider";

    /**
     * The path to the XML specification file.
     */
    protected String                spec;

    /**
     * The TargetProviderManager to use for dynamic nodes.
     */
    protected TargetProviderManager tpManager;

    /**
     * XPath object used for this instance.
     */
    protected XPath                 xpath;

    /**
     * Precompiled XPath query.
     */
    protected XPathExpression       COMPILED_EXPR_MENU;
    /**
     * Precompiled XPath query.
     */
    protected XPathExpression       COMPILED_EXPR_SITE;
    /**
     * Precompiled XPath query.
     */
    protected XPathExpression       COMPILED_EXPR_PAGE_AND_COMMAND_CHILDREN;
    /**
     * Precompiled XPath query.
     */
    protected XPathExpression       COMPILED_EXPR_ATTRIBUTE_TYPE;
    /**
     * Precompiled XPath query.
     */
    protected XPathExpression       COMPILED_EXPR_ATTRIBUTE_PROVIDER;
    /**
     * Precompiled XPath query.
     */
    protected XPathExpression       COMPILED_EXPR_ATTRIBUTE_TARGET;
    /**
     * Precompiled XPath query.
     */
    protected XPathExpression       COMPILED_EXPR_ATTRIBUTE_LABEL;
    /**
     * Precompiled XPath query.
     */
    protected XPathExpression       COMPILED_EXPR_ATTRIBUTE_ICON;

    /**
     * Initializes the XPath instance and precompiles the queries.
     * @throws XPathExpressionException if an errror occurs during precompiling the XPath queries.
     */
    public XmlStructureServer() throws XPathExpressionException {

        xpath = XPathFactory.newInstance().newXPath();
        COMPILED_EXPR_MENU = xpath.compile( XPATH_MENU );
        COMPILED_EXPR_SITE = xpath.compile( XPATH_SITE );
        COMPILED_EXPR_PAGE_AND_COMMAND_CHILDREN = xpath.compile( XPATH_PAGE_AND_COMMAND_CHILDREN );
        COMPILED_EXPR_ATTRIBUTE_TYPE = xpath.compile( XPATH_ATTRIBUTE_TYPE );
        COMPILED_EXPR_ATTRIBUTE_PROVIDER = xpath.compile( XPATH_ATTRIBUTE_PROVIDER );
        COMPILED_EXPR_ATTRIBUTE_TARGET = xpath.compile( XPATH_ATTRIBUTE_TARGET );
        COMPILED_EXPR_ATTRIBUTE_LABEL = xpath.compile( XPATH_ATTRIBUTE_LABEL );
        COMPILED_EXPR_ATTRIBUTE_ICON = xpath.compile( XPATH_ATTRIBUTE_ICON );
    }

    /**
     * Returns the menu structure if a valid XML structure file has been set.
     * @param engineConfiguration The applicable engine configuration.
     * @return The menu's structure or an empty list if the specified path or file is invalid.
     * @throws StructureException if an error occurred during the structure build process.
     * @see setSpec(String)
     * @see org.nexuse2e.ui.structure.StructureService#getMenuStructure()
     */
    public List<StructureNode> getMenuStructure( EngineConfiguration engineConfiguration ) throws StructureException {

        synchronized ( this ) {
            return getStructure( COMPILED_EXPR_MENU, true, engineConfiguration );
        }

    }

    /**
     * Returns the site structure if a valid XML structure file has been set.
     * @param engineConfiguration The applicable engine configuration.
     * @return The site's structure or an empty list if the specified path or file is invalid.
     * @throws StructureException if an error occurred during the structure build process.
     * @see setSpec(String)
     * @see org.nexuse2e.ui.structure.StructureService#getSiteStructure()
     */
    public List<StructureNode> getSiteStructure( EngineConfiguration engineConfiguration ) throws StructureException {

        synchronized ( this ) {
            return getStructure( COMPILED_EXPR_SITE, true, engineConfiguration );
        }
    }
    
    /**
     * Returns the menu structure skeleton if a valid XML structure file has been set.
     * @return The menu's structure skeleton or an empty list if the specified path or file is invalid.
     * @throws StructureException if an error occurred during the structure build process.
     * @see setSpec(String)
     * @see org.nexuse2e.ui.structure.StructureService#getMenuStructure()
     */
    public List<StructureNode> getMenuSkeleton() throws StructureException {

        synchronized ( this ) {
            return getStructure( COMPILED_EXPR_MENU, false, null );
        }
    }

    /**
     * Returns the site structure skeleton if a valid XML structure file has been set.
     * @return The site's structure skeleton or an empty list if the specified path or file is invalid.
     * @throws StructureException if an error occurred during the structure build process.
     * @see setSpec(String)
     * @see org.nexuse2e.ui.structure.StructureService#getMenuStructure()
     */
    public List<StructureNode> getSiteSkeleton( EngineConfiguration engineConfiguration ) throws StructureException {

        synchronized ( this ) {
            return getStructure( COMPILED_EXPR_SITE, false, engineConfiguration );
        }
    }

    /**
     * Returns the site structure if a valid XML structure file has been set.
     * @param rootPath the xpath under which the structure is located.
     * @param Defines wheather to evaluate dynamic nodes (<code>type="provider"</code>) or return the skeleton only.
     * @param engineConfiguration The applicable engine configuration.
     * @return The site's structure or an empty list if the specified path or file is invalid.
     * @throws StructureException if an error occurred during the structure build process.
     * @see setSpec(String)
     * @see org.nexuse2e.ui.structure.StructureService#getSiteStructure()
     */
    @SuppressWarnings("unchecked")
    private List<StructureNode> getStructure(
            XPathExpression rootPath, boolean evaluateDynamicNodes, EngineConfiguration engineConfiguration )
            throws StructureException {

        List<StructureNode> result = Collections.EMPTY_LIST;

        if ( spec != null ) {
            Document doc = parseDocument();
            if ( doc != null ) {
                try {
                    Node menuNode = (Node) rootPath.evaluate( doc, XPathConstants.NODE );
                    if ( menuNode != null ) {
                        NodeList menuList = (NodeList) COMPILED_EXPR_PAGE_AND_COMMAND_CHILDREN.evaluate( menuNode,
                                XPathConstants.NODESET );
                        RootNode root = new RootNode();
                        for ( int i = 0; i < menuList.getLength(); i++ ) {
                            Node currNode = menuList.item( i );
                            if ( currNode.getNodeName().equals( NODE_NAME_PAGE ) ) {
                                buildPageStructure( currNode, root, evaluateDynamicNodes, engineConfiguration );
                            } else if ( currNode.getNodeName().equals( NODE_NAME_COMMAND ) ) {
                                buildCommandStructure( currNode, root, evaluateDynamicNodes, engineConfiguration );
                            }
                        }
                        result = root.getChildren();
                    }
                } catch ( XPathExpressionException e ) {
                    throw new StructureException( "Error parsing the structure definition \"" + spec + "\"!", e );
                }
            }

        } else {
            LOG.warn( "No structure specification defined!" );
        }

        return result;
    }

    /**
     * Recursively builds the structure of a given command node without an additional check of the node name.
     * @param commandNode The command node.
     * @param parents The direct parents in the structure to which the built node(s) will be attached as children.
     * @param Defines whether to evaluate dynamic nodes (<code>type="provider"</code>) or return the skeleton only.
     * @param engineConfiguration The applicable engine configuration.
     * @throws XPathExpressionException if an error occurs during the xpath evaluation.
     * @throws StructureException if no TargetProviderManager is set or a TargetProvider cannot be found for a given
     *         target name.
     */
    private void buildCommandStructure(
            Node commandNode,
            ParentalStructureNode parent,
            boolean evaluateDynamicNodes,
            EngineConfiguration engineConfiguration )
            throws XPathExpressionException, StructureException {

        if ( commandNode != null && parent != null ) {
            String type = ( (Node) COMPILED_EXPR_ATTRIBUTE_TYPE.evaluate( commandNode, XPathConstants.NODE ) )
                    .getNodeValue();
            Node providerNode = (Node) COMPILED_EXPR_ATTRIBUTE_PROVIDER.evaluate( commandNode, XPathConstants.NODE );
            String provider = ( providerNode != null ? providerNode.getNodeValue() : null );
            String target = ( (Node) COMPILED_EXPR_ATTRIBUTE_TARGET.evaluate( commandNode, XPathConstants.NODE ) )
                    .getNodeValue();
            String label = ( (Node) COMPILED_EXPR_ATTRIBUTE_LABEL.evaluate( commandNode, XPathConstants.NODE ) )
                    .getNodeValue();
            String icon = ( (Node) COMPILED_EXPR_ATTRIBUTE_ICON.evaluate( commandNode, XPathConstants.NODE ) )
                    .getNodeValue();

            parent.setProvider( provider );
            parent.setChildTarget( target );
            parent.setChildLabel( label );
            parent.setChildIcon( icon );
            
            if ( TYPE_PROVIDER.equalsIgnoreCase( type ) ) {
                // dynamic node as placeholder for a bunch of nodes
                StructureNode patternNode = new CommandNode( target, label, icon );
                if ( evaluateDynamicNodes ) {
                    if ( tpManager != null ) {
                        TargetProvider tp = tpManager.getTargetProvider( provider );
                        if ( tp != null ) {
                            parent.addChildren( tp.getStructure( patternNode, parent, engineConfiguration ) );
                            parent.setDynamicChildren( true );
                        } else {
                            throw new StructureException( "No TargetProvider instance assigned to provider name \"" + provider
                                    + "\"!" );
                        }
                    } else {
                        throw new StructureException( "No TargetProviderManager set!" );
                    }
                }
                else {
                    // add pattern node (skeleton mode)
                    parent.addChild( patternNode );
                }
            } else if ( TYPE_URL.equalsIgnoreCase( type ) ) {
                // static command node
                StructureNode sNode = new CommandNode( target, label, icon );
                parent.addChild( sNode );
            } else {
                LOG.warn( "Unknown command type in \"" + spec + "\": " + type );
            }
        }
    }

    /**
     * Recursively builds the structure of a given page node without an additional check of the node name.
     * @param pageNode The page node.
     * @param parent The direct parent in the structure to which the built node(s) will be attached as children.
     * @param Defines whether to evaluate dynamic nodes (<code>type="provider"</code>) or return the skeleton only.
     * @param engineConfiguration The applicable engine configuration.
     * @throws XPathExpressionException if an error occurs during the xpath evaluation.
     * @throws StructureException if no TargetProviderManager is set or a TargetProvider cannot be found for a given
     *         target name.
     */
    private void buildPageStructure(
            Node pageNode, ParentalStructureNode parent, boolean evaluateDynamicNodes, EngineConfiguration engineConfiguration )
    throws XPathExpressionException,
            StructureException {

        if ( pageNode != null && parent != null ) {
            String type = ( (Node) COMPILED_EXPR_ATTRIBUTE_TYPE.evaluate( pageNode, XPathConstants.NODE ) )
                    .getNodeValue();
            Node providerNode = (Node) COMPILED_EXPR_ATTRIBUTE_PROVIDER.evaluate( pageNode, XPathConstants.NODE );
            String provider = ( providerNode != null ? providerNode.getNodeValue() : null );
            String target = ( (Node) COMPILED_EXPR_ATTRIBUTE_TARGET.evaluate( pageNode, XPathConstants.NODE ) )
                    .getNodeValue();
            String label = ( (Node) COMPILED_EXPR_ATTRIBUTE_LABEL.evaluate( pageNode, XPathConstants.NODE ) )
                    .getNodeValue();
            String icon = ( (Node) COMPILED_EXPR_ATTRIBUTE_ICON.evaluate( pageNode, XPathConstants.NODE ) )
                    .getNodeValue();

            parent.setProvider( provider );
            parent.setChildTarget( target );
            parent.setChildLabel( label );
            parent.setChildIcon( icon );
            
            if ( TYPE_PROVIDER.equalsIgnoreCase( type ) ) {
                // dynamic node as placeholder for a bunch of nodes
                StructureNode patternNode = new PageNode( target, label, icon );
                if ( evaluateDynamicNodes ) {
                    if ( tpManager != null ) {
                        TargetProvider tp = tpManager.getTargetProvider( provider );
                        if ( tp != null ) {
                            List<StructureNode> sNodes = tp.getStructure( patternNode, parent, engineConfiguration );
                            parent.addChildren( sNodes );
                            parent.setDynamicChildren( true );
                            for ( StructureNode sNode : sNodes ) {
                                if ( sNode instanceof PageNode ) {
                                    // search for children of the new page node
                                    NodeList children = (NodeList) COMPILED_EXPR_PAGE_AND_COMMAND_CHILDREN.evaluate(
                                            pageNode, XPathConstants.NODESET );
                                    if ( children != null ) {
                                        for ( int i = 0; i < children.getLength(); i++ ) {
                                            Node currNode = children.item( i );
                                            if ( currNode.getNodeName().equals( NODE_NAME_PAGE ) ) {
                                                buildPageStructure( currNode, (ParentalStructureNode) sNode, evaluateDynamicNodes, engineConfiguration );
                                            } else if ( currNode.getNodeName().equals( NODE_NAME_COMMAND ) ) {
                                                buildCommandStructure( currNode, (ParentalStructureNode) sNode, evaluateDynamicNodes, engineConfiguration );
                                            }
                                        }
                                    }
                                } else {
                                    LOG.error( "Structure pattern inconsistency! TargetProvider \"" + tp
                                            + "\" must not return a StructureNode of type \"" + sNode.getClass()
                                            + "\" for a pattern node of type PageNode." );
                                }
                            }
                        } else {
                            throw new StructureException( "No TargetProvider instance assigned to target name \"" + target
                                    + "\"!" );
                        }
                    } else {
                        throw new StructureException( "No TargetProviderManager set!" );
                    }
                } else {
                    // add pattern node (skeleton mode)
                    parent.addChild( patternNode );
                    // search for children of the pattern node
                    NodeList children = (NodeList) COMPILED_EXPR_PAGE_AND_COMMAND_CHILDREN.evaluate(
                            pageNode, XPathConstants.NODESET );
                    if ( children != null ) {
                        for ( int i = 0; i < children.getLength(); i++ ) {
                            Node currNode = children.item( i );
                            if ( currNode.getNodeName().equals( NODE_NAME_PAGE ) ) {
                                buildPageStructure( currNode, (ParentalStructureNode) patternNode, evaluateDynamicNodes, engineConfiguration );
                            } else if ( currNode.getNodeName().equals( NODE_NAME_COMMAND ) ) {
                                buildCommandStructure( currNode, (ParentalStructureNode) patternNode, evaluateDynamicNodes, engineConfiguration );
                            }
                        }
                    }
                }
            } else if ( TYPE_URL.equalsIgnoreCase( type ) ) {
                // static page node
                PageNode sNode = new PageNode( target, label, icon );
                parent.addChild( sNode );
                // search for children of the new page node
                NodeList children = (NodeList) COMPILED_EXPR_PAGE_AND_COMMAND_CHILDREN.evaluate( pageNode,
                        XPathConstants.NODESET );
                if ( children != null ) {
                    for ( int i = 0; i < children.getLength(); i++ ) {
                        Node currNode = children.item( i );
                        if ( currNode.getNodeName().equals( NODE_NAME_PAGE ) ) {
                            buildPageStructure( currNode, sNode, evaluateDynamicNodes, engineConfiguration );
                        } else if ( currNode.getNodeName().equals( NODE_NAME_COMMAND ) ) {
                            buildCommandStructure( currNode, sNode, evaluateDynamicNodes, engineConfiguration );
                        }
                    }
                }

            } else {
                LOG.warn( "Unknown command type in \"" + spec + "\": " + type );
            }
        }
    }

    /**
     * Sets the path to the XML structure specification file.
     * This has to be done before one of the service methods is called.
     * @param spec the spec to set
     */
    public void setSpec( String spec ) {

        synchronized ( this ) {
            this.spec = spec;
        }
    }

    /**
     * Sets the TargetProviderManager instance to resolve dynamic structure nodes.
     * @param tpManager the TargetProviderManager to set.
     */
    public void setTargetProviderManager( TargetProviderManager tpManager ) {

        synchronized ( this ) {
            this.tpManager = tpManager;
        }
    }

    /**
     * Parses the XML specification.
     * @return The root node of the DOM.
     */
    protected Document parseDocument() {

        DocumentBuilderFactory dof = DocumentBuilderFactory.newInstance();
        dof.setValidating( true );
        try {
            DocumentBuilder docBuilder = dof.newDocumentBuilder();
            return docBuilder.parse( this.getClass().getResourceAsStream( spec ) );
        } catch ( Exception e ) {
            LOG.error( e );
        }

        return null;
    }

    /**
     * RootNode definition as support for recursive structure build.
     * @author Sebastian Schulze
     * @date 12.12.2006
     */
    private class RootNode extends PageNode {

        private RootNode() {

            super( null, null, null );
        }
    }
}