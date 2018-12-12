package de.k3b.android.locationMapViewer;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MyTestTest extends TestCase {
    private MyTest test;
    SimpleDateFormat simFormat = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat simFormat1 = new SimpleDateFormat("HH:mm:ss");
    @Before
    public void setUp(){
        test=new MyTest();
    }
    @Test
    public void testSetUTC0() {
        assertEquals(test.setUTC(0),0);
    }
    @Test
    public void testSetUTC1() {
        assertEquals(test.setUTC(0),1);
    }
    @Test
    public void testSetUTC2() {
        assertEquals(test.setUTC(-7.5),0);
    }
    @Test
    public void testSetUTC3() {
        assertEquals(test.setUTC(-7.5),1);
    }
    @Test
    public void testSetUTC4() {
        assertEquals(test.setUTC(172.5),12);
    }
    @Test
    public void testSetUTC5() {
        assertEquals(test.setUTC(172.5),11);
    }
    @Test
    public void testSetUTC6() {
        assertEquals(test.setUTC(66.5),4);
    }
    @Test
    public void testSetUTC7() {
        assertEquals(test.setUTC(66.5),5);
    }
    @Test
    public void testSetUTC8() {
        assertEquals(test.setUTC(-98.5),-7);
    }
    @Test
    public void testSetUTC9() {
        assertEquals(test.setUTC(-98.5),-6);
    }
    @Test
    public void testSetLocaltime0() throws ParseException {
        assertEquals(test.setLocaltime(simFormat.parse("00:00:00"),8),"08:00:00");
    }
    @Test
    public void testSetLocaltime1() throws ParseException {
        assertEquals(test.setLocaltime(simFormat.parse("00:00:00"),-8),"16:00:00");
    }
    @Test
    public void testSetLocaltime2() throws ParseException {
        assertEquals(test.setLocaltime(simFormat.parse("00:00:00"),-8),"08:00:00");
    }
    @Test
    public void testSetLocaltime3() throws ParseException {
        assertEquals(test.setLocaltime(simFormat.parse("20:00:00"),8),"04:00:00");
    }
    @Test
    public void testSetLocaltime4() throws ParseException {
        assertEquals(test.setLocaltime(simFormat.parse("20:00:00"),8),"28:00:00");
    }
    @Test
    public void testSetLocaltime5() throws ParseException {
        assertEquals(test.setLocaltime(simFormat.parse("12:00:00"),8),"20:00:00");
    }
}