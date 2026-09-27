package com.example.parkingsystem.controller;

import com.example.parkingsystem.dto.ParkVehicleRequest;
import com.example.parkingsystem.dto.ParkingSessionResponse;
import com.example.parkingsystem.service.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {
    private final ParkingService parkingService;

    /**
     * Эндпоинт для въезда автомобиля на парковку
     * POST /api/parking/park
     */
    @PostMapping("/park")
    public ResponseEntity<ParkingSessionResponse> parkVehicle(@Valid @RequestBody ParkVehicleRequest request) {
        ParkingSessionResponse response = parkingService.parkVehicle(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Эндпоинт для выезда автомобиля с парковки
     * POST /api/parking/unpark/{sessionId}
     */
    @PostMapping("/unpark/{sessionId}")
    public ResponseEntity<ParkingSessionResponse> unparkVehicle(@PathVariable Long sessionId) {
        ParkingSessionResponse response = parkingService.unparkVehicle(sessionId);
        return ResponseEntity.ok(response);
    }
}
