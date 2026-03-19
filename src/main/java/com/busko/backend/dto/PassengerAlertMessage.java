package com.busko.backend.dto;

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
public class PassengerAlertMessage {

    private Long busId;
    private double lat;
    private double lng;
    private String proximityStatus;

    public PassengerAlertMessage() {}

    public PassengerAlertMessage(Long busId, double lat, double lng, String proximityStatus) {
        this.busId = busId;
        this.lat = lat;
        this.lng = lng;
        this.proximityStatus = proximityStatus;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
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

    public String getProximityStatus() {
        return proximityStatus;
    }

    public void setProximityStatus(String proximityStatus) {
        this.proximityStatus = proximityStatus;
    }
}
