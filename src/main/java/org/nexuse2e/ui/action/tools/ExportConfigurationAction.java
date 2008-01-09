package org.nexuse2e.ui.action.tools;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.ui.action.NexusE2EAction;

/**
 * Created: 07.01.2008
 * TODO Class documentation
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ExportConfigurationAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction(
            ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response, ActionMessages errors,
            ActionMessages messages) throws Exception {

        response.setContentType( "text/xml" );
        response.setHeader( "Content-Disposition", "attachment; filename=\"NEXUSe2e_configuration_"
                + new SimpleDateFormat( "yyyyMMddHHmm" ).format( new Date() ) + ".xml\"" );
        JAXBContext jaxbContext = JAXBContext.newInstance( EngineConfiguration.class );
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        OutputStream os = response.getOutputStream();
        marshaller.marshal(
                Engine.getInstance().getActiveConfigurationAccessService().getEngineConfig(), os );
        os.flush();
        
        return null;
    }

}
