package ru.mirea.project.request.service;

import ru.mirea.project.car.exception.CarNotFoundException;
import ru.mirea.project.car.model.Car;
import ru.mirea.project.car.repository.CarRepository;
import ru.mirea.project.request.enums.RequestPriority;
import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.exception.InvalidRequestException;
import ru.mirea.project.request.exception.InvalidStatusChangeException;
import ru.mirea.project.request.exception.RequestNotFoundException;
import ru.mirea.project.request.model.Request;
import ru.mirea.project.request.repository.RequestRepository;
import ru.mirea.project.user.exception.UserNotFoundException;
import ru.mirea.project.user.model.User;
import ru.mirea.project.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public RequestService(
            RequestRepository requestRepository,
            UserRepository userRepository,
            CarRepository carRepository
    ) {
        this.requestRepository = Objects.requireNonNull(requestRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.carRepository = Objects.requireNonNull(carRepository);
    }

    public Request createRequest(
            Long userId,
            Long carId,
            String title,
            String description,
            RequestPriority priority
    ) {
        validateRelatedEntities(userId, carId);

        if (description == null || description.trim().length() < 5) {
            throw new InvalidRequestException("Описание слишком короткое! Минимум 5 символов.");
        }

        Request request = new Request(
                null,
                userId,
                carId,
                title,
                description,
                RequestStatus.NEW,
                priority != null ? priority : RequestPriority.MEDIUM,
                LocalDate.now()
        );

        return requestRepository.save(request);
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Request getRequestById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Некорректный ID заявки!");
        }

        Request request = requestRepository.findById(id);
        if (request == null) {
            throw new RequestNotFoundException(id);
        }
        return request;
    }

    public Request updateRequest(Request request) {
        getRequestById(request.getId());
        validateRelatedEntities(request.getUserId(), request.getCarId());

        if (request.getDescription().length() < 5) {
            throw new InvalidRequestException("Описание слишком короткое! Минимум 5 символов.");
        }

        return requestRepository.update(request);
    }

    public void deleteRequest(Long id) {
        getRequestById(id);
        requestRepository.deleteById(id);
    }

    public Request changeStatus(Long id, RequestStatus newStatus) {
        Request request = getRequestById(id);

        if (newStatus == null) {
            throw new InvalidRequestException("Новый статус не может быть пустым!");
        }

        RequestStatus current = request.getStatus();

        if (current == RequestStatus.CANCELLED && newStatus == RequestStatus.COMPLETED) {
            throw new InvalidStatusChangeException("Отмененную заявку нельзя завершить!");
        }

        if (current == RequestStatus.COMPLETED && newStatus == RequestStatus.NEW) {
            throw new InvalidStatusChangeException("Завершенную заявку нельзя вернуть в NEW!");
        }

        if (current == RequestStatus.CANCELLED && newStatus == RequestStatus.IN_PROGRESS) {
            throw new InvalidStatusChangeException("Отмененную заявку нельзя взять в работу!");
        }

        request.setStatus(newStatus);
        return requestRepository.update(request);
    }

    public List<Request> filterByStatus(RequestStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Статус для фильтра не задан!");
        }
        return requestRepository.findByStatus(status);
    }

    public List<Request> filterByPriority(RequestPriority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Приоритет для фильтра не задан!");
        }
        return requestRepository.findByPriority(priority);
    }

    public List<Request> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Строка поиска по названию пустая!");
        }
        return requestRepository.searchByTitle(title.trim());
    }

    public List<Request> searchByDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Строка поиска по описанию пустая!");
        }
        return requestRepository.searchByDescription(description.trim());
    }

    public List<Request> filterByUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Некорректный ID пользователя!");
        }
        return requestRepository.findByUserId(userId);
    }

    public List<Request> filterByCar(Long carId) {
        if (carId == null || carId <= 0) {
            throw new IllegalArgumentException("Некорректный ID автомобиля!");
        }
        return requestRepository.findByCarId(carId);
    }

    public List<Request> sortByCreatedAt() {
        return requestRepository.sortByCreatedAt();
    }

    public List<Request> sortByPriority() {
        return requestRepository.sortByPriority();
    }

    private void validateRelatedEntities(Long userId, Long carId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден!");
        }

        Car car = carRepository.findById(carId);
        if (car == null) {
            throw new CarNotFoundException("Автомобиль с ID " + carId + " не найден.");
        }
    }
}
