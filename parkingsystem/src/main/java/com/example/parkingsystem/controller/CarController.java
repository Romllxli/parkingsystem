package com.example.parkingsystem.controller;

import com.example.parkingsystem.model.Car;
import com.example.parkingsystem.repository.CarRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarRepository carRepository;

    // Список всех машин
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carRepository.findAll());
    }

    // Поиск машины по ID
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return carRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Поиск машины по номеру (/api/cars/search?plate=01KG777AAA)
    @GetMapping("/search")
    public ResponseEntity<Car> getCarByPlate(@RequestParam String plate) {
        return carRepository.findByLicensePlate(plate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создание машины (Create)
    @PostMapping
    public ResponseEntity<Car> createCar(@Valid @RequestBody Car car) {
        return new ResponseEntity<>(carRepository.save(car), HttpStatus.CREATED);
    }

    // Удаление машины (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
