package ru.mirea.project.request.model;

import ru.mirea.project.request.enums.RequestPriority;
import ru.mirea.project.request.enums.RequestStatus;

import java.time.LocalDate;
import java.util.Objects;

public class Request {

    private Long id;
    private Long userId;
    private Long carId;
    private String title;
    private String description;
    private RequestStatus status;
    private RequestPriority priority;
    private LocalDate createdAt;

    public Request(
            Long id,
            Long userId,
            Long carId,
            String title,
            String description,
            RequestStatus status,
            RequestPriority priority,
            LocalDate createdAt
    ) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("ID заявки должен быть положительным!");
        }

        this.id = id;
        setUserId(userId);
        setCarId(carId);
        setTitle(title);
        setDescription(description);
        setStatus(status);
        setPriority(priority);
        setCreatedAt(createdAt != null ? createdAt : LocalDate.now());
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Некорректный пользователь!");
        }
        this.userId = userId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        if (carId == null || carId <= 0) {
            throw new IllegalArgumentException("Некорректный автомобиль!");
        }
        this.carId = carId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название заявки не может быть пустым!");
        }
        this.title = title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание заявки не может быть пустым!");
        }
        this.description = description.trim();
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть пустым!");
        }
        this.status = status;
    }

    public RequestPriority getPriority() {
        return priority;
    }

    public void setPriority(RequestPriority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Приоритет не может быть пустым!");
        }
        this.priority = priority;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("Дата создания не может быть пустой!");
        }
        if (createdAt.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата создания не может быть в будущем!");
        }
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Request)) {
            return false;
        }
        Request request = (Request) o;
        return Objects.equals(id, request.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", userId=" + userId +
                ", carId=" + carId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                '}';
    }
}
