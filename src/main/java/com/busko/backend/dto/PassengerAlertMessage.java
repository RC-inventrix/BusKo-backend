package com.busko.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Outbound message broadcast to passengers on {@code /topic/route-{routeId}}.
 * Contains the bus's latest position together with a human-readable proximity
 * status that indicates how close the bus is to a reference point.
 *
 * <p>Proximity status values:
 * <ul>
 *   <li>{@code "Red"}    – bus is within 300 m of the reference point (arriving soon)</li>
 *   <li>{@code "Yellow"} – bus is within 1 000 m (approaching)</li>
 *   <li>{@code "Green"}  – bus is more than 1 000 m away (en route)</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassengerAlertMessage {

    private Long busId;
    private double lat;
    private double lng;
    private String proximityStatus;
}
