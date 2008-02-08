package org.nexuse2e.reporting;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.engine.api.IImage;


/**
 * Created: 07.02.2008
 * TODO Class documentation
 *
 * @author jr
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportImageHandlerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private static ReportImageHandlerServlet instance;
    
    private Map<String, ImageRecord> map;
    private int count = 0;
    
    
    public ReportImageHandlerServlet() {
        map = new HashMap<String, ImageRecord>();
        instance = this;
    }
    
    public static ReportImageHandlerServlet getInstance() {
        return instance;
    }
    
    public String getImageBasePath() {
        return "reportimage/";
    }
    
    public String registerImage( IImage image ) {
        Date date = new Date();
        List<String> cleanList = new ArrayList<String>();
        synchronized (map) {
            String s = Long.toString( date.getTime() ) + "_" + count++ + image.getExtension();
            // clean up images after one minute
            for (String key : map.keySet()) {
                ImageRecord value = map.get( key );
                if (date.getTime() - value.timestamp.getTime() > 60000) {
                    cleanList.add( key );
                }
            }
            for (String key : cleanList) {
                map.remove( key );
            }
            map.put( s, new ImageRecord( image, date ) );
            return getImageBasePath() + s;
        }
    }

    public void doGet( HttpServletRequest req, HttpServletResponse resp )
    throws ServletException, IOException {
        ImageRecord imageRecord;
        String uri = req.getRequestURI();
        int index = uri.lastIndexOf( '/' );
        String key;
        if (index >= 0) {
            key = uri.substring( index + 1 );
        } else {
            key = uri;
        }
        synchronized (map) {
            imageRecord = map.get( key );
        }
        if (imageRecord != null) {
            resp.getOutputStream().write( imageRecord.image.getImageData() );
            resp.setStatus( HttpServletResponse.SC_OK );
        } else {
            resp.setStatus( HttpServletResponse.SC_NOT_FOUND );
        }
    }
    
    static class ImageRecord {
        IImage image;
        Date timestamp;
        
        ImageRecord( IImage image, Date timestamp ) {
            this.image = image;
            this.timestamp = timestamp;
        }
    }
}
