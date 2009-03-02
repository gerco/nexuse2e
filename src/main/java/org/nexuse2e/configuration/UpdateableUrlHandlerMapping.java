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
package org.nexuse2e.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

/**
 * @author mbreilmann
 *
 */
public class UpdateableUrlHandlerMapping extends AbstractHandlerMapping {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private PathMatcher   pathMatcher   = new AntPathMatcher();

    private boolean       lazyInitHandlers;

    private final Map<String, Object>     handlerMap    = new HashMap<String, Object>();

    private final Map<String, Object>     urlMap        = new HashMap<String, Object>();

    /**
     * Set if URL lookup should always use the full path within the current servlet
     * context. Else, the path within the current servlet mapping is used
     * if applicable (i.e. in the case of a ".../*" servlet mapping in web.xml).
     * Default is "false".
     * @see org.springframework.web.util.UrlPathHelper#setAlwaysUseFullPath
     */
    public void setAlwaysUseFullPath( boolean alwaysUseFullPath ) {

        this.urlPathHelper.setAlwaysUseFullPath( alwaysUseFullPath );
    }

    /**
     * Set if context path and request URI should be URL-decoded.
     * Both are returned <i>undecoded</i> by the Servlet API,
     * in contrast to the servlet path.
     * <p>Uses either the request encoding or the default encoding according
     * to the Servlet spec (ISO-8859-1).
     * <p>Note: Setting this to "true" requires JDK 1.4 if the encoding differs
     * from the VM's platform default encoding, as JDK 1.3's URLDecoder class
     * does not offer a way to specify the encoding.
     * @see org.springframework.web.util.UrlPathHelper#setUrlDecode
     */
    public void setUrlDecode( boolean urlDecode ) {

        this.urlPathHelper.setUrlDecode( urlDecode );
    }

    /**
     * Set the UrlPathHelper to use for resolution of lookup paths.
     * <p>Use this to override the default UrlPathHelper with a custom subclass,
     * or to share common UrlPathHelper settings across multiple HandlerMappings
     * and MethodNameResolvers.
     * @see org.springframework.web.servlet.mvc.multiaction.AbstractUrlMethodNameResolver#setUrlPathHelper
     */
    public void setUrlPathHelper( UrlPathHelper urlPathHelper ) {

        this.urlPathHelper = urlPathHelper;
    }

    /**
     * Set the PathMatcher implementation to use for matching URL paths
     * against registered URL patterns. Default is AntPathMatcher.
     * @see org.springframework.util.AntPathMatcher
     */
    public void setPathMatcher( PathMatcher pathMatcher ) {

        Assert.notNull( pathMatcher, "PathMatcher must not be null" );
        this.pathMatcher = pathMatcher;
    }

    /**
     * Set whether to lazily initialize handlers. Only applicable to
     * singleton handlers, as prototypes are always lazily initialized.
     * Default is "false", as eager initialization allows for more efficiency
     * through referencing the controller objects directly.
     * <p>If you want to allow your controllers to be lazily initialized,
     * make them "lazy-init" and set this flag to true. Just making them
     * "lazy-init" will not work, as they are initialized through the
     * references from the handler mapping in this case.
     */
    public void setLazyInitHandlers( boolean lazyInitHandlers ) {

        this.lazyInitHandlers = lazyInitHandlers;
    }

    /**
     * Look up a handler for the URL path of the given request.
     * @param request current HTTP request
     * @return the looked up handler instance, or <code>null</code>
     */
    protected Object getHandlerInternal( HttpServletRequest request ) throws Exception {

        String lookupPath = this.urlPathHelper.getLookupPathForRequest( request );
        if ( logger.isDebugEnabled() ) {
            logger.debug( "Looking up handler for [" + lookupPath + "]" );
        }
        return lookupHandler( lookupPath, request );
    }

    /**
     * Look up a handler instance for the given URL path.
     * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
     * and various Ant-style pattern matches, e.g. a registered "/t*" matches
     * both "/test" and "/team". For details, see the AntPathMatcher class.
     * <p>Looks for the most exact pattern, where most exact is defined as
     * the longest path pattern.
     * @param urlPath URL the bean is mapped to
     * @return the associated handler instance, or <code>null</code> if not found
     * @see org.springframework.util.AntPathMatcher
     */
    protected Object lookupHandler( String urlPath, HttpServletRequest request ) {

        // direct match?
        Object handler = this.handlerMap.get( urlPath );
        if ( handler == null ) {
            // pattern match?
            String bestPathMatch = null;
            for (String registeredPath : handlerMap.keySet()) {
                if ( this.pathMatcher.match( registeredPath, urlPath )
                        && ( bestPathMatch == null || bestPathMatch.length() <= registeredPath.length() ) ) {
                    handler = this.handlerMap.get( registeredPath );
                    bestPathMatch = registeredPath;
                }
            }

            if ( handler != null ) {
                exposePathWithinMapping( this.pathMatcher.extractPathWithinPattern( bestPathMatch, urlPath ), request );
            }
        } else {
            exposePathWithinMapping( urlPath, request );
        }
        return handler;
    }

