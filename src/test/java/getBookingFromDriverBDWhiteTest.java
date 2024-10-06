import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import configuration.UtilDate;
import dataAccess.DataAccess;
import domain.Booking;
import domain.Driver;
import domain.Ride;
import domain.Traveler;
import testOperations.TestDataAccess;

public class getBookingFromDriverBDWhiteTest {

    // sut: system under test
    static DataAccess sut = new DataAccess();
    
    // additional operations needed to execute the test 
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    // sut.getBookingFromDriver: Test case where the driver has bookings
	    public void test1() {
	        String username ="Zuri";
	        List<Booking> bookings = new LinkedList<>();
	
	        try {
	        	testDA.open();
	            Driver driver1 = testDA.createDriver(username, "456");
	            driver1.setBalorazioa(10); 
	            driver1.setBalkop(3);       
	            
	            Calendar cal = Calendar.getInstance();
				cal.set(2024, Calendar.MAY, 20);
				Date date1 = UtilDate.trim(cal.getTime());
	       
	            Ride ride5 = driver1.addRide("Donostia", "Hondarribi", date1, 5, 3);
	
	          
	
	            Traveler traveler1 = new Traveler("Unax", "789");
	            traveler1.setIzoztatutakoDirua(68); 
	            traveler1.setMoney(100);            
	            traveler1.setBalorazioa(14);         
	            traveler1.setBalkop(4);              
	
	            Booking booking1 = new Booking(ride5, traveler1, 1);
	            booking1.setStatus("Accepted");
	            
	            List<Booking> bokings = new ArrayList<Booking>();
	            bokings.add(booking1);
	            ride5.setBookings(bookings);
	
	            sut.open();
	            bookings = sut.getBookingFromDriver(username);
	            sut.close();
	            
	            testDA.close();
	            assertNotNull(bookings);
	
	        } catch (Exception e) {
	            fail();
	        } finally {
	            testDA.open();
	            testDA.removeDriver(username); // Clean up
	            testDA.close();
	        }
	    }

    @Test
    // sut.getBookingFromDriver: Test case where the driver is null
    public void test2() {
        String username = null;
        List<Booking> bookings = new LinkedList<>();

        try {
            testDA.open();
            testDA.close();

            sut.open();
            bookings = sut.getBookingFromDriver(username);
            sut.close();

            // Verify that the bookings list is empty
            assertNull(bookings);

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    // sut.getBookingFromDriver: Test case where the driver does not exist in DB
    public void test3() {
        String username = "wowmean";
        List<Booking> bookings = null;

        try {
            sut.open();
            bookings = sut.getBookingFromDriver(username);
            sut.close();

            // Verify that the result is null since the driver doesn't exist
            assertNull(bookings);

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    // sut.getBookingFromDriver: Test case where the driver exists in DB but has no bookings
    public void test4() {
        String username = "HaiYong";
        List<Booking> bookings = new LinkedList<>();

        try {
            testDA.open();
            testDA.createDriver(username, "password");
            testDA.close();

            sut.open();
            bookings = sut.getBookingFromDriver(username);
            sut.close();

            // Verify that the bookings list is empty
            assertNotNull(bookings);
            assertTrue(bookings.isEmpty());

        } catch (Exception e) {
            fail();
        } finally {
            testDA.open();
            testDA.removeDriver(username); // Clean up
            testDA.close();
        }
    }
}

