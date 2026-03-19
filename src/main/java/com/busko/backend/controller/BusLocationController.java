package com.busko.backend.controller;

import com.busko.backend.dto.LocationUpdateMessage;
import com.busko.backend.dto.PassengerAlertMessage;
import com.busko.backend.service.GeoService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * STOMP WebSocket controller that handles real-time bus location updates.
 *
 * <h2>Data flow</h2>
 * <ol>
 *   <li>The driver app sends a STOMP {@code SEND} frame to
 *       {@code /app/driver/update} carrying a {@link LocationUpdateMessage}
 *       payload (JSON-serialised).</li>
 *   <li>Spring routes the frame to {@link #handleDriverUpdate} because the
 *       {@code /app} application-destination prefix is stripped and the
 *       remaining {@code /driver/update} matches the {@code @MessageMapping}.</li>
 *   <li>{@link GeoService#processDriverUpdate} validates the coordinates,
 *       persists the {@link com.busko.backend.entity.BusLocation} to PostgreSQL,
 *       and computes a proximity status (Green / Yellow / Red).</li>
 *   <li>{@link SimpMessagingTemplate#convertAndSend} broadcasts the resulting
 *       {@link PassengerAlertMessage} to {@code /topic/route-{routeId}}.
 *       Every passenger client subscribed to that topic receives the update
 *       instantly.</li>
 * </ol>
 */
@Controller
public class BusLocationController {

    private final GeoService geoService;
    private final SimpMessagingTemplate messagingTemplate;

    public BusLocationController(GeoService geoService,
                                  SimpMessagingTemplate messagingTemplate) {
        this.geoService = geoService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Receives a GPS position update from a driver and broadcasts the enriched
     * alert to all passengers subscribed to the route's topic.
     *
     * @param message the inbound location update from the driver app
     */
    @MessageMapping("/driver/update")
    public void handleDriverUpdate(LocationUpdateMessage message) {
        PassengerAlertMessage alert = geoService.processDriverUpdate(message);
        messagingTemplate.convertAndSend(
                "/topic/route-" + message.getRouteId(), alert);
    }
}
