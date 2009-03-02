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
package org.nexuse2e.ui.taglib;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.nexuse2e.Engine;
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

    private static final String REPORT_BASE_DIR = "/WEB-INF/reports";
    
    private static final String DIALECT_SUFFIX = "Dialect";
    
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
            // find out DB dialect
            String dialect = Engine.getInstance().getDatabaseDialect();
            if (dialect != null) {
                int index = dialect.lastIndexOf( '.' );
                if (index >= 0) {
                    dialect = dialect.substring( index + 1 );
                }
                if (dialect.endsWith( DIALECT_SUFFIX )) {
                    dialect = dialect.substring( 0, dialect.length() - DIALECT_SUFFIX.length() );
                }
                dialect = dialect.toLowerCase();
            }

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

            // Load properties from property file
            File propertyFile = new File( sc.getRealPath( REPORT_BASE_DIR ), reportName + "." + dialect );
            if (!propertyFile.exists()) {
                propertyFile = new File( sc.getRealPath( REPORT_BASE_DIR ), reportName + ".default" );
            }
            Properties properties = new Properties();
            if (propertyFile.exists()) {
                properties.load( new FileInputStream( propertyFile ) );
            }
            
            // Open report design
            File reportFile = new File( sc.getRealPath( REPORT_BASE_DIR ), reportName + ".rptdesign" );
            String report = FileUtils.readFileToString( reportFile, null );
            for (Object property : properties.keySet()) {
                String name = (String) property;
                report = StringUtils.replace( report, "${" + name + "}", properties.getProperty( name ) );
            }
            design = birtReportEngine.openReportDesign( new ByteArrayInputStream( report.getBytes() ) );
            //create task to run and render report
            IRunAndRenderTask task = birtReportEngine.createRunAndRenderTask( design );
            for (String name : parameters.keySet()) {
                Object value = parameters.get( name );
                task.setParameterValue( name, value );
            }
            task.setLocale( java.util.Locale.US );

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
