package com.example.parkingsystem.service;

import com.example.parkingsystem.dto.ParkVehicleRequest;
import com.example.parkingsystem.dto.ParkingSessionResponse;
import com.example.parkingsystem.model.*;
import com.example.parkingsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final CarRepository carRepository;
    private final ParkingSpotRepository spotRepository;
    private final ParkingSessionRepository sessionRepository;

    private static final BigDecimal HOURLY_RATE = new BigDecimal("20.00");


    @Transactional
    public ParkingSessionResponse parkVehicle(ParkVehicleRequest request) {

        Car car = carRepository.findByLicensePlate(request.getLicensePlate())
                .orElseGet(() -> carRepository.save(Car.builder()
                        .licensePlate(request.getLicensePlate())
                        .vehicleType(request.getVehicleType())
                        .build()));


        sessionRepository.findByCarIdAndStatus(car.getId(), SessionStatus.ACTIVE)
                .ifPresent(s -> {
                    throw new IllegalArgumentException("Автомобиль с таким номером уже находится на парковке!");
                });


        ParkingSpot spot = spotRepository.findById(request.getSpotId())
                .orElseThrow(() -> new IllegalArgumentException("Парковочное место с ID " + request.getSpotId() + " не найдено!"));


        if (spot.isOccupied()) {
            throw new IllegalArgumentException("Парковочное место " + spot.getSpotNumber() + " уже занято!");
        }


        if (!spot.getAllowedType().equals(request.getVehicleType())) {
            throw new IllegalArgumentException("Тип транспорта (" + request.getVehicleType() +
                    ") не соответствует разрешенному типу места (" + spot.getAllowedType() + ")!");
        }


        spot.setOccupied(true);
        spotRepository.save(spot);


        ParkingSession session = ParkingSession.builder()
                .car(car)
                .parkingSpot(spot)
                .entryTime(LocalDateTime.now())
                .status(SessionStatus.ACTIVE)
                .build();

        ParkingSession savedSession = sessionRepository.save(session);

        return mapToResponse(savedSession);
    }


    @Transactional
    public ParkingSessionResponse unparkVehicle(Long sessionId) {

        ParkingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Сессия с ID " + sessionId + " не найдена!"));

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new IllegalArgumentException("Эта сессия уже завершена!");
        }


        LocalDateTime exitTime = LocalDateTime.now();
        session.setExitTime(exitTime);


        BigDecimal totalCost = calculateCost(session.getEntryTime(), exitTime);
        session.setTotalCost(totalCost);
        session.setStatus(SessionStatus.COMPLETED);


        ParkingSpot spot = session.getParkingSpot();
        spot.setOccupied(false);
        spotRepository.save(spot);

        ParkingSession updatedSession = sessionRepository.save(session);

        return mapToResponse(updatedSession);
    }


    private BigDecimal calculateCost(LocalDateTime entry, LocalDateTime exit) {
        long minutes = Duration.between(entry, exit).toMinutes();
        if (minutes <= 0) {
            minutes = 1;
        }


        BigDecimal hours = new BigDecimal(minutes).divide(new BigDecimal("60"), 4, RoundingMode.HALF_UP);
        return hours.multiply(HOURLY_RATE).setScale(2, RoundingMode.HALF_UP);
    }


    private ParkingSessionResponse mapToResponse(ParkingSession session) {
        return ParkingSessionResponse.builder()
                .sessionId(session.getId())
                .licensePlate(session.getCar().getLicensePlate())
                .vehicleType(session.getCar().getVehicleType())
                .spotNumber(session.getParkingSpot().getSpotNumber())
                .entryTime(session.getEntryTime())
                .exitTime(session.getExitTime())
                .totalCost(session.getTotalCost())
                .status(session.getStatus())
                .build();
    }
}
