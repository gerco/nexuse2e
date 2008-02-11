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
package org.nexuse2e.reporting;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.nexuse2e.Engine;
import org.springframework.beans.BeansException;

/**
 * This servlet initializes the BIRT engine for using it within the NEXUSe2e application.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class BirtReportServlet extends HttpServlet {

    private static final long serialVersionUID = -3656202991475539637L;

    public static final String REPORT_NAME_PARAM_NAME = "reportName";
    
    private static BirtReportServlet instance;
    
    protected static Logger logger = Logger.getLogger( "org.eclipse.birt" );
    
    public BirtReportServlet() {
        instance = this;
    }
    
    public static BirtReportServlet getInstance() {
        return instance;
    }
    
    
    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); 
        BirtEngine.shutdown();
    }


    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //get report name and launch the engine
        resp.setContentType("text/html");
        //resp.setContentType( "application/pdf" ); 
        //resp.setHeader ("Content-Disposition","inline; filename=test.pdf");       
        String reportName = req.getParameter( REPORT_NAME_PARAM_NAME );
        ServletContext sc = req.getSession().getServletContext();
        IReportEngine birtReportEngine = BirtEngine.getBirtEngine(sc);
        
        //setup image directory
        IReportRunnable design;
        try
        {
            HTMLServerImageHandler handler = new HTMLServerImageHandler() {
                @Override
                public String onCustomImage(IImage image, IReportContext context) {
                    ReportImageHandlerServlet imageHandler = ReportImageHandlerServlet.getInstance();
                    return imageHandler.registerImage( image );
                }
            };
            
            //Open report design
            design = birtReportEngine.openReportDesign( sc.getRealPath("/WEB-INF/reports") + "/" + reportName );
            //create task to run and render report
            IRunAndRenderTask task = birtReportEngine.createRunAndRenderTask( design );
            
            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            for (Enumeration<?> enumeration = req.getParameterNames(); enumeration.hasMoreElements(); ) {
                String name = enumeration.nextElement().toString();
                String value = req.getParameter( name );
                if (!REPORT_NAME_PARAM_NAME.equals( name )) {
                    if (name.toLowerCase().indexOf( "date" ) >= 0) {
                        task.setParameterValue( name, dateFormat.parse( value ) );
                    } else {
                        task.setParameterValue( name, value );
                    }
                }
            }
            
            //set output options
            HTMLRenderOption options = new HTMLRenderOption();
            options.setEmbeddable( true );
            options.setImageHandler( handler );
            options.setOutputFormat( HTMLRenderOption.OUTPUT_FORMAT_HTML );
            options.setBaseImageURL( ReportImageHandlerServlet.getInstance().getImageBasePath() );
            options.setOutputStream( resp.getOutputStream() );
            task.setRenderOption(options);
            
            //run report
            task.run();
            task.close();
        }catch (Exception e){
            
            e.printStackTrace();
            throw new ServletException( e );
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occure
     */
    public void init() throws ServletException {
        BirtEngine.initBirtConfig();
        try {
            Context i = new InitialContext();
            Context e;
            try {
                e = (Context) i.lookup( "birt" );
            } catch (NameNotFoundException nnfex) {
                e = i.createSubcontext( "birt" );
            }
            e.rebind("jdbc", Engine.getInstance().getBeanFactory().getBean( "internal" ) );
        } catch (BeansException bex) {
            throw new ServletException( bex );
        } catch (NamingException nex) {
            throw new ServletException( nex );
        }
    }

}
