package com.example.parkingsystem.dto;

import com.example.parkingsystem.model.SessionStatus;
import com.example.parkingsystem.model.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ParkingSessionResponse {
    private Long sessionId;
    private String licensePlate;
    private VehicleType vehicleType;
    private String spotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal totalCost;
    private SessionStatus status;
}
