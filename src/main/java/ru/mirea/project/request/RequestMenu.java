package ru.mirea.project.request;

import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.model.Request;
import ru.mirea.project.request.service.RequestService;

import java.time.LocalDate;
import java.util.List;
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
            System.out.println("\n=== ЗАЯВКИ НА ДОЛГОСРОЧНУЮ АРЕНДУ ===");
            System.out.println("1. Создать");
            System.out.println("2. Показать все");
            System.out.println("3. Сменить статус");
            System.out.println("0. Назад");
            System.out.print("Выбор: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> create();
                case "2" -> print(service.getAll());
                case "3" -> change();
                case "0" -> { return; }
                default -> System.out.println("Неизвестная команда");
            }
        }
    }

    private void create() {
        Request request = service.createRental(
                readLong("ID арендатора: "),
                readLong("ID автомобиля: "),
                LocalDate.parse(read("Дата начала YYYY-MM-DD: ")),
                LocalDate.parse(read("Дата окончания YYYY-MM-DD: ")),
                Integer.parseInt(read("Цена в месяц: ")),
                read("Примечание: ")
        );
        System.out.println("Создано: " + request);
    }

    private void change() {
        service.changeStatus(readLong("ID заявки: "), RequestStatus.valueOf(read("Статус: ").toUpperCase()));
        System.out.println("Статус обновлён.");
    }

    private void print(List<Request> requests) {
        if (requests.isEmpty()) System.out.println("Пусто.");
        requests.forEach(System.out::println);
    }

    private String read(String prompt) { System.out.print(prompt); return scanner.nextLine().trim(); }
    private Long readLong(String prompt) { return Long.parseLong(read(prompt)); }
}
