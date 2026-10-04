package ru.mirea.project;

import ru.mirea.project.car.CarMenu;
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.car.service.CarService;

import ru.mirea.project.user.UserMenu;
import ru.mirea.project.user.repository.JdbcUserRepository;
import ru.mirea.project.user.service.UserService;

// Эти импорты добавишь, когда будут готовы request-классы
// import ru.mirea.project.request.RequestMenu;
// import ru.mirea.project.request.repository.JdbcRequestRepository;
// import ru.mirea.project.request.service.RequestService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Автомобили
        CarService carService =
                new CarService(new JdbcCarRepository());

        CarMenu carMenu =
                new CarMenu(carService, scanner);

        // Пользователи
        UserService userService =
                new UserService(new JdbcUserRepository());

        UserMenu userMenu =
                new UserMenu(userService, scanner);

        // Заявки
        // RequestService requestService =
        //         new RequestService(new JdbcRequestRepository());

        // RequestMenu requestMenu =
        //         new RequestMenu(requestService, scanner);

        while (true) {

            System.out.println();
            System.out.println(
                    "========== FLEET MANAGEMENT SYSTEM =========="
            );

            System.out.println("1. Управление автомобилями");
            System.out.println("2. Управление пользователями");
            System.out.println("3. Управление заявками");
            System.out.println("0. Выход");

            System.out.print("\nВыберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    carMenu.run();
                    break;

                case "2":
                    userMenu.run();
                    break;

                case "3":
                    System.out.println(
                            "Модуль заявок пока не подключён."
                    );
                    // Когда будет готов:
                    // requestMenu.run();
                    break;

                case "0":
                    System.out.println("Программа завершена.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Неизвестная команда!");
            }
        }
    }
}