package com.busko.backend.repository;

import com.busko.backend.entity.BusLocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {

    /**
     * Finds all bus locations within {@code radiusInMeters} metres of the given
     * (latitude, longitude) point using the PostGIS ST_DWithin function.
     *
     * <p>Coordinates are cast to the {@code geography} type so that the radius is
     * interpreted in metres (rather than degrees).
     */
    @Query(value = """
            SELECT * FROM bus_locations
            WHERE ST_DWithin(
                location::geography,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :radiusInMeters
            )
            """, nativeQuery = true)
    List<BusLocation> findBusesWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusInMeters") double radiusInMeters);
}
