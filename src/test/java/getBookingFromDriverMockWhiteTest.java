import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.Booking;
import domain.Driver;
import domain.Ride;
import domain.Traveler;

public class getBookingFromDriverMockWhiteTest {

    static DataAccess sut;

    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;

    TypedQuery query;

    Driver driver, driver2;
    Ride ride;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
            .thenReturn(entityManagerFactory);
        
        driver = new Driver("Urtzi", "123");
		driver.setMoney(15);
		driver.setBalorazioa(14);
		driver.setBalkop(3);
		
		ride = driver.addRide("Donostia", "Madrid", Date.from(Instant.now()), 5, 20); 
		ride.setBookings(Arrays.asList(new Booking(ride, new Traveler("traveler", "pass"), 1)));	
		
        driver2 = new Driver("Roman", "123");
		driver2.setMoney(15);
		driver2.setBalorazioa(14);
		driver2.setBalkop(3);
        
        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        
        query = Mockito.mock(TypedQuery.class);
        Mockito.doReturn(query).when(db).createQuery(Mockito.anyString(), Mockito.any());
        
        sut = new DataAccess(db);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    @Test
    // White box test: Verify correct bookings are returned for an existing driver
    public void test1() 
    {
    	Mockito.doReturn(driver).when(query).getSingleResult();
		sut.open();
		List<Booking> bookings = sut.getBookingFromDriver(driver.getUsername());
		sut.close();
		
		// Verify the results
		assertNotNull(bookings);
		assertEquals(1, bookings.size());
		assertEquals(ride, bookings.get(0).getRide());
		assertEquals("traveler", bookings.get(0).getTraveler().getUsername());
    }

    @Test
    // White box test: No bookings for driver
    public void test2() {
        Mockito.doReturn(driver2).when(query).getSingleResult();
        
        sut.open();
        List<Booking> bookings = sut.getBookingFromDriver(driver2.getUsername());
        sut.close();
        
        // Verify the results
        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    // White box test: Null driver username
    public void test3() {
        String driverUsername = null;

        // Create a driver and return null for driver in query
        Mockito.doReturn(null).when(query).getSingleResult();
        
        // Invoke System Under Test (sut)
        sut.open();
        List<Booking> bookings = sut.getBookingFromDriver(driverUsername);
        sut.close();

        // Verify the results
        assertNull(bookings);
    }

    @Test
    // White box test: Invalid driver (driver not found)
    public void test4() {
        String driverUsername = "invalidDriver";

        // Mock behavior for invalid driver
        Mockito.doReturn(null).when(query).getSingleResult();
        
        sut.open();
        List<Booking> bookings = sut.getBookingFromDriver(driverUsername);
        sut.close();

        // Verify the results
        assertNull(bookings);
    }
}
