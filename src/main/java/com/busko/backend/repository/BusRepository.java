package com.busko.backend.repository;

import com.busko.backend.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {
    // No custom methods needed at the moment. Lookups will be handled by the default findById().
}