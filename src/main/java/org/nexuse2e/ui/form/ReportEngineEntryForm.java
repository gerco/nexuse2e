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
package org.nexuse2e.ui.form;

import java.text.SimpleDateFormat;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.struts.action.ActionForm;
import org.nexuse2e.pojo.LogPojo;

/**
 * @author mbreilmann
 *
 */
public class ReportEngineEntryForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = -3768732889563738711L;

    private String            severity         = null;
    private String            issuedDate       = null;
    private String            description      = null;
    private String            className        = null;
    private String            methodName       = null;

    public void setEnginePorperties( LogPojo pojo ) {

        switch ( pojo.getSeverity() ) {
            case Priority.FATAL_INT:
                setSeverity( Level.FATAL.toString() );
                break;
            case Priority.ERROR_INT:
                setSeverity( Level.ERROR.toString() );
                break;
            case Priority.WARN_INT:
                setSeverity( Level.WARN.toString() );
                break;
            case Priority.INFO_INT:
                setSeverity( Level.INFO.toString() );
                break;
            case Priority.DEBUG_INT:
                setSeverity( Level.DEBUG.toString() );
                break;
            case Priority.ALL_INT:
                setSeverity( Level.ALL.toString() );
                break;
            default:
                break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss.SSS" );
        setIssuedDate( sdf.format( pojo.getCreatedDate() ) );
        setDescription( pojo.getDescription() );
        setClassName( pojo.getClassName() );
        setMethodName( pojo.getMethodName() );
    }

    public String getClassName() {

        return className;
    }

    public void setClassName( String className ) {

        this.className = className;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription( String description ) {

        this.description = description;
    }

    public String getIssuedDate() {

        return issuedDate;
    }

    public void setIssuedDate( String issuedDate ) {

        this.issuedDate = issuedDate;
    }

    public String getMethodName() {

        return methodName;
    }

    public void setMethodName( String methodName ) {

        this.methodName = methodName;
    }

    public String getSeverity() {

        return severity;
    }

    public void setSeverity( String severity ) {

        this.severity = severity;
    }

} // ReportEngineEntryForm
