package com.busko.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound message sent by the driver app over STOMP to {@code /app/driver/update}.
 * Contains the bus's current GPS position and identifiers for the bus and route.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateMessage {

    private double lat;
    private double lng;
    private Long busId;
    private Long routeId;
}
