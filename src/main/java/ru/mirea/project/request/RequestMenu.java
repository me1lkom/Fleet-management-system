package ru.mirea.project.request;

import ru.mirea.project.car.exception.CarNotFoundException;
import ru.mirea.project.car.exception.DataAccessException;
import ru.mirea.project.request.enums.RequestPriority;
import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.exception.InvalidRequestException;
import ru.mirea.project.request.exception.InvalidStatusChangeException;
import ru.mirea.project.request.exception.RequestDataAccessException;
import ru.mirea.project.request.exception.RequestNotFoundException;
import ru.mirea.project.request.model.Request;
import ru.mirea.project.request.service.RequestService;
import ru.mirea.project.user.exception.UserDataAccessException;
import ru.mirea.project.user.exception.UserNotFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class RequestMenu {

    private final RequestService service;
    private final Scanner scanner;

    public RequestMenu(RequestService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            System.out.println("\n=== МЕНЮ ЗАЯВОК ===");
            System.out.println("1. Создать заявку");
            System.out.println("2. Показать все заявки");
            System.out.println("3. Найти заявку по ID");
            System.out.println("4. Поиск по названию");
            System.out.println("5. Поиск по описанию");
            System.out.println("6. Фильтр по статусу");
            System.out.println("7. Фильтр по приоритету");
            System.out.println("8. Фильтр по пользователю");
            System.out.println("9. Фильтр по автомобилю");
            System.out.println("10. Изменить статус");
            System.out.println("11. Обновить заявку");
            System.out.println("12. Удалить заявку");
            System.out.println("13. Сортировать по дате");
            System.out.println("14. Сортировать по приоритету");
            System.out.println("0. Вернуться в главное меню");
            System.out.print("\nВыберите действие: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        createRequest();
                        break;
                    case "2":
                        printRequests(service.getAllRequests());
                        break;
                    case "3":
                        printRequest(service.getRequestById(readLong("ID заявки: ")));
                        break;
                    case "4":
                        printRequests(service.searchByTitle(read("Название: ")));
                        break;
                    case "5":
                        printRequests(service.searchByDescription(read("Описание: ")));
                        break;
                    case "6":
                        printRequests(service.filterByStatus(readStatus()));
                        break;
                    case "7":
                        printRequests(service.filterByPriority(readPriority()));
                        break;
                    case "8":
                        printRequests(service.filterByUser(readLong("ID пользователя: ")));
                        break;
                    case "9":
                        printRequests(service.filterByCar(readLong("ID автомобиля: ")));
                        break;
                    case "10":
                        changeStatus();
                        break;
                    case "11":
                        updateRequest();
                        break;
                    case "12":
                        service.deleteRequest(readLong("ID заявки: "));
                        System.out.println("Заявка удалена.");
                        break;
                    case "13":
                        printRequests(service.sortByCreatedAt());
                        break;
                    case "14":
                        printRequests(service.sortByPriority());
                        break;
                    case "0":
                        System.out.println("Выход из меню заявок.");
                        return;
                    default:
                        System.out.println("Неизвестная команда!");
                }
            } catch (
                    IllegalArgumentException
                    | RequestNotFoundException
                    | InvalidRequestException
                    | InvalidStatusChangeException
                    | RequestDataAccessException
                    | UserNotFoundException
                    | CarNotFoundException
                    | UserDataAccessException
                    | DataAccessException e
            ) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void createRequest() {
        System.out.println("\n=== СОЗДАНИЕ ЗАЯВКИ ===");
        Request request = service.createRequest(
                readLong("ID пользователя: "),
                readLong("ID автомобиля: "),
                read("Название: "),
                read("Описание: "),
                readPriority()
        );
        System.out.println("Заявка создана:");
        printRequest(request);
    }

    private void changeStatus() {
        Long id = readLong("ID заявки: ");
        RequestStatus status = readStatus();
        Request updated = service.changeStatus(id, status);
        System.out.println("Статус обновлён:");
        printRequest(updated);
    }

    private void updateRequest() {
        Long id = readLong("ID заявки: ");
        Request current = service.getRequestById(id);

        current.setUserId(readLong("Новый ID пользователя: "));
        current.setCarId(readLong("Новый ID автомобиля: "));
        current.setTitle(read("Новое название: "));
        current.setDescription(read("Новое описание: "));
        current.setPriority(readPriority());

        Request updated = service.updateRequest(current);
        System.out.println("Заявка обновлена:");
        printRequest(updated);
    }

    private RequestStatus readStatus() {
        System.out.println("Статусы: NEW, IN_PROGRESS, COMPLETED, CANCELLED");
        String value = read("Статус: ").toUpperCase(Locale.ROOT);
        return RequestStatus.valueOf(value);
    }

    private RequestPriority readPriority() {
        System.out.println("Приоритеты: LOW, MEDIUM, HIGH");
        String value = read("Приоритет: ").toUpperCase(Locale.ROOT);
        return RequestPriority.valueOf(value);
    }

    private String read(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private Long readLong(String prompt) {
        String value = read(prompt);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Нужно ввести число!");
        }
    }

    private void printRequest(Request request) {
        System.out.println("-----------------------------");
        System.out.println("ID: " + request.getId());
        System.out.println("Пользователь: " + request.getUserId());
        System.out.println("Автомобиль: " + request.getCarId());
        System.out.println("Название: " + request.getTitle());
        System.out.println("Описание: " + request.getDescription());
        System.out.println("Статус: " + request.getStatus());
        System.out.println("Приоритет: " + request.getPriority());
        System.out.println("Дата создания: " + request.getCreatedAt());
        System.out.println("-----------------------------");
    }

    private void printRequests(List<Request> requests) {
        if (requests.isEmpty()) {
            System.out.println("Заявки не найдены.");
            return;
        }
        System.out.println("Найдено заявок: " + requests.size());
        for (Request request : requests) {
            printRequest(request);
        }
    }
}
