package com.busko.backend.controller;

import com.busko.backend.entity.Bus;
import com.busko.backend.entity.Route;
import com.busko.backend.repository.BusRepository;
import com.busko.backend.repository.RouteRepository;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public BusController(BusRepository busRepository, RouteRepository routeRepository) {
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    @PostMapping("/start")
    public ResponseEntity<Bus> startShift(@RequestParam Long routeId) {
        Optional<Route> routeOpt = routeRepository.findById(routeId);

        if (routeOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Create a new bus and attach the selected route
        Bus newBus = new Bus();
        newBus.setRoute(routeOpt.get());

        Bus savedBus = busRepository.save(newBus);
        return ResponseEntity.ok(savedBus);
    }
}