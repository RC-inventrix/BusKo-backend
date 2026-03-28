package com.busko.backend.controller;

import com.busko.backend.dto.LocationUpdateMessage;
import com.busko.backend.dto.PassengerAlertMessage;
import com.busko.backend.service.GeoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusLocationControllerTest {

    @Mock
    private GeoService geoService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private BusLocationController controller;

    @Test
    void handleDriverUpdate_callsGeoServiceAndBroadcastsToCorrectTopic() {
        LocationUpdateMessage message = new LocationUpdateMessage(6.9271, 79.8612, 1L, 5L);
        PassengerAlertMessage alert = new PassengerAlertMessage(1L, 6.9271, 79.8612, "Green");
        when(geoService.processDriverUpdate(message)).thenReturn(alert);

        controller.handleDriverUpdate(message);

        verify(geoService).processDriverUpdate(message);
        ArgumentCaptor<PassengerAlertMessage> captor =
                ArgumentCaptor.forClass(PassengerAlertMessage.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/route-5"), captor.capture());
        assertThat(captor.getValue().getProximityStatus()).isEqualTo("Green");
    }

    @Test
    void handleDriverUpdate_broadcastsAlertWithCorrectRouteId() {
        LocationUpdateMessage message = new LocationUpdateMessage(6.9271, 79.8612, 1L, 42L);
        PassengerAlertMessage alert = new PassengerAlertMessage(1L, 6.9271, 79.8612, "Red");
        when(geoService.processDriverUpdate(message)).thenReturn(alert);

        controller.handleDriverUpdate(message);

        verify(messagingTemplate).convertAndSend(eq("/topic/route-42"), eq(alert));
    }
}
