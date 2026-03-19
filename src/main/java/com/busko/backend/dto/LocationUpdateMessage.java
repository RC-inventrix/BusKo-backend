package com.busko.backend.dto;

/**
 * Inbound message sent by the driver app over STOMP to {@code /app/driver/update}.
 * Contains the bus's current GPS position and identifiers for the bus and route.
 */
public class LocationUpdateMessage {

    private double lat;
    private double lng;
    private Long busId;
    private Long routeId;

    public LocationUpdateMessage() {}

    public LocationUpdateMessage(double lat, double lng, Long busId, Long routeId) {
        this.lat = lat;
        this.lng = lng;
        this.busId = busId;
        this.routeId = routeId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
}
