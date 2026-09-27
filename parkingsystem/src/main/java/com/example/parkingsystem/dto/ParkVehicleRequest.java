package com.example.parkingsystem.dto;

import com.example.parkingsystem.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParkVehicleRequest {

    @NotBlank(message = "Номерной знак обязателен")
    private String licensePlate;

    @NotNull(message = "Тип транспорта обязателен")
    private VehicleType vehicleType;

    @NotNull(message = "ID парковочного места обязателен")
    private Long spotId;
}
