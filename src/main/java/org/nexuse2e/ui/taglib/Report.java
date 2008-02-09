package org.nexuse2e.ui.taglib;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.nexuse2e.reporting.BirtEngine;
import org.nexuse2e.reporting.ReportImageHandlerServlet;

/**
 * Renders a report using the BIRT framework.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class Report extends BodyTagSupport {

    private static final long serialVersionUID = 1L;

    private String name;
    private Map<String, Object> parameters;

    public Report() {
        parameters = new HashMap<String, Object>();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }
    
    @Override
    public int doEndTag() throws JspException {
        final JspWriter out = pageContext.getOut();
        OutputStream os = new OutputStream() {
            @Override
            public void write(byte[] b, int off, int len)
                    throws IOException {
                out.write( new String( b ), off, len);
            }

            @Override
            public void write(byte[] b) throws IOException {
                out.write( new String( b ) );
            }

            @Override
            public void write(int b) throws IOException {
                out.write( b );
            }
        };
        String reportName = name;

        try {
            //get report name and launch the engine
            ServletContext sc = pageContext.getSession().getServletContext();
            IReportEngine birtReportEngine = (IReportEngine) getValue( "birtReportEngine" );
            if (birtReportEngine == null) {
                birtReportEngine = BirtEngine.getBirtEngine(sc);
            }
            
            //setup image directory
            IReportRunnable design;
            HTMLServerImageHandler handler = new HTMLServerImageHandler() {
                @Override
                public String onCustomImage(IImage image, IReportContext context) {
                    ReportImageHandlerServlet imageHandler = ReportImageHandlerServlet.getInstance();
                    return imageHandler.registerImage( image );
                }
            };

            //Open report design
            String reportPath = sc.getRealPath("/WEB-INF/reports") + "/" + reportName + ".rptdesign";
            design = birtReportEngine.openReportDesign( reportPath );
            //create task to run and render report
            IRunAndRenderTask task = birtReportEngine.createRunAndRenderTask( design );
            for (String name : parameters.keySet()) {
                Object value = parameters.get( name );
                task.setParameterValue( name, value );
            }

            //set output options
            HTMLRenderOption options = new HTMLRenderOption();
            options.setEmbeddable( true );
            options.setImageHandler( handler );
            options.setOutputFormat( HTMLRenderOption.OUTPUT_FORMAT_HTML );
            String imageBasePath = ReportImageHandlerServlet.getInstance().getImageBasePath();
            options.setBaseImageURL( imageBasePath );
            options.setOutputStream( os );
            task.setRenderOption( options );
            
            //run report
            task.run();
            task.close();
        } catch (UnsupportedOperationException uoex) {
            // BIRT not supported
        } catch (Exception e){
            throw new JspException( e );
        }
        return EVAL_PAGE;
    }
    
    public void setParameter( String name, Object value ) {
        parameters.put( name, value );
    }
}
