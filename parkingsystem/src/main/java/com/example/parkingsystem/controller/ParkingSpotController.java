package com.example.parkingsystem.controller;

import com.example.parkingsystem.model.ParkingSpot;
import com.example.parkingsystem.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotRepository spotRepository;

    /**
     * Эндпоинт для получения всех парковочных мест
     * GET /api/spots
     */
    @GetMapping
    public ResponseEntity<List<ParkingSpot>> getAllSpots() {
        return ResponseEntity.ok(spotRepository.findAll());
    }
}
