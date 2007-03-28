/**
 * *#* ©2006 The Tamalpais Group, Inc., Xioma *+*
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
