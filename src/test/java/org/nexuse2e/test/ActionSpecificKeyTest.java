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
/**
 * *#* �2006 The Tamalpais Group, Inc., Xioma *+*
 */
package org.nexuse2e.test;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;
import org.nexuse2e.ActionSpecificKey;



/**
 * @author gesch
 *
 */
public class ActionSpecificKeyTest {

    
    ActionSpecificKey a = null;
    ActionSpecificKey b = null;
    ActionSpecificKey c = null;
    
    @Before
    public void setUp()  {
        a = new ActionSpecificKey();
        a.setActionId( "action1" );
        a.setChoreographyId( "choreography1" );
        
        b = new ActionSpecificKey();
        b.setActionId( "action1" );
        b.setChoreographyId( "choreography1" );
        
        c = new ActionSpecificKey("a","c");
        c.setActionId( "action2" );
        c.setChoreographyId( "choreography1" );
    }
    @Test 
    public void comparingKeys() {
        
        assertEquals( "Same Object", true,a.equals( a ) );
        
        assertEquals( "Same Values, different objects", true,a.equals( b ) );
        
        assertEquals( "different values", false ,a.equals( c ) );
    }
    
    @Test 
    public void toStringSchema() {
        String sample = a.toString();
        String reference = "ActionKey: choreography1/action1";
        assertEquals( "toStringSchema", true ,reference.equals( sample ) );
    }
    
    @Test
    public void getterAndSetter() {
        ActionSpecificKey d = new ActionSpecificKey();
        assertEquals( "empty Object, null expected for any getter",true, d.getChoreographyId() == null );
        assertEquals( "empty Object, null expected for any getter",true, d.getActionId() == null );
        
        String testid = "interstingTest-ID"+1;
        d.setChoreographyId( testid );
        assertEquals( "comparing get und set", true, d.getChoreographyId().equals( testid ) );
        
        testid = "interstingTest-ID"+2;
        d.setActionId( testid );
        assertEquals( "comparing get und set", true, d.getActionId().equals( testid ) );
        
        
        
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ActionSpecificKeyTest.class);
    }
    
}
