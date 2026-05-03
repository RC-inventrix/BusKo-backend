package com.busko.backend.repository;

import com.busko.backend.entity.Route;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
    // Finds any route containing the typed characters (e.g., typing "13" finds "138" and "120")
    List<Route> findByRouteNumberContainingIgnoreCase(String routeNumber);
}