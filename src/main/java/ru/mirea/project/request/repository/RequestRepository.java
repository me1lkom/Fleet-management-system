package ru.mirea.project.request.repository;

import ru.mirea.project.request.enums.RequestPriority;
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

    List<Request> findByPriority(RequestPriority priority);

    List<Request> searchByTitle(String title);

    List<Request> searchByDescription(String description);

    List<Request> findByUserId(Long userId);

    List<Request> findByCarId(Long carId);

    List<Request> sortByCreatedAt();

    List<Request> sortByPriority();
}
