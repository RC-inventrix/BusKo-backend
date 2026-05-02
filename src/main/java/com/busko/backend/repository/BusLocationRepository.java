package com.busko.backend.repository;

import com.busko.backend.entity.BusLocation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {

    // Added to support the Upsert pattern
    Optional<BusLocation> findByBusId(Long busId);

    /**
     * Finds all bus locations within {@code radiusInMeters} metres of the given
     * (latitude, longitude) point using the PostGIS ST_DWithin function.
     */
    @Query(value = """
            SELECT * FROM bus_current_location
            WHERE ST_DWithin(
                location::geography,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusInMeters
            )
            """, nativeQuery = true) // Updated table name in native query
    List<BusLocation> findBusesWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusInMeters") double radiusInMeters);
}