package com.busko.backend.repository;

import com.busko.backend.entity.Bus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {

    Optional<Bus> findByRegistrationNumber(String registrationNumber);
}
