package ru.mirea.project.request.repository;

import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.exception.RequestDataAccessException;
import ru.mirea.project.request.model.Request;
import ru.mirea.project.util.DatabaseManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcRequestRepository implements RequestRepository {

    private Request map(ResultSet rs) throws SQLException {
        return new Request(
                rs.getLong("request_id"),
                rs.getLong("user_id"),
                rs.getLong("car_id"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getInt("monthly_price"),
                RequestStatus.valueOf(rs.getString("status")),
                rs.getString("notes")
        );
    }

    @Override
    public Request save(Request request) {
        String sql = "INSERT INTO requests (user_id, car_id, start_date, end_date, monthly_price, status, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING request_id";
        try (Connection c = DatabaseManager.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, request);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Request(rs.getLong("request_id"), request.getUserId(), request.getCarId(),
                            request.getStartDate(), request.getEndDate(), request.getMonthlyPrice(),
                            request.getStatus(), request.getNotes());
                }
                throw new RequestDataAccessException("Не удалось сохранить заявку на аренду.");
            }
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка сохранения заявки на аренду.", e);
        }
    }

    @Override
    public Request findById(Long id) {
        return queryOne("SELECT * FROM requests WHERE request_id = ?", id);
    }

    @Override
    public List<Request> findAll() {
        return query("SELECT * FROM requests ORDER BY request_id");
    }

    @Override
    public Request update(Request request) {
        String sql = "UPDATE requests SET user_id = ?, car_id = ?, start_date = ?, end_date = ?, "
                + "monthly_price = ?, status = ?, notes = ? WHERE request_id = ?";
        try (Connection c = DatabaseManager.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, request);
            ps.setLong(8, request.getId());
            if (ps.executeUpdate() == 0) return null;
            return request;
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка обновления заявки на аренду.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection c = DatabaseManager.openConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM requests WHERE request_id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка удаления заявки на аренду.", e);
        }
    }

    @Override
    public List<Request> findByStatus(RequestStatus status) {
        return query("SELECT * FROM requests WHERE status = ?", status.name());
    }

    @Override
    public List<Request> findByUserId(Long userId) {
        return query("SELECT * FROM requests WHERE user_id = ?", userId);
    }

    @Override
    public List<Request> findByCarId(Long carId) {
        return query("SELECT * FROM requests WHERE car_id = ?", carId);
    }

    @Override
    public List<Request> searchByNotes(String notes) {
        return query("SELECT * FROM requests WHERE LOWER(COALESCE(notes, '')) LIKE LOWER(?)", "%" + notes + "%");
    }

    @Override
    public List<Request> findOverlapping(Long carId, LocalDate start, LocalDate end, Long excludeId) {
        String sql = "SELECT * FROM requests WHERE car_id = ? AND status IN ('APPROVED', 'ACTIVE') "
                + "AND start_date < ? AND end_date > ? AND (? IS NULL OR request_id <> ?)";
        List<Request> result = new ArrayList<>();
        try (Connection c = DatabaseManager.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, carId);
            ps.setDate(2, Date.valueOf(end));
            ps.setDate(3, Date.valueOf(start));
            if (excludeId == null) ps.setNull(4, java.sql.Types.BIGINT); else ps.setLong(4, excludeId);
            if (excludeId == null) ps.setNull(5, java.sql.Types.BIGINT); else ps.setLong(5, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка проверки пересечения аренды.", e);
        }
    }

    @Override
    public List<Request> sortByStartDate(boolean ascending) {
        return query("SELECT * FROM requests ORDER BY start_date " + (ascending ? "ASC" : "DESC"));
    }

    @Override
    public List<Request> sortByMonthlyPrice(boolean ascending) {
        return query("SELECT * FROM requests ORDER BY monthly_price " + (ascending ? "ASC" : "DESC"));
    }

    private void fill(PreparedStatement ps, Request request) throws SQLException {
        ps.setLong(1, request.getUserId());
        ps.setLong(2, request.getCarId());
        ps.setDate(3, Date.valueOf(request.getStartDate()));
        ps.setDate(4, Date.valueOf(request.getEndDate()));
        ps.setInt(5, request.getMonthlyPrice());
        ps.setString(6, request.getStatus().name());
        ps.setString(7, request.getNotes());
    }

    private Request queryOne(String sql, Long id) {
        try (Connection c = DatabaseManager.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка поиска заявки.", e);
        }
    }

    private List<Request> query(String sql) {
        return query(sql, (Object) null);
    }

    private List<Request> query(String sql, Object param) {
        List<Request> result = new ArrayList<>();
        try (Connection c = DatabaseManager.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (param instanceof String) ps.setString(1, (String) param);
            else if (param instanceof Long) ps.setLong(1, (Long) param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RequestDataAccessException("Ошибка выборки заявок.", e);
        }
    }
}
