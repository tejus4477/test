import com.starterkit.springboot.brs.model.bus.*;
import com.starterkit.springboot.brs.model.user.Role;
import com.starterkit.springboot.brs.model.user.User;
import com.starterkit.springboot.brs.model.user.UserRoles;
import com.starterkit.springboot.brs.repository.bus.*;
import com.starterkit.springboot.brs.repository.user.RoleRepository;
import com.starterkit.springboot.brs.repository.user.UserRepository;
import com.starterkit.springboot.brs.util.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
public class BusReservationSystemApplicationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripScheduleRepository tripScheduleRepository;

    @BeforeEach
    void setup() {
        // Create roles
        roleRepository.save(new Role().setRole(UserRoles.ADMIN));
        roleRepository.save(new Role().setRole(UserRoles.PASSENGER));

        // Create user
        userRepository.save(new User().setEmail("test@email.com").setPassword("password").setRoles(Arrays.asList(roleRepository.findByRole(UserRoles.PASSENGER))));

        // Create stops
        stopRepository.save(new Stop().setName("Stop A").setDetail("Near hills").setCode("STPA"));
        stopRepository.save(new Stop().setName("Stop B").setDetail("Near river").setCode("STPB"));

        // Create agency
        agencyRepository.save(new Agency().setName("Green Mile Agency").setCode("AGENCY-A").setDetails("Reaching desitnations with ease"));

        // Create bus
        busRepository.save(new Bus().setCode("AGENCY-A-1").setAgency(agencyRepository.findByCode("AGENCY-A")).setCapacity(60));

        // Create trip
        tripRepository.save(new Trip().setSourceStop(stopRepository.findByCode("STPA")).setDestStop(stopRepository.findByCode("STPB")).setBus(busRepository.findByCode("AGENCY-A-1")).setAgency(agencyRepository.findByCode("AGENCY-A")).setFare(100).setJourneyTime(60));

        // Create trip schedule
        tripScheduleRepository.save(new TripSchedule().setTripDetail(tripRepository.findAll().get(0)).setTripDate(DateUtils.todayStr()).setAvailableSeats(tripRepository.findAll().get(0).getBus().getCapacity()));
    }

    @Test
    public void testUserCreation() {
        User user = userRepository.findByEmail("test@email.com");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("test@email.com", user.getEmail());
    }

    @Test
    public void testBusCreation() {
        Bus bus = busRepository.findByCode("AGENCY-A-1");
        Assertions.assertNotNull(bus);
        Assertions.assertEquals("AGENCY-A-1", bus.getCode());
    }

    @Test
    public void testTripCreation() {
        Trip trip = tripRepository.findAll().get(0);
        Assertions.assertNotNull(trip);
        Assertions.assertEquals("STPA", trip.getSourceStop().getCode());
        Assertions.assertEquals("STPB", trip.getDestStop().getCode());
    }

    @Test
    public void testTripScheduleCreation() {
        TripSchedule tripSchedule = tripScheduleRepository.findAll().get(0);
        Assertions.assertNotNull(tripSchedule);
        Assertions.assertEquals(tripRepository.findAll().get(0), tripSchedule.getTripDetail());
        Assertions.assertEquals(DateUtils.todayStr(), tripSchedule.getTripDate());
    }

    @Test
    public void testInvalidUserEmail() {
        Assertions.assertNull(userRepository.findByEmail("invalid@email.com"));
    }

    @Test
    public void testRoleCreation() {
        Role adminRole = roleRepository.findByRole(UserRoles.ADMIN);
        Assertions.assertNotNull(adminRole);
        Assertions.assertEquals(UserRoles.ADMIN, adminRole.getRole());

        Role passengerRole = roleRepository.findByRole(UserRoles.PASSENGER);
        Assertions.assertNotNull(passengerRole);
        Assertions.assertEquals(UserRoles.PASSENGER, passengerRole.getRole());
    }

    @Test
    public void testInvalidRoleCreation() {
        Role invalidRole = new Role().setRole("INVALID_ROLE");
        Assertions.assertNull(roleRepository.save(invalidRole));
    }

    @Test
    public void testBusAgencyAssignment() {
        Bus bus = busRepository.findByCode("AGENCY-A-1");
        Assertions.assertNotNull(bus.getAgency());
        Assertions.assertEquals("AGENCY-A", bus.getAgency().getCode());
    }

    @Test
    public void testTripSourceAndDestinationStopValidation() {
        Trip trip = tripRepository.findAll().get(0);
        Assertions.assertNotNull(trip.getSourceStop());
        Assertions.assertNotNull(trip.getDestStop());
        Assertions.assertNotEquals(trip.getSourceStop(), trip.getDestStop());
    }

    @Test
    public void testTripScheduleAvailability() {
        TripSchedule tripSchedule = tripScheduleRepository.findAll().get(0);
        Assertions.assertEquals(tripRepository.findAll().get(0).getBus().getCapacity(), tripSchedule.getAvailableSeats());
    }

}
