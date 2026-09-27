package com.example.parkingsystem.repository;

import com.example.parkingsystem.model.ParkingSession;
import com.example.parkingsystem.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    Optional<ParkingSession> findByCarIdAndStatus(Long carId, SessionStatus status);
}
