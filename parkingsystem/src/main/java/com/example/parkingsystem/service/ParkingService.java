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

    /**
     * БИЗНЕС-ЛОГИКА ВЪЕЗДА
     */
    @Transactional
    public ParkingSessionResponse parkVehicle(ParkVehicleRequest request) {
        // 1. Ищем или создаем автомобиль
        Car car = carRepository.findByLicensePlate(request.getLicensePlate())
                .orElseGet(() -> carRepository.save(Car.builder()
                        .licensePlate(request.getLicensePlate())
                        .vehicleType(request.getVehicleType())
                        .build()));

        // 2. Проверяем, не припаркован ли этот автомобиль прямо сейчас
        sessionRepository.findByCarIdAndStatus(car.getId(), SessionStatus.ACTIVE)
                .ifPresent(s -> {
                    throw new IllegalArgumentException("Автомобиль с таким номером уже находится на парковке!");
                });

        // 3. Находим парковочное место
        ParkingSpot spot = spotRepository.findById(request.getSpotId())
                .orElseThrow(() -> new IllegalArgumentException("Парковочное место с ID " + request.getSpotId() + " не найдено!"));

        // 4. Проверяем, свободно ли место
        if (spot.isOccupied()) {
            throw new IllegalArgumentException("Парковочное место " + spot.getSpotNumber() + " уже занято!");
        }

        // 5. Проверяем соответствие типа транспорта типу места
        if (!spot.getAllowedType().equals(request.getVehicleType())) {
            throw new IllegalArgumentException("Тип транспорта (" + request.getVehicleType() +
                    ") не соответствует разрешенному типу места (" + spot.getAllowedType() + ")!");
        }

        // 6. Занимаем место
        spot.setOccupied(true);
        spotRepository.save(spot);

        // 7. Создаем и сохраняем сессию парковки
        ParkingSession session = ParkingSession.builder()
                .car(car)
                .parkingSpot(spot)
                .entryTime(LocalDateTime.now())
                .status(SessionStatus.ACTIVE)
                .build();

        ParkingSession savedSession = sessionRepository.save(session);

        return mapToResponse(savedSession);
    }

    /**
     * БИЗНЕС-ЛОГИКА ВЫЕЗДА
     */
    @Transactional
    public ParkingSessionResponse unparkVehicle(Long sessionId) {
        // 1. Ищем сессию
        ParkingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Сессия с ID " + sessionId + " не найдена!"));

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new IllegalArgumentException("Эта сессия уже завершена!");
        }

        // 2. Фиксируем время выезда
        LocalDateTime exitTime = LocalDateTime.now();
        session.setExitTime(exitTime);

        // 3. Рассчитываем стоимость
        BigDecimal totalCost = calculateCost(session.getEntryTime(), exitTime);
        session.setTotalCost(totalCost);
        session.setStatus(SessionStatus.COMPLETED);

        // 4. Освобождаем парковочное место
        ParkingSpot spot = session.getParkingSpot();
        spot.setOccupied(false);
        spotRepository.save(spot);

        ParkingSession updatedSession = sessionRepository.save(session);

        return mapToResponse(updatedSession);
    }

    /**
     * Алгоритм расчета стоимости (20 сом/час с пропорциональным расчетом)
     */
    private BigDecimal calculateCost(LocalDateTime entry, LocalDateTime exit) {
        long minutes = Duration.between(entry, exit).toMinutes();
        if (minutes <= 0) {
            minutes = 1; // Минимальная сессия для тестов
        }

        // Вычисляем точно за количество минут: (минуты / 60) * 20 сом
        BigDecimal hours = new BigDecimal(minutes).divide(new BigDecimal("60"), 4, RoundingMode.HALF_UP);
        return hours.multiply(HOURLY_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Преобразование Entity в DTO Response
     */
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
