package ru.mirea.project.request.repository;

import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.model.Request;

import java.util.List;

public interface RequestRepository {
    Request save(Request request);
    Request findById(Long id);
    List<Request> findAll();
    Request update(Request request);
    void deleteById(Long id);

    List<Request> findByStatus(RequestStatus status);
    List<Request> findByUserId(Long userId);
    List<Request> findByCarId(Long carId);
    List<Request> searchByNotes(String notes);
    List<Request> findOverlapping(Long carId, java.time.LocalDate start, java.time.LocalDate end, Long excludeId);
    List<Request> sortByStartDate(boolean ascending);
    List<Request> sortByMonthlyPrice(boolean ascending);
}
