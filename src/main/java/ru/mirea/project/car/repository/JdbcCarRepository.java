package ru.mirea.project.car.repository;

import ru.mirea.project.car.enums.BodyType;
import ru.mirea.project.car.enums.CarStatus;
import ru.mirea.project.car.enums.Transmission;
import ru.mirea.project.car.exception.CarNotFoundException;
import ru.mirea.project.car.exception.DataAccessException;
import ru.mirea.project.car.model.Car;
import ru.mirea.project.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcCarRepository implements CarRepository {

    private Car mapCar(ResultSet resultSet) throws SQLException {
        return new Car(
                resultSet.getLong("car_id"),
                resultSet.getString("brand"),
                resultSet.getString("model"),
                Transmission.valueOf(resultSet.getString("transmission")),
                resultSet.getInt("year"),
                resultSet.getString("license_plate"),
                BodyType.valueOf(resultSet.getString("body_type")),
                CarStatus.valueOf(resultSet.getString("status")),
                resultSet.getInt("mileage")
        );
    }

    @Override
    public Car save(Car car) {

        String sql = "INSERT INTO cars" +
                "(brand, model, transmission, year, license_plate, body_type, status, mileage)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)" +
                "RETURNING car_id";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, car.getBrand());
            statement.setString(2, car.getModel());
            statement.setString(3, car.getTransmission().name());
            statement.setInt(4, car.getYear());
            statement.setString(5, car.getLicensePlate());
            statement.setString(6, car.getBodyType().name());
            statement.setString(7, car.getStatus().name());
            statement.setInt(8, car.getMileage());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("car_id");

                    return new Car(
                            id,
                            car.getBrand(),
                            car.getModel(),
                            car.getTransmission(),
                            car.getYear(),
                            car.getLicensePlate(),
                            car.getBodyType(),
                            car.getStatus(),
                            car.getMileage()
                    );
                }
            }

            throw new DataAccessException("Не получилось получить id созданного автомобиля.");
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка при сохранении автомобиля.",
                    e
            );
        }
    }

    @Override
    public Car findById(Long id) {

        String sql = "SELECT * FROM cars " +
                "WHERE car_id = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCar(resultSet);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка получение автомобил по ID.",
                    e
            );
        }
    }

    @Override
    public Car searchByLicensePlate(String licensePlate) {

        String sql = "SELECT * FROM cars " +
                "WHERE license_plate = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, licensePlate);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCar(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка получения автомобиля по номеру.",
                    e
            );
        }
    }

    @Override
    public List<Car> findAll() {

        String sql = "SELECT * FROM cars";

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                cars.add(mapCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка при получении всех автомобилей: ",
                    e
            );
        }
    }

    @Override
    public List<Car> searchByBrand(String brand) {

        String sql = "SELECT * FROM cars " +
                "WHERE brand = ?";

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, brand);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cars.add(mapCar(resultSet));
                }
                return cars;
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка получения автомобилей по бренду.",
                    e
            );
        }
    }

    @Override
    public List<Car> searchByModel(String model) {

        String sql = "SELECT * FROM cars " +
                "WHERE model = ?";

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, model);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cars.add(mapCar(resultSet));
                }
                return cars;
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка получения автомобилей по модели.",
                    e
            );
        }
    }

    @Override
    public List<Car> filterByStatus(CarStatus status) {

        String sql = "SELECT * FROM cars " +
                "WHERE status = ?";

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, status.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cars.add(mapCar(resultSet));
                }
                return cars;
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка получения автомобилей по статусу.",
                    e
            );
        }
    }

    @Override
    public List<Car> filterByBodyType(BodyType bodyType) {

        String sql = "SELECT * FROM cars " +
                "WHERE body_type = ?";

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, bodyType.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cars.add(mapCar(resultSet));
                }
                return cars;
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка получения автомобилей по типу кузова.",
                    e
            );
        }
    }

    @Override
    public List<Car> filterByTransmission(Transmission transmission) {

        String sql = "SELECT * FROM cars " +
                "WHERE transmission = ?";

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, transmission.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cars.add(mapCar(resultSet));
                }
                return cars;
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка получения автомобилей по коробке.",
                    e
            );
        }
    }

    @Override
    public List<Car> sortByYear() {

        String sql = "SELECT * FROM cars " +
                "ORDER BY year";

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                cars.add(mapCar(resultSet));
            }
            return cars;

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка получения автомобилей с сортировкой по году.",
                    e
            );
        }
    }

    @Override
    public List<Car> sortByMileage() {

        String sql = "SELECT * FROM cars " +
                "ORDER BY mileage";

        List<Car> cars = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                cars.add(mapCar(resultSet));
            }
            return cars;

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка получения автомобилей с сортировкой по пробегу.",
                    e
            );
        }
    }

    @Override
    public Car update(Car car) {

        String sql = "UPDATE cars SET " +
                "brand = ?, " +
                "model = ?, " +
                "transmission = ?, " +
                "year = ?, " +
                "license_plate = ?, " +
                "body_type = ?," +
                "status = ?, " +
                "mileage = ? " +
                "WHERE car_id = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, car.getBrand());
            statement.setString(2, car.getModel());
            statement.setString(3, car.getTransmission().name());
            statement.setInt(4, car.getYear());
            statement.setString(5, car.getLicensePlate());
            statement.setString(6, car.getBodyType().name());
            statement.setString(7, car.getStatus().name());
            statement.setInt(8, car.getMileage());

            statement.setLong(9, car.getId());

            int answerDB = statement.executeUpdate();

            if (answerDB == 0) {
                throw new CarNotFoundException(
                        "Автомобиль с ID " + car.getId() + " не найден."
                );
            }

            return car;
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка обновления автомобил.",
                    e
            );
        }
    }

    @Override
    public void deleteById(Long id) {

        String sql = "DELETE FROM cars " +
                "WHERE car_id = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, id);
            int answerDB = statement.executeUpdate();

            if (answerDB == 0) {
                throw new CarNotFoundException(
                        "Автомобиль с ID " + id + " не найден."
                );
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Ошибка удаление автомобил по ID.",
                    e
            );
        }
    }
}
