package ru.mirea.project.request.repository;

import ru.mirea.project.request.enums.RequestPriority;
import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.exception.RequestDataAccessException;
import ru.mirea.project.request.model.Request;
import ru.mirea.project.util.DatabaseManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcRequestRepository implements RequestRepository {

    private Request mapRequest(ResultSet resultSet) throws SQLException {
        return new Request(
                resultSet.getLong("request_id"),
                resultSet.getLong("user_id"),
                resultSet.getLong("car_id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                RequestStatus.valueOf(resultSet.getString("status")),
                RequestPriority.valueOf(resultSet.getString("priority")),
                resultSet.getDate("created_at").toLocalDate()
        );
    }

    @Override
    public Request save(Request request) {
        String sql = "INSERT INTO requests " +
                "(user_id, car_id, title, description, status, priority, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING request_id";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, request.getUserId());
            statement.setLong(2, request.getCarId());
            statement.setString(3, request.getTitle());
            statement.setString(4, request.getDescription());
            statement.setString(5, request.getStatus().name());
            statement.setString(6, request.getPriority().name());
            statement.setDate(7, Date.valueOf(request.getCreatedAt()));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Request(
                            resultSet.getLong("request_id"),
                            request.getUserId(),
                            request.getCarId(),
                            request.getTitle(),
                            request.getDescription(),
                            request.getStatus(),
                            request.getPriority(),
                            request.getCreatedAt()
                    );
                }
                throw new RequestDataAccessException("Не удалось сохранить заявку.");
            }
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка сохранения заявки.", e);
        }
    }

    @Override
    public Request findById(Long id) {
        String sql = "SELECT * FROM requests WHERE request_id = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRequest(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка поиска заявки по ID.", e);
        }
    }

    @Override
    public List<Request> findAll() {
        String sql = "SELECT * FROM requests ORDER BY request_id";
        List<Request> requests = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                requests.add(mapRequest(resultSet));
            }
            return requests;
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка получения списка заявок.", e);
        }
    }

    @Override
    public Request update(Request request) {
        String sql = "UPDATE requests SET " +
                "user_id = ?, car_id = ?, title = ?, description = ?, " +
                "status = ?, priority = ?, created_at = ? " +
                "WHERE request_id = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, request.getUserId());
            statement.setLong(2, request.getCarId());
            statement.setString(3, request.getTitle());
            statement.setString(4, request.getDescription());
            statement.setString(5, request.getStatus().name());
            statement.setString(6, request.getPriority().name());
            statement.setDate(7, Date.valueOf(request.getCreatedAt()));
            statement.setLong(8, request.getId());

            int updated = statement.executeUpdate();
            if (updated == 0) {
                return null;
            }
            return request;
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка обновления заявки.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM requests WHERE request_id = ?";

        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка удаления заявки.", e);
        }
    }

    @Override
    public List<Request> findByStatus(RequestStatus status) {
        return queryByString(
                "SELECT * FROM requests WHERE status = ?",
                status.name(),
                "Ошибка фильтрации заявок по статусу."
        );
    }

    @Override
    public List<Request> findByPriority(RequestPriority priority) {
        return queryByString(
                "SELECT * FROM requests WHERE priority = ?",
                priority.name(),
                "Ошибка фильтрации заявок по приоритету."
        );
    }

    @Override
    public List<Request> searchByTitle(String title) {
        return queryByString(
                "SELECT * FROM requests WHERE LOWER(title) LIKE LOWER(?)",
                "%" + title + "%",
                "Ошибка поиска заявок по названию."
        );
    }

    @Override
    public List<Request> searchByDescription(String description) {
        return queryByString(
                "SELECT * FROM requests WHERE LOWER(description) LIKE LOWER(?)",
                "%" + description + "%",
                "Ошибка поиска заявок по описанию."
        );
    }

    @Override
    public List<Request> findByUserId(Long userId) {
        return queryByLong(
                "SELECT * FROM requests WHERE user_id = ?",
                userId,
                "Ошибка поиска заявок по пользователю."
        );
    }

    @Override
    public List<Request> findByCarId(Long carId) {
        return queryByLong(
                "SELECT * FROM requests WHERE car_id = ?",
                carId,
                "Ошибка поиска заявок по автомобилю."
        );
    }

    @Override
    public List<Request> sortByCreatedAt() {
        return queryNoParams(
                "SELECT * FROM requests ORDER BY created_at DESC, request_id",
                "Ошибка сортировки заявок по дате."
        );
    }

    @Override
    public List<Request> sortByPriority() {
        String sql = "SELECT * FROM requests ORDER BY " +
                "CASE priority " +
                "WHEN 'HIGH' THEN 1 " +
                "WHEN 'MEDIUM' THEN 2 " +
                "WHEN 'LOW' THEN 3 " +
                "ELSE 4 END, request_id";
        return queryNoParams(sql, "Ошибка сортировки заявок по приоритету.");
    }

    private List<Request> queryByString(String sql, String value, String error) {
        List<Request> requests = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapRequest(resultSet));
                }
                return requests;
            }
        } catch (SQLException e) {
            throw new RequestDataAccessException(error, e);
        }
    }

    private List<Request> queryByLong(String sql, Long value, String error) {
        List<Request> requests = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapRequest(resultSet));
                }
                return requests;
            }
        } catch (SQLException e) {
            throw new RequestDataAccessException(error, e);
        }
    }

    private List<Request> queryNoParams(String sql, String error) {
        List<Request> requests = new ArrayList<>();
        try (
                Connection connection = DatabaseManager.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                requests.add(mapRequest(resultSet));
            }
            return requests;
        } catch (SQLException e) {
            throw new RequestDataAccessException(error, e);
        }
    }
}
