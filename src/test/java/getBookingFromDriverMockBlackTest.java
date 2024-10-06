import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
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

import configuration.UtilDate;
import dataAccess.DataAccess;
import domain.Booking;
import domain.Driver;
import domain.Ride;
import domain.Traveler;

public class getBookingFromDriverMockBlackTest {

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
    // sut.getBookingFromDriver: The Driver("Driver Test") has bookings.
    public void test1() 
    {
    	Mockito.doReturn(driver).when(query).getSingleResult();
		sut.open();
		List<Booking> bookings = sut.getBookingFromDriver(driver.getUsername());
		sut.close();
		
		// Verify the results
		assertNotNull(bookings);
    }

    @Test
    // sut.getBookingFromDriver: The driver does not exist.
    public void test2() {

    	Mockito.doReturn(null).when(query).getSingleResult();
        try {
            // Invoke System Under Test (sut)  
    		sut.open();
    		List<Booking> bookings = sut.getBookingFromDriver("wowmean");
    		sut.close();

            // Verify the results
            assertNull(bookings);
        } catch (Exception e) {
            fail(); // If we reach here, it means the test failed
        }
    }

    @Test
    // sut.getBookingFromDriver: The driver has no bookings.
    public void test3() {
        Mockito.doReturn(driver2).when(query).getSingleResult();
        try {
            // Invoke System Under Test (sut)  
            sut.open();
            List<Booking> retrievedRide = sut.getBookingFromDriver(driver2.getUsername());
            sut.close();

            // Verify the results
            
            assertNotNull(retrievedRide);
        } catch (Exception e) {
            fail(); // If we reach here, it means the test failed
        }
    }

    @Test
    // sut.getBookingFromDriver: The retrieved booking is null.
    public void test4() {
        String driverUsername = null;

        // Create a driver and add a ride but return null for the booking
        driver = new Driver(driverUsername, "123");
        
        // Configure the state through mocks
        Mockito.when(db.find(Driver.class, driverUsername)).thenReturn(driver);
        
        // Invoke System Under Test (sut)  
        sut.open();
        List<Booking> retrievedRide = sut.getBookingFromDriver(driverUsername);
        sut.close();

        // Verify the results
        assertNull(retrievedRide);
    }
}
