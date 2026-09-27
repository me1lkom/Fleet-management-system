package ru.mirea.project.car.repository;

import ru.mirea.project.car.enums.BodyType;
import ru.mirea.project.car.enums.CarStatus;
import ru.mirea.project.car.enums.Transmission;
import ru.mirea.project.car.model.Car;

import java.util.List;

public interface CarRepository {
    Car save(Car car);

    Car findById(Long id);
    Car searchByLicensePlate(String licensePlate);

    List<Car> findAll();
    List<Car> searchByBrand(String brand);
    List<Car> searchByModel(String model);

    List<Car> filterByStatus(CarStatus status);
    List<Car> filterByBodyType(BodyType bodyType);
    List<Car> filterByTransmission(Transmission transmission);
    List<Car> sortByYear();
    List<Car> sortByMileage();

    Car update(Car car);

    void deleteById(Long id);
}