    /**
     * Expose the path within the current mapping as request attribute.
     * @param pathWithinMapping the path within the current mapping
     * @param request the request to expose the path to
     * @see #PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
     */
    protected void exposePathWithinMapping( String pathWithinMapping, HttpServletRequest request ) {

        request.setAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, pathWithinMapping );
    }

    /**
     * Register the specified handler for the given URL paths.
     * @param urlPaths the URLs that the bean should be mapped to
     * @param beanName the name of the handler bean
     * @throws BeansException if the handler couldn't be registered
     * @throws IllegalStateException if there is a conflicting handler registered
     */
    protected void registerHandler( String[] urlPaths, String beanName ) throws BeansException, IllegalStateException {

        Assert.notNull( urlPaths, "URL path array must not be null" );
        for ( int j = 0; j < urlPaths.length; j++ ) {
            registerHandler( urlPaths[j], beanName );
        }
    }

    /**
     * Register the specified handler for the given URL path.
     * @param urlPath the URL the bean should be mapped to
     * @param handler the handler instance or handler bean name String
     * (a bean name will automatically be resolved into the corrresponding handler bean)
     * @throws BeansException if the handler couldn't be registered
     * @throws IllegalStateException if there is a conflicting handler registered
     */
    protected void registerHandler( String urlPath, Object handler ) throws BeansException, IllegalStateException {

        Assert.notNull( urlPath, "URL path must not be null" );
        Assert.notNull( handler, "Handler object must not be null" );

        // Eagerly resolve handler if referencing singleton via name.
        if ( !this.lazyInitHandlers && handler instanceof String ) {
            String handlerName = (String) handler;
            if ( getApplicationContext().isSingleton( handlerName ) ) {
                handler = getApplicationContext().getBean( handlerName );
            }
        }

        if ( urlPath.equals( "/*" ) ) {
            setDefaultHandler( handler );
        } else {
            this.handlerMap.put( urlPath, handler );
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Mapped URL path [" + urlPath + "] onto handler [" + handler + "]" );
            }
        }
    }

    /**
     * Map URL paths to handler bean names.
     * This is the typical way of configuring this HandlerMapping.
     * <p>Supports direct URL matches and Ant-style pattern matches.
     * For syntax details, see the AntPathMatcher class.
     * @param mappings properties with URLs as keys and bean names as values
     * @see org.springframework.util.AntPathMatcher
     */
    public void setMappings( Properties mappings ) {

        for (Object key : mappings.keySet()) {
            if (key != null) {
                urlMap.put( key.toString(), mappings.get( key ) );
            }
        }
    }

    /**
     * Set a Map with URL paths as keys and handler beans as values.
     * Convenient for population with bean references.
     * <p>Supports direct URL matches and Ant-style pattern matches.
     * For syntax details, see the AntPathMatcher class.
     * @param urlMap map with URLs as keys and beans as values
     * @see org.springframework.util.AntPathMatcher
     */
    public void setUrlMap( Map<String, ? extends Object> urlMap ) {

        this.urlMap.putAll( urlMap );
    }

    /**
     * Calls the <code>registerHandlers</code> method in addition
     * to the superclass's initialization.
     * @see #registerHandlers
     */
    public void initApplicationContext() throws BeansException {

        super.initApplicationContext();
        registerHandlers( this.urlMap );
    }

    /**
     * Register all handlers specified in the URL map for the corresponding paths.
     * @param urlMap Map with URL paths as keys and handler beans or bean names as values
     * @throws BeansException if a handler couldn't be registered
     * @throws IllegalStateException if there is a conflicting handler registered
     */
    protected void registerHandlers( Map<String, ? extends Object> urlMap ) throws BeansException {

        if ( urlMap.isEmpty() ) {
            logger.debug( "Neither 'urlMap' nor 'mappings' set on UpdateableUrlHandlerMapping" );
        } else {
            for (String url : urlMap.keySet()) {
                Object handler = urlMap.get( url );
                // Prepend with slash if not already present.
                if ( !url.startsWith( "/" ) ) {
                    url = "/" + url;
                }
                registerHandler( url, handler );
            }
        }
    }

} // UpdateableUrlHandlerMapping
