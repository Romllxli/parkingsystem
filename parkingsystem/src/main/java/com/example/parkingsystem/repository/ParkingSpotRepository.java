package com.example.parkingsystem.repository;

import com.example.parkingsystem.model.ParkingSpot;
import com.example.parkingsystem.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    List<ParkingSpot> findByIsOccupiedFalseAndAllowedType(VehicleType allowedType);

    List<ParkingSpot> findByIsOccupiedFalse();
}
