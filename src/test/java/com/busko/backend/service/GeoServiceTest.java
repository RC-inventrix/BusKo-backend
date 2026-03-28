package com.busko.backend.service;

import com.busko.backend.dto.LocationUpdateMessage;
import com.busko.backend.dto.PassengerAlertMessage;
import com.busko.backend.entity.Bus;
import com.busko.backend.entity.BusLocation;
import com.busko.backend.entity.Route;
import com.busko.backend.repository.BusLocationRepository;
import com.busko.backend.repository.BusRepository;
import com.busko.backend.repository.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoServiceTest {

    @Mock
    private BusLocationRepository busLocationRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private GeoService geoService;

    private Bus bus;
    private Route route;

    @BeforeEach
    void setUp() {
        bus = new Bus();
        bus.setId(1L);
        bus.setRegistrationNumber("ABC-1234");

        route = new Route();
        route.setId(2L);
    }

    @Test
    void processDriverUpdate_returnsGreenStatus_whenNoNearbyBuses() {
        LocationUpdateMessage message = new LocationUpdateMessage(6.9271, 79.8612, 1L, 2L);
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(routeRepository.findById(2L)).thenReturn(Optional.of(route));
        when(busLocationRepository.findBusesWithinRadius(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(busLocationRepository.save(any(BusLocation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PassengerAlertMessage alert = geoService.processDriverUpdate(message);

        assertThat(alert.getBusId()).isEqualTo(1L);
        assertThat(alert.getLat()).isEqualTo(6.9271);
        assertThat(alert.getLng()).isEqualTo(79.8612);
        assertThat(alert.getProximityStatus()).isEqualTo("Green");
    }

    @Test
    void processDriverUpdate_returnsRedStatus_whenBusWithin300m() {
        LocationUpdateMessage message = new LocationUpdateMessage(6.9271, 79.8612, 1L, 2L);
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(routeRepository.findById(2L)).thenReturn(Optional.of(route));
        // First call (300 m) returns a nearby bus -> Red
        when(busLocationRepository.findBusesWithinRadius(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(new BusLocation()));
        when(busLocationRepository.save(any(BusLocation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PassengerAlertMessage alert = geoService.processDriverUpdate(message);

        assertThat(alert.getProximityStatus()).isEqualTo("Red");
    }

    @Test
    void processDriverUpdate_returnsYellowStatus_whenBusWithin1000mButNot300m() {
        LocationUpdateMessage message = new LocationUpdateMessage(6.9271, 79.8612, 1L, 2L);
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(routeRepository.findById(2L)).thenReturn(Optional.of(route));
        // 300 m radius returns empty; 1000 m radius returns a bus -> Yellow
        when(busLocationRepository.findBusesWithinRadius(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList())
                .thenReturn(List.of(new BusLocation()));
        when(busLocationRepository.save(any(BusLocation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PassengerAlertMessage alert = geoService.processDriverUpdate(message);

        assertThat(alert.getProximityStatus()).isEqualTo("Yellow");
    }

    @Test
    void processDriverUpdate_persistsBusLocation() {
        LocationUpdateMessage message = new LocationUpdateMessage(6.9271, 79.8612, 1L, 2L);
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(routeRepository.findById(2L)).thenReturn(Optional.of(route));
        when(busLocationRepository.findBusesWithinRadius(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        ArgumentCaptor<BusLocation> captor = ArgumentCaptor.forClass(BusLocation.class);
        when(busLocationRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        geoService.processDriverUpdate(message);

        BusLocation saved = captor.getValue();
        assertThat(saved.getBus()).isEqualTo(bus);
        assertThat(saved.getRoute()).isEqualTo(route);
        assertThat(saved.getLocation()).isNotNull();
        assertThat(saved.getTimestamp()).isNotNull();
    }

    @Test
    void processDriverUpdate_throwsException_whenBusNotFound() {
        LocationUpdateMessage message = new LocationUpdateMessage(6.9271, 79.8612, 99L, 2L);
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> geoService.processDriverUpdate(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bus not found");
    }

    @Test
    void processDriverUpdate_throwsException_whenRouteNotFound() {
        LocationUpdateMessage message = new LocationUpdateMessage(6.9271, 79.8612, 1L, 99L);
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> geoService.processDriverUpdate(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Route not found");
    }

    @Test
    void processDriverUpdate_throwsException_whenLatitudeOutOfRange() {
        LocationUpdateMessage message = new LocationUpdateMessage(91.0, 79.8612, 1L, 2L);

        assertThatThrownBy(() -> geoService.processDriverUpdate(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude");
    }

    @Test
    void processDriverUpdate_throwsException_whenLongitudeOutOfRange() {
        LocationUpdateMessage message = new LocationUpdateMessage(6.9271, 181.0, 1L, 2L);

        assertThatThrownBy(() -> geoService.processDriverUpdate(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude");
    }
}
