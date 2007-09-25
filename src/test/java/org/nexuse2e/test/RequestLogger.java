package org.nexuse2e.test;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Created: 25.09.2007
 * TODO Class documentation
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class RequestLogger implements Controller, ApplicationContextAware {

    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) {
        try {
            List<?> l = IOUtils.readLines( request.getInputStream() );
            for (Object s : l) {
                System.out.println( s );
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext( ApplicationContext context )
            throws BeansException {
        Object o = context.getBean( "xfire" );
        System.out.println( o );
    }
}
