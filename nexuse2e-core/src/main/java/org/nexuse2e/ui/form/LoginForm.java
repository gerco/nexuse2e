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
package org.nexuse2e.ui.form;

import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for login action.
 * @author Sebastian Schulze
 * @date 29.12.2006
 */
public class LoginForm extends ActionForm {

    private static final long serialVersionUID = 1880183009540485505L;

    private String            user;
    private String            pass;

    /**
     * @return the pass
     */
    public String getPass() {

        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass( String pass ) {

        this.pass = pass;
    }

    /**
     * @return the user
     */
    public String getUser() {

        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser( String user ) {

        this.user = user;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.ServletRequest)
     */
    @Override
    public void reset( ActionMapping actionMapping, ServletRequest request ) {

        user = null;
        pass = null;
        super.reset( actionMapping, request );
    }

}
