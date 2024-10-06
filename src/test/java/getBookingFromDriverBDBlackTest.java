import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import dataAccess.DataAccess;
import domain.Booking;
import testOperations.TestDataAccess;

public class getBookingFromDriverBDBlackTest {

    //sut: system under test
    static DataAccess sut = new DataAccess();

    //additional operations needed to execute the test 
    static TestDataAccess testDA = new TestDataAccess();

    @Test
    //sut.getBookingFromDriver: The username is null. The test must return null.
    //If an Exception is returned, the getBookingFromDriver method is not well implemented.
    public void test1() {
        String username = null;
        List<Booking> booking = null;

        try {
            //invoke System Under Test (sut)
            sut.open();
            booking = sut.getBookingFromDriver(username);
            sut.close();

            //verify the results
            assertNull(booking);
        } catch (Exception e) {
            //if the program goes to this point, fail
            fail();
        }
    }

    @Test
    //sut.getBookingFromDriver: The username "wowmean" does not exist in the database.
    public void test2() {
        String username = "wowmean";
        List<Booking> booking = null;

        try {
            //invoke System Under Test (sut)
            sut.open();
            booking = sut.getBookingFromDriver(username);
            sut.close();

            //verify the results
            assertNull(booking);
        } catch (Exception e) {
            //if the program goes to this point, fail
            fail();
        } finally {
        }
    }

    @Test
    //sut.getBookingFromDriver: The username "Zuri" is in the database and has bookings.
    public void test3() {
        String username = "Zuri";
        List<Booking> booking = new LinkedList<>();


        try {
            //invoke System Under Test (sut)
            sut.open();
            booking = sut.getBookingFromDriver(username);
            sut.close();

            //verify the results
            assertNotNull(booking);
            // Additional asserts can be added to verify booking details if needed
        } catch (Exception e) {
            //if the program goes to this point, fail
            fail();
        } finally {
            testDA.open();
            testDA.removeDriver(username); // Clean up
            testDA.close();
        }
    }

    
    @Test
    //sut.getBookingFromDriver: The username "HaiYong" is in the database but has no bookings.
    public void test4() {
        String username = "HaiYong";
        List<Booking> booking = new LinkedList<>();
        List<Booking> expectedValue = new LinkedList<>();

        testDA.open();
        testDA.createDriver(username, "123"); // Assume the driver is created but no bookings
        testDA.close();

        try {
            //invoke System Under Test (sut)
            sut.open();
            booking = sut.getBookingFromDriver(username);
            sut.close();

            //verify the results
            assertEquals(expectedValue, booking);
        } catch (Exception e) {
            //if the program goes to this point, fail
            fail();
        } finally {
            testDA.open();
            testDA.removeDriver(username); // Clean up
            testDA.close();
        }
    }
}
