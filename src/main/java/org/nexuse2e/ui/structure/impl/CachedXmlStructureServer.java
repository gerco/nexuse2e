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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.nexuse2e.ui.structure.StructureException;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.StructureService;
import org.w3c.dom.Document;

/**
 * This {@link StructureService} implementation uses the {@link XmlStructrureServer}
 * to build the structures of the given XML specifications only once and cache them
 * until the end of the instances lifecycle or until the cache is cleared manually.
 * @author Sebastian Schulze
 * @date 25.01.2007
 */
public class CachedXmlStructureServer extends XmlStructureServer {

    private Map<String, List<StructureNode>> menuSkeletonCache;
    private Map<String, List<StructureNode>> menuStructureCache;
    private Map<String, List<StructureNode>> siteSkeletonCache;
    private Map<String, List<StructureNode>> siteStructureCache;
    private Map<String, Document> docCache;

    /**
     * Constructor.
     * @throws XPathExpressionException 
     */
    public CachedXmlStructureServer() throws XPathExpressionException {

        super();
        menuSkeletonCache = new HashMap<String, List<StructureNode>>();
        menuStructureCache = new HashMap<String, List<StructureNode>>();
        siteSkeletonCache = new HashMap<String, List<StructureNode>>();
        siteStructureCache = new HashMap<String, List<StructureNode>>();
        docCache = new HashMap<String, Document>();
    }
    
    
    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.XmlStructureServer#getMenuSkeleton()
     */
    @Override
    public List<StructureNode> getMenuSkeleton() throws StructureException {

        synchronized ( this ) {
            List<StructureNode> result = null;
            // lookup skeleton in cache
            if ( menuSkeletonCache.containsKey( spec ) ) {
                // get skeleton from cache
                result = menuSkeletonCache.get( spec );
            } else {
                // parse spec
                result = super.getMenuSkeleton();
                menuSkeletonCache.put( spec, result );
            }

            return result;
        }
    }
    
    

    
    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.XmlStructureServer#getMenuStructure()
     */
    @Override
    public List<StructureNode> getMenuStructure() throws StructureException {

        synchronized ( this ) {
            List<StructureNode> result = null;
            // lookup structure in cache
            if ( menuStructureCache.containsKey( spec ) ) {
                // get structure from cache
                result = menuStructureCache.get( spec );
            } else {
                // parse spec
                result = super.getMenuStructure();
                menuStructureCache.put( spec, result );
            }

            return result;
        }
    }



    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.XmlStructureServer#getSiteSkeleton()
     */
    @Override
    public List<StructureNode> getSiteSkeleton() throws StructureException {

        synchronized ( this ) {
            List<StructureNode> result = null;
            // lookup skeleton in cache
            if ( siteSkeletonCache.containsKey( spec ) ) {
                // get skeleton from cache
                result = siteSkeletonCache.get( spec );
            } else {
                // parse spec
                result = super.getSiteSkeleton();
                siteSkeletonCache.put( spec, result );
            }

            return result;
        }
    }



    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.XmlStructureServer#getSiteStructure()
     */
    @Override
    public List<StructureNode> getSiteStructure() throws StructureException {

        synchronized ( this ) {
            List<StructureNode> result = null;
            // lookup structure in cache
            if ( siteStructureCache.containsKey( spec ) ) {
                // get structure from cache
                result = siteStructureCache.get( spec );
            } else {
                // parse spec
                result = super.getSiteStructure();
                siteStructureCache.put( spec, result );
            }

            return result;
        }
    }



    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.XmlStructureServer#parseDocument()
     */
    @Override
    protected Document parseDocument() {

        synchronized ( this ) {
            Document result = null;
            // lookup document in cache
            if ( docCache.containsKey( spec ) ) {
                // get document from cache
                result = docCache.get( spec );
            } else {
                // parse spec
                result = super.parseDocument();
                docCache.put( spec, result );
            }

            return result;
        }
    }
    
    /**
     * Removes the current spec from all caches and parses the structure specification again.
     */
    public void cacheSpec() {
        synchronized ( this ) {
            menuSkeletonCache.remove( spec );
            menuStructureCache.remove( spec );
            siteSkeletonCache.remove( spec );
            siteStructureCache.remove( spec );
            Document result = super.parseDocument();
            docCache.put( spec, result );
        }
    }
    
    /**
     * Pre-Caches the menu skeleton based of the currently set structure specification.
     */
    public void cacheMenuSkeleton() throws StructureException {

        synchronized ( this ) {
            List<StructureNode> result = super.getMenuSkeleton();
                menuSkeletonCache.put( spec, result );
        }
    }
        
    /**
     * Pre-Caches the menu structure based of the currently set structure specification.
     */
    public void cacheMenuStructure() throws StructureException {

        synchronized ( this ) {
            List<StructureNode> result = super.getMenuStructure();
                menuStructureCache.put( spec, result );
        }
    }



    /**
     * Pre-Caches the site skeleton based of the currently set structure specification.
     */
    public void cacheSiteSkeleton() throws StructureException {

        synchronized ( this ) {
            List<StructureNode> result = super.getSiteSkeleton();
                siteSkeletonCache.put( spec, result );
        }
    }


    /**
     * Pre-Caches the site structure based of the currently set structure specification.
     */
    public void cacheSiteStructure() throws StructureException {

        synchronized ( this ) {
            List<StructureNode> result = super.getSiteStructure();
                siteStructureCache.put( spec, result );
        }
    }
    
    /**
     * Clears the cache that keeps the structure skeletons.
     */
    public void clearSkeletonCache() {
        synchronized ( this ) {
            menuSkeletonCache = new HashMap<String, List<StructureNode>>();
            siteSkeletonCache = new HashMap<String, List<StructureNode>>();
        }
    }
    
    /**
     * Clears the cache that  keeps the structures.
     */
    public void clearStructureCache() {
        synchronized ( this ) {
            menuStructureCache = new HashMap<String, List<StructureNode>>();
            siteStructureCache = new HashMap<String, List<StructureNode>>();
        }
    }
    
    /**
     * Clears the cache that keeps the DOM trees of the parsed documents.
     */
    public void clearDocumentCache() {
        synchronized ( this ) {
            docCache = new HashMap<String, Document>();
        }
    }
}
