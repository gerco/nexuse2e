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

    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        try {
            List<?> l = IOUtils.readLines( request.getInputStream() );
            for (Object s : l) {
                System.out.println( s );
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //response.setContentType( "text/xml;charset=UTF-8" );
        response.setContentType( null );
        response.getOutputStream().println(
                "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Header/><SOAP:Body/></SOAP:Envelope>" );
        
        //response.getOutputStream().println( "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soap:Body><processInboundMessageResponse xmlns=\"http://integration.nexuse2e.org/BackendDeliveryInterface/\"><statusResponse xmlns=\"http://integration.nexuse2e.org/BackendDeliveryInterface/\">success</statusResponse></processInboundMessageResponse></soap:Body></soap:Envelope>" );
        
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
