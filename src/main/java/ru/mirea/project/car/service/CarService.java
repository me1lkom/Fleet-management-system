package ru.mirea.project.car.service;

import ru.mirea.project.car.enums.BodyType;
import ru.mirea.project.car.enums.CarStatus;
import ru.mirea.project.car.enums.Transmission;
import ru.mirea.project.car.exception.CarNotFoundException;
import ru.mirea.project.car.exception.DuplicateLicensePlateException;
import ru.mirea.project.car.model.Car;
import ru.mirea.project.car.repository.CarRepository;

import java.util.List;

public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car createCar(Car car) {

        Car existingCar = carRepository.searchByLicensePlate(car.getLicensePlate());

        if (existingCar != null) {
            throw new DuplicateLicensePlateException(
                    "Автомобиль с таким номером уже зарегистрирован."
            );
        }

        return carRepository.save(car);
    }

    public Car getCarById(Long id) {
        Car existCar = carRepository.findById(id);

        if (existCar == null) {
            throw new CarNotFoundException(
                    "Автомобиль с ID " + id + " не найден."
            );
        }

        return existCar;
    }

    public Car getCarByLicensePlate(String licensePlate) {
        Car existCar = carRepository.searchByLicensePlate(licensePlate);

        if (existCar == null) {
            throw new DuplicateLicensePlateException(
                    "Автомобиль с номером " + licensePlate + " не найден."
            );
        }

        return existCar;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> getCarsByBrand(String brand) {
        return carRepository.searchByBrand(brand);
    }

    public List<Car> getCarsByModel(String model) {
        return carRepository.searchByModel(model);
    }

    public List<Car> filterCarsByStatus(CarStatus status) {
        return carRepository.filterByStatus(status);
    }

    public List<Car> filterCarsByBodyType(BodyType bodyType) {
        return carRepository.filterByBodyType(bodyType);
    }

    public List<Car> filterCarsByTransmission(Transmission transmission) {
        return carRepository.filterByTransmission(transmission);
    }

    public List<Car> sortCarsByYear() {
        return carRepository.sortByYear();
    }

    public List<Car> sortCarsByMileage() {
        return carRepository.sortByMileage();
    }


    public Car updateCar(Car car) {

        if (car.getId() == null) {
            throw new IllegalArgumentException(
                    "Для обновления автомобиля необходим его ID."
            );
        }

        Car currentCar = carRepository.findById(car.getId());
        if (currentCar == null) {
            throw new CarNotFoundException(
                    "Автомобиля с таким ID " + car.getId() + " не существует"
            );
        }

        if (car.getMileage() < currentCar.getMileage()) {
            throw new IllegalArgumentException(
                    "Нельзя уменьшить пробег автомобиля."
            );
        }

        if(!car.getLicensePlate().equals(currentCar.getLicensePlate())) {
            Car existCar = carRepository.searchByLicensePlate(car.getLicensePlate());

            if (existCar != null) {
                throw new DuplicateLicensePlateException(
                        "Автомобиля с таким номером " + car.getLicensePlate() + " уже существует."
                );
            }
        }

        return carRepository.update(car);
    }

    public void deleteCarById(Long id) {
        Car existCar = carRepository.findById(id);

        if (existCar == null) {
            throw new CarNotFoundException(
                    "Автомобиль с ID " + id + " не найден."
            );
        }

        carRepository.deleteById(id);
    }
}
