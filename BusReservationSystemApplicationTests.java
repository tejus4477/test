package com.starterkit.springboot.brs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.chrono.HijrahChronology;

import com.starterkit.springboot.brs.model.bus.*;
import com.starterkit.springboot.brs.model.user.*;
import com.starterkit.springboot.brs.repository.bus.*;
import com.starterkit.springboot.brs.repository.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BusReservationSystemApplicationTests {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StopRepository stopRepository;

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripScheduleRepository tripScheduleRepository;

    @InjectMocks
    private BusReservationSystemApplication application;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInit() {
        // Given
        Role adminRole = new Role();
        adminRole.setRole("ADMIN");
        when(roleRepository.findByRole("ADMIN")).thenReturn(adminRole);

        User admin = new User();
        admin.setEmail("admin@gmail.com");
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(null);

        // When
        application.init(roleRepository, userRepository, stopRepository, agencyRepository, busRepository, tripRepository, tripScheduleRepository).run();

        // Then
        verify(roleRepository, times(1)).findByRole("ADMIN");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testStopCreation() {
        // Given
        Stop stopA = new Stop();
        stopA.setCode("STPA");
        when(stopRepository.findByCode("STPA")).thenReturn(null);

        // When
        application.init(roleRepository, userRepository, stopRepository, agencyRepository, busRepository, tripRepository, tripScheduleRepository).run();

        // Then
        verify(stopRepository, times(1)).save(any(Stop.class));
    }

    @Test
    public void testAgencyCreation() {
        // Given
        Agency agencyA = new Agency();
        agencyA.setCode("AGENCY-A");
        when(agencyRepository.findByCode("AGENCY-A")).thenReturn(null);

        User admin = new User();
        admin.setEmail("admin@gmail.com");
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(admin);

        // When
        application.init(roleRepository, userRepository, stopRepository, agencyRepository, busRepository, tripRepository, tripScheduleRepository).run();

        // Then
        verify(agencyRepository, times(1)).save(any(Agency.class));
    }

    @Test
    public void testBusCreation() {
        // Given
        Agency agencyA = new Agency();
        agencyA.setCode("AGENCY-A");
        when(agencyRepository.findByCode("AGENCY-A")).thenReturn(agencyA);

        Bus busA = new Bus();
        busA.setCode("AGENCY-A-1");
        when(busRepository.findByCode("AGENCY-A-1")).thenReturn(null);

        // When
        application.init(roleRepository, userRepository, stopRepository, agencyRepository, busRepository, tripRepository, tripScheduleRepository).run();

        // Then
        verify(busRepository, times(1)).save(any(Bus.class));
    }
}

