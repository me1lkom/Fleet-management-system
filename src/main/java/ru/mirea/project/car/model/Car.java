package ru.mirea.project.car.model;
import ru.mirea.project.car.enums.BodyType;
import ru.mirea.project.car.enums.CarStatus;
import ru.mirea.project.car.enums.Transmission;

import java.time.LocalDate;

public class Car {

    private Long id;
    private String brand;
    private String model;
    private Transmission transmission;
    private int year;
    private String licensePlate;
    private BodyType bodyType;
    private CarStatus status;
    private int mileage;

    public Car(
            Long id,
            String brand,
            String model,
            Transmission transmission,
            int year,
            String licensePlate,
            BodyType bodyType,
            CarStatus status,
            int mileage
    ) {
        this.id = id;
        setBrand(brand);
        setModel(model);
        setTransmission(transmission);
        setYear(year);
        setLicensePlate(licensePlate);
        setBodyType(bodyType);
        setStatus(status);
        setMileage(mileage);
    }

    public Long getId(){
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand){
        if (brand == null || brand.isBlank()){
            throw new IllegalArgumentException("Марка автомобиля не может быть пустой!");
        }
        this.brand = brand.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model){
        if (model == null || model.isBlank()){
            throw new IllegalArgumentException("Модель автомобиля не может быть пустой!");
        }
        this.model = model.trim();
    }

    public Transmission getTransmission(){
        return transmission;
    }

    public void setTransmission(Transmission transmission){
        if (transmission == null){
            throw new IllegalArgumentException("Тип коробки не может быть пустым!");
        }
        this.transmission = transmission;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year){
        int currentYear = LocalDate.now().getYear();
        if (year > currentYear || year < 1900){
            throw new IllegalArgumentException("Год введен неверно!");
        }

        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new IllegalArgumentException("Номер автомобиля не может быть пустой!");
        }
        this.licensePlate = licensePlate.trim();
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType){
        if (bodyType == null){
            throw new IllegalArgumentException("Тип кузова не может быть пустым!");
        }
        this.bodyType = bodyType;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        if (status == null){
            throw new IllegalArgumentException("Статус не может быть пустым!");
        }
        this.status = status;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage){
        if (mileage < 0){
            throw new IllegalArgumentException("Пробег не может быть отрицательным!");
        }
        this.mileage = mileage;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", transmission=" + transmission +
                ", year=" + year +
                ", licensePlate='" + licensePlate + '\'' +
                ", bodyType=" + bodyType +
                ", status=" + status +
                ", mileage=" + mileage +
                '}';
    }

}

