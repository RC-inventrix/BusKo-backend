package com.busko.backend.service;

import com.busko.backend.dto.LocationUpdateMessage;
import com.busko.backend.dto.PassengerAlertMessage;
import com.busko.backend.entity.Bus;
import com.busko.backend.entity.BusLocation;
import com.busko.backend.entity.Route;
import com.busko.backend.repository.BusLocationRepository;
import com.busko.backend.repository.BusRepository;
import com.busko.backend.repository.RouteRepository;
import java.time.Instant;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles incoming GPS updates from driver apps, persists bus locations, and
 * computes a proximity status for outbound passenger alerts.
 */
@Service
public class GeoService {

    private static final int SRID_WGS84 = 4326;

    /** Radius (metres) below which a bus is considered "Red" (arriving soon). */
    private static final double RED_THRESHOLD_METERS = 300.0;

    /** Radius (metres) below which a bus is considered "Yellow" (approaching). */
    private static final double YELLOW_THRESHOLD_METERS = 1000.0;

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), SRID_WGS84);

    private final BusLocationRepository busLocationRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public GeoService(BusLocationRepository busLocationRepository,
                      BusRepository busRepository,
                      RouteRepository routeRepository) {
        this.busLocationRepository = busLocationRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    /**
     * Validates and persists the driver's GPS update, then returns a
     * {@link PassengerAlertMessage} ready to be broadcast to passengers.
     *
     * <p>Data flow:
     * <ol>
     *   <li>Sanitise/validate the incoming coordinates.</li>
     *   <li>Resolve the {@link Bus} and {@link Route} from the database.</li>
     *   <li>Build a JTS {@link Point} (SRID 4326) and save a new
     *       {@link BusLocation} record.</li>
     *   <li>Determine the proximity status via the PostGIS ST_DWithin query.</li>
     *   <li>Return the alert DTO that the controller will broadcast.</li>
     * </ol>
     */
    @Transactional
    public PassengerAlertMessage processDriverUpdate(LocationUpdateMessage message) {
        validateCoordinates(message.getLat(), message.getLng());

        Bus bus = busRepository.findById(message.getBusId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Bus not found: " + message.getBusId()));

        Route route = routeRepository.findById(message.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Route not found: " + message.getRouteId()));

        // JTS Point: X = longitude, Y = latitude (GeoJSON / PostGIS convention)
        Point location = GEOMETRY_FACTORY.createPoint(
                new Coordinate(message.getLng(), message.getLat()));

        BusLocation busLocation = new BusLocation();
        busLocation.setBus(bus);
        busLocation.setRoute(route);
        busLocation.setLocation(location);
        busLocation.setTimestamp(Instant.now());
        busLocationRepository.save(busLocation);

        String proximityStatus = calculateProximityStatus(message.getLat(), message.getLng());
        return new PassengerAlertMessage(
                message.getBusId(), message.getLat(), message.getLng(), proximityStatus);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Determines whether the bus is "Red", "Yellow", or "Green" relative to
     * other known bus locations via the PostGIS ST_DWithin radius query.
     *
     * <p>The query counts how many existing bus-location records fall within the
     * given radius of the bus's current position:
     * <ul>
     *   <li><b>Red</b>   – at least one record within {@value #RED_THRESHOLD_METERS} m
     *       (the bus is clustered with other buses / near a stop)</li>
     *   <li><b>Yellow</b> – at least one record within {@value #YELLOW_THRESHOLD_METERS} m</li>
     *   <li><b>Green</b>  – no records in either radius (bus is running freely)</li>
     * </ul>
     *
     * <p>TODO: In production replace this with a passenger-centric proximity
     * check that compares the bus location against the passenger's reported
     * location, received via a separate subscription or REST call.
     */
    private String calculateProximityStatus(double lat, double lng) {
        // Check red threshold first (most urgent)
        if (!busLocationRepository
                .findBusesWithinRadius(lat, lng, RED_THRESHOLD_METERS).isEmpty()) {
            return "Red";
        }
        // Check yellow threshold (only if not red)
        if (!busLocationRepository
                .findBusesWithinRadius(lat, lng, YELLOW_THRESHOLD_METERS).isEmpty()) {
            return "Yellow";
        }
        return "Green";
    }

    private void validateCoordinates(double lat, double lng) {
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90, got: " + lat);
        }
        if (lng < -180 || lng > 180) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180, got: " + lng);
        }
    }
}
