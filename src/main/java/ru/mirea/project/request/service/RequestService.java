package ru.mirea.project.request.service;

import ru.mirea.project.car.exception.CarNotFoundException;
import ru.mirea.project.car.model.Car;
import ru.mirea.project.car.repository.CarRepository;
import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.exception.InvalidRequestException;
import ru.mirea.project.request.exception.InvalidStatusChangeException;
import ru.mirea.project.request.exception.RequestNotFoundException;
import ru.mirea.project.request.model.Request;
import ru.mirea.project.request.repository.RequestRepository;
import ru.mirea.project.user.exception.UserNotFoundException;
import ru.mirea.project.user.repository.UserRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public RequestService(RequestRepository requestRepository, UserRepository userRepository, CarRepository carRepository) {
        this.requestRepository = Objects.requireNonNull(requestRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.carRepository = Objects.requireNonNull(carRepository);
    }

    public Request createRental(Long userId, Long carId, LocalDate start, LocalDate end, Integer monthlyPrice, String notes) {
        validateRelated(userId, carId);
        ensureNoOverlap(carId, start, end, null);
        Request request = new Request(null, userId, carId, start, end, monthlyPrice, RequestStatus.NEW, notes);
        return requestRepository.save(request);
    }

    public List<Request> getAll() { return requestRepository.findAll(); }

    public Request getById(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Некорректный ID заявки!");
        Request request = requestRepository.findById(id);
        if (request == null) throw new RequestNotFoundException(id);
        return request;
    }

    public Request updateRental(Request request) {
        getById(request.getId());
        validateRelated(request.getUserId(), request.getCarId());
        if (request.getStatus() == RequestStatus.APPROVED || request.getStatus() == RequestStatus.ACTIVE) {
            ensureNoOverlap(request.getCarId(), request.getStartDate(), request.getEndDate(), request.getId());
        }
        return requestRepository.update(request);
    }

    public void delete(Long id) {
        getById(id);
        requestRepository.deleteById(id);
    }

    public Request changeStatus(Long id, RequestStatus newStatus) {
        Request request = getById(id);
        RequestStatus current = request.getStatus();
        if (current == RequestStatus.COMPLETED || current == RequestStatus.CANCELLED) {
            throw new InvalidStatusChangeException("Завершённую или отменённую аренду нельзя менять!");
        }
        if (current == RequestStatus.NEW && newStatus == RequestStatus.ACTIVE) {
            throw new InvalidStatusChangeException("Сначала одобрите заявку, потом активируйте аренду!");
        }
        if (newStatus == RequestStatus.APPROVED || newStatus == RequestStatus.ACTIVE) {
            ensureNoOverlap(request.getCarId(), request.getStartDate(), request.getEndDate(), request.getId());
        }
        request.setStatus(newStatus);
        return requestRepository.update(request);
    }

    public List<Request> filterByStatus(RequestStatus status) { return requestRepository.findByStatus(status); }
    public List<Request> filterByUser(Long userId) { return requestRepository.findByUserId(userId); }
    public List<Request> filterByCar(Long carId) { return requestRepository.findByCarId(carId); }
    public List<Request> searchByNotes(String notes) { return requestRepository.searchByNotes(notes); }
    public List<Request> sortByStartDate(boolean ascending) { return requestRepository.sortByStartDate(ascending); }
    public List<Request> sortByMonthlyPrice(boolean ascending) { return requestRepository.sortByMonthlyPrice(ascending); }

    public RentalStatistics statistics() {
        List<Request> all = getAll();
        long total = all.size();
        long active = all.stream().filter(r -> r.getStatus() == RequestStatus.ACTIVE).count();
        long approved = all.stream().filter(r -> r.getStatus() == RequestStatus.APPROVED).count();
        long completed = all.stream().filter(r -> r.getStatus() == RequestStatus.COMPLETED).count();
        int minPrice = all.stream().mapToInt(Request::getMonthlyPrice).min().orElse(0);
        int maxPrice = all.stream().mapToInt(Request::getMonthlyPrice).max().orElse(0);
        double avgPrice = all.stream().mapToInt(Request::getMonthlyPrice).average().orElse(0);
        long longRentals = all.stream().filter(r -> r.rentalDays() >= 90).count();
        return new RentalStatistics(total, active, approved, completed, minPrice, maxPrice, avgPrice, longRentals);
    }

    public void exportCsv(List<Request> requests, File file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write('\uFEFF');
            writer.write("ID,Арендатор,Автомобиль,Начало,Окончание,Дней,Цена в месяц,Статус,Примечание");
            writer.newLine();
            for (Request r : requests) {
                writer.write(r.getId() + "," + r.getUserId() + "," + r.getCarId() + ","
                        + r.getStartDate() + "," + r.getEndDate() + "," + r.rentalDays() + ","
                        + r.getMonthlyPrice() + "," + r.getStatus().name() + "," + csv(r.getNotes()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new InvalidRequestException("Не удалось экспортировать заявки: " + e.getMessage());
        }
    }

    private void validateRelated(Long userId, Long carId) {
        if (userRepository.findById(userId) == null) {
            throw new UserNotFoundException("Арендатор с ID " + userId + " не найден!");
        }
        Car car = carRepository.findById(carId);
        if (car == null) {
            throw new CarNotFoundException("Автомобиль с ID " + carId + " не найден.");
        }
    }

    private void ensureNoOverlap(Long carId, LocalDate start, LocalDate end, Long excludeId) {
        if (!requestRepository.findOverlapping(carId, start, end, excludeId).isEmpty()) {
            throw new InvalidRequestException("Автомобиль уже занят другой одобренной или активной арендой на эти даты!");
        }
    }

    private String csv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        return escaped.contains(",") || escaped.contains("\n") ? "\"" + escaped + "\"" : escaped;
    }

    public record RentalStatistics(long total, long active, long approved, long completed,
                                   int minPrice, int maxPrice, double avgPrice, long longRentals) {}
}
