package org.nexuse2e;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.log4j.Logger;

/**
 * This subclass of <code>CXFNonSpringServlet</code> keeps a reference on the instance
 * created by the servlet container in order to make it available for code that
 * dynamically registers/unregisters web services.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class DynamicWSDispatcherServlet extends CXFNonSpringServlet {
    private static Logger      LOG                         = Logger.getLogger( DynamicWSDispatcherServlet.class );
    
    
    private static final long serialVersionUID = 1L;
    
    private static DynamicWSDispatcherServlet instance;
    
    private ServletConfig servletConfig;

    public DynamicWSDispatcherServlet() {
        instance = this;
    }
    
    public void init( ServletConfig servletConfig ) throws ServletException {
        super.init( servletConfig );
        this.servletConfig = servletConfig;
        Bus bus = getBus();
        if (bus != null) {
            BusFactory.setDefaultBus( bus );
        } else {
            LOG.error( "CXF bus is null" );
        }
    }
    
    public void reinitialize() {
        controller = createServletController( servletConfig );
    }

    /**
     * Gets the most recently created instance of this class.
     * @return The last created instance, or <code>null</code> if none was created yet.
     */
    public static DynamicWSDispatcherServlet getInstance() {
        return instance;
    }
}
