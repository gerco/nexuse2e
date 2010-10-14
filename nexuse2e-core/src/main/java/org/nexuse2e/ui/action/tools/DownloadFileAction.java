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
package org.nexuse2e.ui.action.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.action.tools.FileDownloadAction.Tuple;

/**
 * Created: 07.01.2008
 * TODO Class documentation
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class DownloadFileAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction(
            ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response,
            EngineConfiguration engineConfiguration,
            ActionMessages errors,
            ActionMessages messages) throws Exception {
        
        String filename = request.getParameter( "file" );
        if (filename == null || filename.length() == 0) {
            return null;
        }
        
        boolean compress = Boolean.TRUE.toString().equals( request.getParameter( "compress" ) );

        File file = new File( filename );
        boolean allow = false;
        for (Tuple tuple : FileDownloadAction.getTuples()) {
            for (File f : tuple.getFiles()) {
                if (f.equals( file )) {
                    allow = true;
                    break;
                }
            }
        }
        if (!allow) {
            return null;
        }
        
        response.setContentType( "application/octet-stream" );
        response.setHeader( "Content-Disposition", "attachment; filename=" + file.getName() + (compress ? ".zip" : "") );
        JAXBContext jaxbContext = JAXBContext.newInstance( EngineConfiguration.class );
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        OutputStream os = response.getOutputStream();
        if (compress) {
            ZipOutputStream zos = new ZipOutputStream( os );
            zos.putNextEntry( new ZipEntry( file.getName() ) );
            os = zos;
        }
        
        FileInputStream fin = FileUtils.openInputStream( file );
        
        IOUtils.copy( fin, os );
        fin.close();
        os.flush();
        if (compress) {
            ((ZipOutputStream) os).closeEntry();
            ((ZipOutputStream) os).close();
        }
        
        return null;
    }

}
