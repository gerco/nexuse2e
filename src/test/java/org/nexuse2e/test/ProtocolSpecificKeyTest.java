/**
 * *#* ©2006 The Tamalpais Group, Inc., Xioma *+*
 */
package org.nexuse2e.test;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;
import org.nexuse2e.ProtocolSpecificKey;



/**
 * @author gesch
 *
 */
public class ProtocolSpecificKeyTest {

    
    private ProtocolSpecificKey a = null;
    private ProtocolSpecificKey b = null;
    private ProtocolSpecificKey c = null;
    
    
    @Before
    public void setUp()
    {
        a = new ProtocolSpecificKey();
        a.setCommunicationProtocolId( "ebxml" );
        a.setCommunicationProtocolVersion( "2.0" );
        a.setTransportProtocolId( "html" );
        
        b = new ProtocolSpecificKey();
        b.setCommunicationProtocolId( "ebxml" );
        b.setCommunicationProtocolVersion( "2.0" );
        b.setTransportProtocolId( "html" );
        
        c = new ProtocolSpecificKey("a","b","c");
        c.setCommunicationProtocolId( "ebxml" );
        c.setCommunicationProtocolVersion( "1.0" );
        c.setTransportProtocolId( "mail" );
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
        String reference = "ProtocolKey: ebxml 2.0 (html)";
        assertEquals( "toStringSchema", true ,reference.equals( sample ) );
    }
    
    @Test
    public void getterAndSetter() {
        ProtocolSpecificKey d = new ProtocolSpecificKey();
        assertEquals( "empty Object, null expected for any getter",true, d.getCommunicationProtocolId() == null );
        assertEquals( "empty Object, null expected for any getter",true, d.getCommunicationProtocolVersion() == null );
        assertEquals( "empty Object, null expected for any getter",true, d.getTransportProtocolId() == null );
        
        String testid = "interstingTest-ID"+1;
        d.setCommunicationProtocolId( testid );
        assertEquals( "comparing get und set", true, d.getCommunicationProtocolId().equals( testid ) );
        
        testid = "interstingTest-ID"+2;
        d.setCommunicationProtocolVersion( testid );
        assertEquals( "comparing get und set", true, d.getCommunicationProtocolVersion().equals( testid ) );
        
        testid = "interstingTest-ID"+3;
        d.setTransportProtocolId( testid );
        assertEquals( "comparing get und set", true, d.getTransportProtocolId().equals( testid ) );
        
        
    }
    
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ProtocolSpecificKeyTest.class);
    }
    
}
