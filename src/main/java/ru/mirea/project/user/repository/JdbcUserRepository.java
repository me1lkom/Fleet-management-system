
package ru.mirea.project.user.repository;

import ru.mirea.project.user.model.User;
import ru.mirea.project.user.exception.UserNotFoundException;
import ru.mirea.project.user.exception.UserDataAccessException;
import ru.mirea.project.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserRepository implements UserRepository {

    // Преобразование строки из PostgreSQL в объект User
    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("user_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("phone"),
                resultSet.getString("email"),
                resultSet.getString("driver_license")
        );
    }

    // Обработка ошибок базы данных
    private UserDataAccessException databaseError(
            String message, SQLException e) {

        if ("23505".equals(e.getSQLState())) {
            return new UserDataAccessException(
                    "Пользователь с таким телефоном, email " +
                            "или водительским удостоверением уже существует.",
                    e
            );
        }

        if ("23503".equals(e.getSQLState())) {
            return new UserDataAccessException(
                    "Операция запрещена: пользователь связан с заявками.",
                    e
            );
        }

        return new UserDataAccessException(message, e);
    }

    // Добавление пользователя
    @Override
    public User save(User user) {

        String sql = "INSERT INTO users " +
                "(first_name, last_name, phone, email, driver_license) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "RETURNING user_id";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getDriverLicense());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    Long id = resultSet.getLong("user_id");

                    return new User(
                            id,
                            user.getFirstName(),
                            user.getLastName(),
                            user.getPhone(),
                            user.getEmail(),
                            user.getDriverLicense()
                    );
                }
            }

            throw new UserDataAccessException(
                    "Не удалось получить ID созданного пользователя."
            );

        } catch (SQLException e) {
            throw databaseError("Ошибка сохранения пользователя.", e);
        }
    }

    // Поиск пользователя по ID
    @Override
    public User findById(Long id) {

        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw databaseError("Ошибка поиска пользователя по ID.", e);
        }
    }

    // Поиск по телефону
    @Override
    public User findByPhone(String phone) {

        String sql = "SELECT * FROM users WHERE phone = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, phone);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw databaseError("Ошибка поиска пользователя по телефону.", e);
        }
    }

    // Поиск по электронной почте
    @Override
    public User findByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw databaseError("Ошибка поиска пользователя по email.", e);
        }
    }

    // Получение всех пользователей
    @Override
    public List<User> findAll() {

        String sql = "SELECT * FROM users ORDER BY user_id";

        List<User> users = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }

            return users;

        } catch (SQLException e) {
            throw databaseError("Ошибка получения списка пользователей.", e);
        }
    }

    // Поиск по имени или фамилии
    @Override
    public List<User> searchByName(String name) {

        String sql = "SELECT * FROM users " +
                "WHERE first_name ILIKE ? OR last_name ILIKE ?";

        List<User> users = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            String search = "%" + name + "%";

            statement.setString(1, search);
            statement.setString(2, search);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    users.add(mapUser(resultSet));
                }
            }

            return users;

        } catch (SQLException e) {
            throw databaseError("Ошибка поиска пользователей по имени.", e);
        }
    }

    // Сортировка по фамилии
    @Override
    public List<User> sortByLastName() {

        String sql = "SELECT * FROM users " +
                "ORDER BY last_name, first_name, user_id";

        List<User> users = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }

            return users;

        } catch (SQLException e) {
            throw databaseError("Ошибка сортировки пользователей.", e);
        }
    }

    // Изменение пользователя
    @Override
    public User update(User user) {

        String sql = "UPDATE users SET " +
                "first_name = ?, " +
                "last_name = ?, " +
                "phone = ?, " +
                "email = ?, " +
                "driver_license = ? " +
                "WHERE user_id = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getDriverLicense());
            statement.setLong(6, user.getId());

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new UserNotFoundException(
                        "Пользователь с ID " + user.getId() + " не найден."
                );
            }

            return user;

        } catch (SQLException e) {
            throw databaseError("Ошибка обновления пользователя.", e);
        }
    }

    // Удаление пользователя
    @Override
    public void deleteById(Long id) {

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new UserNotFoundException(
                        "Пользователь с ID " + id + " не найден."
                );
            }

        } catch (SQLException e) {
            throw databaseError("Ошибка удаления пользователя.", e);
        }
    }
}
