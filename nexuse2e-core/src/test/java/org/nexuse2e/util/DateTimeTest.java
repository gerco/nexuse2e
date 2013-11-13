/**
 * 
 */
package org.nexuse2e.util;

import static org.junit.Assert.*;

import java.util.Date;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author jjerke
 *
 */
@SuppressWarnings("unused")
public class DateTimeTest {
    
    private DateTime dt = new DateTime();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void constructFromShortArray() {
        short[] parameters = {19, 70, 1, 1, 1, 0, 3, 0};
        DateTime dt = new DateTime(parameters);
        
        assertTrue(19 == dt.getCentury());
        assertTrue(70 == dt.getYear());
        assertTrue(1 == dt.getMonth());
        assertTrue(1 == dt.getDay());
        assertTrue(1 == dt.getHour());
        assertTrue(0 == dt.getMinute());
        assertTrue(3 == dt.getSeconds());
        assertTrue(0 == dt.getMilli());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void constructFromShortArrayWithInvalidLength() {
        short[] parameters = {19, 70, 1, 1};
        DateTime dt = new DateTime(parameters);
    }

    @Test
    public void constructFromLong() {
        DateTime dt = new DateTime(3600L);
        
        assertTrue(19 == dt.getCentury());
        assertTrue(70 == dt.getYear());
        assertTrue(1 == dt.getMonth());
        assertTrue(1 == dt.getDay());
        assertTrue(1 == dt.getHour());
        assertTrue(0 == dt.getMinute());
        assertTrue(3 == dt.getSeconds());
        assertTrue(600 == dt.getMilli());
    }
    
    @Test
    public void constructFromString() throws ParseException {
        DateTime dt = new DateTime("1970-01-01T01:00:03.600+01:00");
        
        assertTrue(19 == dt.getCentury());
        assertTrue(70 == dt.getYear());
        assertTrue(1 == dt.getMonth());
        assertTrue(1 == dt.getDay());
        assertTrue(1 == dt.getHour());
        assertTrue(0 == dt.getMinute());
        assertTrue(3 == dt.getSeconds());
        assertTrue(600 == dt.getMilli());
    }
    
    @Test(expected=ParseException.class)
    public void constructFromStringWithInvalidString() throws ParseException {
        DateTime dt = new DateTime("Invalid DateTime string!");
    }
    
    @Test
    public void testValues() {
        short[] parameters = {19, 70, 1, 1, 1, 0, 3, 0};
        DateTime dt = new DateTime(parameters);
        
        short[] returned = dt.getValues();
        assertTrue(19 == returned[0]);
        assertTrue(70 == returned[1]);
        assertTrue(1 == returned[2]);
        assertTrue(1 == returned[3]);
        assertTrue(1 == returned[4]);
        assertTrue(0 == returned[5]);
        assertTrue(3 == returned[6]);
        assertTrue(0 == returned[7]);
        
        Date returnedDate = dt.toDate();
        assertTrue(3000L == returnedDate.getTime());
        
        assertTrue(3000L == dt.toLong());
        
        assertTrue("1970-01-01T01:00:03".equalsIgnoreCase(dt.toString()));
    }
    
    @Test
    public void testParses() throws ParseException {
        DateTime dtOne = DateTime.parse("1970-01-01T01:00:03.600+01:00");
        assertTrue(19 == dtOne.getCentury());
        assertTrue(70 == dtOne.getYear());
        assertTrue(1 == dtOne.getMonth());
        assertTrue(1 == dtOne.getDay());
        assertTrue(1 == dtOne.getHour());
        assertTrue(0 == dtOne.getMinute());
        assertTrue(3 == dtOne.getSeconds());
        assertTrue(600 == dtOne.getMilli());
        
        DateTime dtTwo = DateTime.parse("1970-01-01T01:00:03.600+01:00");
        assertTrue(19 == dtTwo.getCentury());
        assertTrue(70 == dtTwo.getYear());
        assertTrue(1 == dtTwo.getMonth());
        assertTrue(1 == dtTwo.getDay());
        assertTrue(1 == dtTwo.getHour());
        assertTrue(0 == dtTwo.getMinute());
        assertTrue(3 == dtTwo.getSeconds());
        assertTrue(600 == dtTwo.getMilli());
    }

}
