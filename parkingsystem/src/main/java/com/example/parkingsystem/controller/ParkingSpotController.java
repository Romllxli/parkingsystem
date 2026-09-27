package com.example.parkingsystem.controller;

import com.example.parkingsystem.model.ParkingSpot;
import com.example.parkingsystem.model.VehicleType;
import com.example.parkingsystem.repository.ParkingSpotRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotRepository spotRepository;

    // Получить все места
    @GetMapping
    public ResponseEntity<List<ParkingSpot>> getAllSpots() {
        return ResponseEntity.ok(spotRepository.findAll());
    }

    // Получить свободные места (с опциональным фильтром по типу: /api/spots/available?type=CAR)
    @GetMapping("/available")
    public ResponseEntity<List<ParkingSpot>> getAvailableSpots(@RequestParam(required = false) VehicleType type) {
        if (type != null) {
            return ResponseEntity.ok(spotRepository.findByIsOccupiedFalseAndAllowedType(type));
        }
        return ResponseEntity.ok(spotRepository.findByIsOccupiedFalse());
    }

    // Добавить новое парковочное место (Create)
    @PostMapping
    public ResponseEntity<ParkingSpot> createSpot(@Valid @RequestBody ParkingSpot spot) {
        return new ResponseEntity<>(spotRepository.save(spot), HttpStatus.CREATED);
    }

    // Удалить парковочное место (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        spotRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
