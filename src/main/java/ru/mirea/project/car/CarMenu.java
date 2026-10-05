package ru.mirea.project.car;

import ru.mirea.project.car.enums.BodyType;
import ru.mirea.project.car.enums.CarStatus;
import ru.mirea.project.car.enums.Transmission;
import ru.mirea.project.car.exception.CarNotFoundException;
import ru.mirea.project.car.exception.DataAccessException;
import ru.mirea.project.car.model.Car;
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.car.service.CarService;

import java.util.List;
import java.util.Scanner;

public class CarMenu {

    private final CarService service;
    private final Scanner scanner;

    public CarMenu(CarService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void run() {

        while (true) {

            System.out.println("\n========== АВТОМОБИЛИ ==========");
            System.out.println("1. Добавить автомобиль");
            System.out.println("2. Показать все автомобили");
            System.out.println("3. Найти автомобиль по ID");
            System.out.println("4. Найти по госномеру");
            System.out.println("5. Поиск по марке");
            System.out.println("6. Поиск по модели");
            System.out.println("7. Изменить автомобиль");
            System.out.println("8. Удалить автомобиль");
            System.out.println("9. Фильтрация");
            System.out.println("10. Сортировка");
            System.out.println("11. Экспорт в CVS");
            System.out.println("0. Вернуться в главное меню");

            System.out.print("\nВыберите действие: ");
            String choice = scanner.nextLine().trim();

            try {

                switch (choice) {

                    case "0":
                        System.out.println("Выход из меню автомобилей.");
                        return;
                    case "1":
                        createCar();
                        break;
                    case "2":
                        showAllCars();
                        break;
                    case "3":
                        findCarById();
                        break;
                    case "4":
                        findCarByLicensePlate();
                        break;
                    case "5":
                        findCarByBrand();
                        break;
                    case "6":
                        findCarByModel();
                        break;

                    case "7":
                        updateCar();
                        break;
                    case "8":
                        deleteCar();
                        break;
                    case "9":
                        filterCars();
                        break;
                    case "10":
                        sortCars();
                        break;
                    case "11":
                        exportCars();
                        break;

                    default:
                        System.out.println("Неизвестная команда!");
                }

            } catch (
                    IllegalArgumentException |
                    CarNotFoundException |
                    DataAccessException e
            ) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void showAllCars() {

        System.out.println("\n=== СПИСОК АВТОМОБИЛЕЙ ===");

        List<Car> cars = service.getAllCars();

        if (cars.isEmpty()) {
            System.out.println("Автомобили не найдены.");
            return;
        }

        for (Car car : cars) {
            System.out.println(car);
        }
    }

    private void createCar() {

        System.out.println("\n=== ДОБАВЛЕНИЕ АВТОМОБИЛЯ ===");

        System.out.print("Марка: ");
        String brand = scanner.nextLine().trim();

        System.out.print("Модель: ");
        String model = scanner.nextLine().trim();
        Transmission transmission = readTransmission();

        int year = readInt("Год: ");

        System.out.print("Госномер: ");
        String licensePlate = scanner.nextLine().trim();

        BodyType bodyType = readBodyType();

        int mileage = readInt("Пробег: ");

        CarStatus carStatus = readCarStatus();

        Car car = new Car(
                null,
                brand,
                model,
                transmission,
                year,
                licensePlate,
                bodyType,
                carStatus,
                mileage
        );

        Car savedCar = service.createCar(car);

        System.out.println("Автомобиль создан:");
        System.out.println(savedCar);
    }

    private void findCarById() {
        System.out.println("\n=== ПОИСК АВТО ПО ID ===");

        Long carId = readLong("Введите ID: ");

        Car findCar = service.getCarById(carId);

        if (findCar == null) {
            System.out.println("Автомобиль не найден.");
            return;
        }

        System.out.println(findCar);
    }

    private void findCarByLicensePlate() {
        System.out.println("\n=== ПОИСК АВТО ПО ГОСНОМЕРУ ===");

        System.out.println("Введите номер авто: ");
        String licensePlate = scanner.nextLine().trim();

        Car findCar = service.getCarByLicensePlate(licensePlate);

        if (findCar == null) {
            System.out.println("Автомобиль не найден.");
            return;
        }

        System.out.println(findCar);
    }

    private void findCarByBrand() {
        System.out.println("\n=== ПОИСК АВТО ПО МАРКЕ ===");

        System.out.println("Введите марку авто: ");
        String brand = scanner.nextLine().trim();

        List<Car> findCars = service.getCarsByBrand(brand);

        if (findCars.isEmpty()) {
            System.out.println("Автомобили не найдены.");
            return;
        }

        for (Car car : findCars) {
            System.out.println(car);
        }
    }

    private void findCarByModel() {
        System.out.println("\n=== ПОИСК АВТО ПО МОДЕЛИ ===");

        System.out.println("Введите модель авто: ");
        String model = scanner.nextLine().trim();

        List<Car> findCars = service.getCarsByModel(model);

        if (findCars.isEmpty()) {
            System.out.println("Автомобили не найдены.");
            return;
        }

        for (Car car : findCars) {
            System.out.println(car);
        }
    }

    private void deleteCar() {
        System.out.println("\n=== УДАЛЕНИЕ АВТО ПО ID ===");

        Long carId = readLong("Введите ID: ");

        service.deleteCarById(carId);

        System.out.println("Авто удалено");
    }

    private void filterCars() {
        while (true) {
            System.out.println("\n=== ФИЛЬТРАЦИЯ АВТО ===");
            System.out.println("1. Фильтрация по статусу");
            System.out.println("2. Фильтрация по типу кузова");
            System.out.println("3. Фильтрация по КПП");
            System.out.println("0. НАЗАД");

            System.out.print("\nВыберите действие: ");
            String choice = scanner.nextLine().trim();

            try {

                switch (choice) {

                    case "0":
                        System.out.println("Выход из меню фильтрации.");
                        return;
                    case "1":
                        filterCarsByStatus();
                        break;
                    case "2":
                        filterCarsByBodyType();
                        break;
                    case "3":
                        filterCarsByTransmission();
                        break;

                    default:
                        System.out.println("Неизвестная команда!");
                }

            } catch (
                    IllegalArgumentException |
                    CarNotFoundException |
                    DataAccessException e
            ) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void filterCarsByStatus() {
        System.out.println("\n=== ФИЛЬТРАЦИЯ ПО СТАТУСУ ===");

        CarStatus carStatus = readCarStatus();

        List<Car> cars = service.filterCarsByStatus(carStatus);

        if (cars.isEmpty()) {
            System.out.println("Автомобили не найдены.");
            return;
        }

        for (Car car : cars) {
            System.out.println(car);
        }
    }

    private void filterCarsByBodyType() {
        System.out.println("\n=== ФИЛЬТРАЦИЯ ПО ТИПУ КУЗОВА ===");

        BodyType bodyType = readBodyType();

        List<Car> cars = service.filterCarsByBodyType(bodyType);

        if (cars.isEmpty()) {
            System.out.println("Автомобили не найдены.");
            return;
        }

        for (Car car : cars) {
            System.out.println(car);
        }
    }

    private void filterCarsByTransmission() {
        System.out.println("\n=== ФИЛЬТРАЦИЯ ПО КПП ===");

        Transmission transmission = readTransmission();

        List<Car> cars = service.filterCarsByTransmission(transmission);

        if (cars.isEmpty()) {
            System.out.println("Автомобили не найдены.");
            return;
        }

        for (Car car : cars) {
            System.out.println(car);
        }
    }
    private void sortCars() {
        while (true) {

            System.out.println("\n=== СОРТИРОВКА АВТО ===");
            System.out.println("1. Сортировка по годам");
            System.out.println("2. Сортировка по пробегу");
            System.out.println("0. НАЗАД");

            System.out.print("\nВыберите действие: ");
            String choice = scanner.nextLine().trim();

            try {

                switch (choice) {

                    case "0":
                        System.out.println("Выход из меню сортировки.");
                        return;
                    case "1":
                        sortCarsByYear();
                        break;
                    case "2":
                        sortCarsByMileage();
                        break;

                    default:
                        System.out.println("Неизвестная команда!");
                }

            } catch (
                    IllegalArgumentException |
                    CarNotFoundException |
                    DataAccessException e
            ) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void sortCarsByYear() {
        System.out.println("\n=== СОРТИРОВКА АВТО ПО ГОДАМ ===");

        List<Car> findCars = service.sortCarsByYear();

        if (findCars.isEmpty()) {
            System.out.println("Автомобили не найдены.");
            return;
        }

        for (Car car : findCars) {
            System.out.println(car);
        }
    }

    private void sortCarsByMileage() {
        System.out.println("\n=== СОРТИРОВКА АВТО ПО ПРОБЕГУ ===");

        List<Car> findCars = service.sortCarsByMileage();

        if (findCars.isEmpty()) {
            System.out.println("Автомобили не найдены.");
            return;
        }

        for (Car car : findCars) {
            System.out.println(car);
        }
    }

    private void updateCar() {

        System.out.println("\n=== ИЗМЕНЕНИЕ АВТОМОБИЛЯ ===");

        Long carId = readLong("Введите ID: ");

        Car car = service.getCarById(carId);

        System.out.println("\nТекущие данные:");
        System.out.println(car);

        System.out.println("\nВведите новые данные.");
        System.out.println("Enter - оставить прежнее значение.");

        System.out.print("Марка [" + car.getBrand() + "]: ");
        String brand = scanner.nextLine().trim();
        if (!brand.isEmpty()) {
            car.setBrand(brand);
        }

        System.out.print("Модель [" + car.getModel() + "]: ");
        String model = scanner.nextLine().trim();
        if (!model.isEmpty()) {
            car.setModel(model);
        }

        System.out.println("КПП [" + car.getTransmission() + "]:");

        System.out.print("Выберите новое значение или Enter, чтобы оставить старое: ");
        System.out.println("1. AUTOMATIC");
        System.out.println("2. MANUAL");

        String transmissionChoice = scanner.nextLine().trim();

        if (!transmissionChoice.isEmpty()) {
            Transmission transmission;
            switch (transmissionChoice) {
                case "1":
                    transmission = Transmission.AUTOMATIC;
                    break;
                case "2":
                    transmission = Transmission.MANUAL;
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип КПП.");
            }

            car.setTransmission(transmission);
        }

        System.out.print("Год [" + car.getYear() + "]: ");
        String year = scanner.nextLine().trim();
        if (!year.isEmpty()) {
            car.setYear(Integer.parseInt(year));
        }

        System.out.print("Госномер [" + car.getLicensePlate() + "]: ");
        String licensePlate = scanner.nextLine().trim();
        if (!licensePlate.isEmpty()) {
            car.setLicensePlate(licensePlate);
        }

        System.out.println("Тип кузова [" + car.getBodyType() + "]:");

        System.out.print("Выберите новое значение или Enter, чтобы оставить старое: ");
        System.out.println("1. SEDAN");
        System.out.println("2. HATCHBACK");

        String bodyTypeChoice = scanner.nextLine().trim();

        if (!bodyTypeChoice.isEmpty()) {
            BodyType bodyType;
            switch (bodyTypeChoice) {
                case "1":
                    bodyType = BodyType.SEDAN;
                    break;
                case "2":
                    bodyType = BodyType.HATCHBACK;
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип кузова.");
            }

            car.setBodyType(bodyType);
        }

        System.out.println("Статус [" + car.getStatus() + "]:");

        System.out.print("Выберите новое значение или Enter, чтобы оставить старое: ");
        System.out.println("1. AVAILABLE");
        System.out.println("2. IN_USE");
        System.out.println("3. MAINTENANCE");


        String carStatusChoice = scanner.nextLine().trim();

        if (!carStatusChoice.isEmpty()) {
            CarStatus carStatus;
            switch (carStatusChoice) {
                case "1":
                    carStatus = CarStatus.AVAILABLE;
                    break;
                case "2":
                    carStatus = CarStatus.IN_USE;
                    break;
                case "3":
                    carStatus = CarStatus.MAINTENANCE;
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный статус.");
            }

            car.setStatus(carStatus);
        }


        System.out.print("Пробег [" + car.getMileage() + "]: ");
        String mileage = scanner.nextLine().trim();
        if (!mileage.isEmpty()) {
            car.setMileage(Integer.parseInt(mileage));
        }

        Car updatedCar = service.updateCar(car);

        System.out.println("\nАвтомобиль обновлён:");
        System.out.println(updatedCar);
    }

    private void exportCars() {
        System.out.println("\n=== ЭКСПОРТ АВТОМОБИЛЕЙ ===");

        service.exportCarsToCsv();

        System.out.println("Автомобили успешно экспортированы.");
        System.out.println("Файл: exports/cars.csv");
    }

    private int readInt(String message) {
        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private Transmission readTransmission() {
        while (true) {
            System.out.println("Выберите коробку передач:");
            System.out.println("1. AUTOMATIC");
            System.out.println("2. MANUAL");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return Transmission.AUTOMATIC;

                case "2":
                    return Transmission.MANUAL;

                default:
                    System.out.println("Неизвестный тип коробки передач. Попробуйте ещё раз.");
            }
        }
    }

    private BodyType readBodyType() {
        while (true) {
            System.out.println("Выберите тип кузова:");
            System.out.println("1. HATCHBACK");
            System.out.println("2. SEDAN");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return BodyType.HATCHBACK;

                case "2":
                    return BodyType.SEDAN;

                default:
                    System.out.println("Неизвестный тип кузова. Попробуйте ещё раз.");
            }
        }
    }

    private CarStatus readCarStatus() {
        while (true) {
            System.out.println("Выберите статус:");
            System.out.println("1. AVAILABLE");
            System.out.println("2. IN_USE");
            System.out.println("3. MAINTENANCE");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return CarStatus.AVAILABLE;

                case "2":
                    return CarStatus.IN_USE;

                case "3":
                    return CarStatus.MAINTENANCE;

                default:
                    System.out.println("Неизвестный статус. Попробуйте ещё раз.");
            }
        }
    }

    private Long readLong(String message) {
        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }
    }
}