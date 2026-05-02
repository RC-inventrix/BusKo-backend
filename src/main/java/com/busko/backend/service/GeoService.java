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

@Service
public class GeoService {

    private static final int SRID_WGS84 = 4326;
    private static final double RED_THRESHOLD_METERS = 300.0;
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

    @Transactional
    public PassengerAlertMessage processDriverUpdate(LocationUpdateMessage message) {
        validateCoordinates(message.getLat(), message.getLng());

        Bus bus = busRepository.findById(message.getBusId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Bus not found: " + message.getBusId()));

        Route route = routeRepository.findById(message.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Route not found: " + message.getRouteId()));

        Point location = GEOMETRY_FACTORY.createPoint(
                new Coordinate(message.getLng(), message.getLat()));

        // --- UPSERT LOGIC IMPLEMENTED HERE ---
        BusLocation busLocation = busLocationRepository.findByBusId(bus.getId())
                .orElse(new BusLocation()); // Get existing or create new

        busLocation.setBus(bus);
        busLocation.setRoute(route);
        busLocation.setLocation(location);
        busLocation.setTimestamp(Instant.now());

        busLocationRepository.save(busLocation); // Automatically does UPDATE if ID exists

        String proximityStatus = calculateProximityStatus(message.getLat(), message.getLng());
        return new PassengerAlertMessage(
                message.getBusId(), message.getLat(), message.getLng(), proximityStatus);
    }

    private String calculateProximityStatus(double lat, double lng) {
        if (!busLocationRepository
                .findBusesWithinRadius(lat, lng, RED_THRESHOLD_METERS).isEmpty()) {
            return "Red";
        }
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