package com.busko.backend.controller;

import com.busko.backend.entity.Route;
import com.busko.backend.repository.RouteRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteRepository routeRepository;

    public RouteController(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Route>> searchRoutes(@RequestParam String query) {
        List<Route> routes = routeRepository.findByRouteNumberContainingIgnoreCase(query);
        return ResponseEntity.ok(routes);
    }
}