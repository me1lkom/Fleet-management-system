package ru.mirea.project.request.model;

import ru.mirea.project.request.enums.RequestStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Request {

    public static final int MIN_RENTAL_DAYS = 30;

    private Long id;
    private Long userId;
    private Long carId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer monthlyPrice;
    private RequestStatus status;
    private String notes;

    public Request(
            Long id,
            Long userId,
            Long carId,
            LocalDate startDate,
            LocalDate endDate,
            Integer monthlyPrice,
            RequestStatus status,
            String notes
    ) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("ID заявки должен быть положительным!");
        }
        this.id = id;
        setUserId(userId);
        setCarId(carId);
        setPeriod(startDate, endDate);
        setMonthlyPrice(monthlyPrice);
        setStatus(status != null ? status : RequestStatus.NEW);
        setNotes(notes);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getCarId() { return carId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Integer getMonthlyPrice() { return monthlyPrice; }
    public RequestStatus getStatus() { return status; }
    public String getNotes() { return notes; }

    public long rentalDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public void setUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Некорректный арендатор!");
        }
        this.userId = userId;
    }

    public void setCarId(Long carId) {
        if (carId == null || carId <= 0) {
            throw new IllegalArgumentException("Некорректный автомобиль!");
        }
        this.carId = carId;
    }

    public void setPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Даты аренды обязательны!");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала!");
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) < MIN_RENTAL_DAYS) {
            throw new IllegalArgumentException("Долгосрочная аренда: минимум " + MIN_RENTAL_DAYS + " дней!");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setMonthlyPrice(Integer monthlyPrice) {
        if (monthlyPrice == null || monthlyPrice <= 0) {
            throw new IllegalArgumentException("Месячная цена должна быть больше 0!");
        }
        this.monthlyPrice = monthlyPrice;
    }

    public void setStatus(RequestStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть пустым!");
        }
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? null : notes.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        return Objects.equals(id, ((Request) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Request{id=" + id + ", userId=" + userId + ", carId=" + carId
                + ", " + startDate + ".." + endDate + ", price=" + monthlyPrice
                + ", status=" + status + "}";
    }
}
