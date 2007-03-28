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
package org.nexuse2e.messaging.mime;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

import org.codehaus.xfire.util.Base64;

/**
 * @author mbreilmann
 *
 */
public class binary_base64 implements DataContentHandler {

    private static final String         VERSIONSTRING = "$Id: binary_base64.java 550 2005-03-08 14:37:00Z markus.breilmann $";
    private static ActivationDataFlavor myDF          = new ActivationDataFlavor( byte[].class, "application/pdf",
                                                              "Binary Data" );

    protected ActivationDataFlavor getDF() {

        return myDF;
    }

    /**
     * Return the DataFlavors for this <code>DataContentHandler</code>.
     * @return The DataFlavors
     */
    public DataFlavor[] getTransferDataFlavors() { // throws Exception;

        return new DataFlavor[] { getDF()};
    }

    /**
     * Return the Transfer Data of type DataFlavor from InputStream.
     * @param df The DataFlavor
     * @param ins The InputStream corresponding to the data
     * @return String object
     */
    public Object getTransferData( DataFlavor df, DataSource ds ) throws IOException {

        // use myDF.equals to be sure to get ActivationDataFlavor.equals,
        // which properly ignores Content-Type parameters in comparison
        if ( getDF().equals( df ) ) {
            return Base64.encode( (byte[]) getContent( ds ) );
        } else {
            return null;
        }
    }

    public Object getContent( DataSource ds ) throws IOException {

        InputStreamReader is = new InputStreamReader( ds.getInputStream() );
        int pos = 0;
        int count;
        char buf[] = new char[1024];

        while ( ( count = is.read( buf, pos, 1024 ) ) != -1 ) {
            pos += count;
            char tbuf[] = new char[pos + 1024];
            System.arraycopy( buf, 0, tbuf, 0, pos );
            buf = tbuf;
        }

        byte[] returnBuffer = Base64.decode( buf, 0, buf.length );

        return returnBuffer;
    }

    /** Write the object to the output stream, using the specified MIME type. */
    public void writeTo( Object obj, String type, OutputStream os ) throws IOException {

        if ( !( obj instanceof byte[] ) ) {
            throw new IOException( "\"" + getDF().getMimeType() + "\" DataContentHandler requires byte[] object, "
                    + "was given object of type " + obj.getClass().toString() );
        }
        byte[] byteArray = (byte[]) obj;
        byte[] encodedByteArray = Base64.encode( byteArray ).getBytes();
        os.write( encodedByteArray, 0, encodedByteArray.length );
        os.flush();
    }

} // binary_base64
