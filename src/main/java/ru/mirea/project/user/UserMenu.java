package ru.mirea.project.user;

import ru.mirea.project.user.exception.UserAlreadyExistsException;
import ru.mirea.project.user.exception.UserDataAccessException;
import ru.mirea.project.user.exception.UserNotFoundException;
import ru.mirea.project.user.model.User;
import ru.mirea.project.user.repository.JdbcUserRepository;
import ru.mirea.project.user.service.UserService;

import java.util.List;
import java.util.Scanner;

public class UserMenu {

    private final UserService service;
    private final Scanner scanner;

    public UserMenu(UserService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    // Основное меню
    public void run() {

        while (true) {

            System.out.println("\n========== ПОЛЬЗОВАТЕЛИ ==========");
            System.out.println("1. Добавить пользователя");
            System.out.println("2. Показать всех пользователей");
            System.out.println("3. Найти пользователя по ID");
            System.out.println("4. Найти по телефону");
            System.out.println("5. Поиск по имени или фамилии");
            System.out.println("6. Изменить пользователя");
            System.out.println("7. Удалить пользователя");
            System.out.println("8. Сортировать по фамилии");
            System.out.println("9. Показать пользователей с правами");
            System.out.println("0. Вернуться в главное меню");

            System.out.print("\nВыберите действие: ");
            String choice = scanner.nextLine().trim();

            try {

                switch (choice) {

                    case "1":
                        createUser();
                        break;

                    case "2":
                        showAllUsers();
                        break;

                    case "3":
                        findById();
                        break;

                    case "4":
                        findByPhone();
                        break;

                    case "5":
                        searchByName();
                        break;

                    case "6":
                        updateUser();
                        break;

                    case "7":
                        deleteUser();
                        break;

                    case "8":
                        printUsers(service.sortByLastName());
                        break;

                    case "9":
                        printUsers(service.filterWithDriverLicense());
                        break;

                    case "0":
                        System.out.println("Выход из меню пользователей.");
                        return;

                    default:
                        System.out.println("Неизвестная команда!");
                }

            } catch (
                    IllegalArgumentException |
                    UserNotFoundException |
                    UserAlreadyExistsException |
                    UserDataAccessException e
            ) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    // Добавление пользователя
    private void createUser() {

        System.out.println("\n=== ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");

        String firstName = read("Имя: ");
        String lastName = read("Фамилия: ");
        String phone = readPhone();
        String email = read("Email (можно пропустить): ");
        String license = read("Водительское удостоверение (можно пропустить): ");

        User user = service.createUser(
                firstName,
                lastName,
                phone,
                email,
                license
        );

        System.out.println("\nПользователь успешно создан!");
        printUser(user);
    }

    // Просмотр всех пользователей
    private void showAllUsers() {

        System.out.println("\n=== СПИСОК ПОЛЬЗОВАТЕЛЕЙ ===");

        printUsers(service.getAllUsers());
    }

    // Поиск по ID
    private void findById() {

        System.out.println("\n=== ПОИСК ПО ID ===");

        Long id = readId();

        User user = service.getUserById(id);

        printUser(user);
    }

    // Поиск по телефону
    private void findByPhone() {

        System.out.println("\n=== ПОИСК ПО ТЕЛЕФОНУ ===");

        String phone = read("Введите телефон: ");

        User user = service.findByPhone(phone);

        printUser(user);
    }

    // Поиск по имени или фамилии
    private void searchByName() {

        System.out.println("\n=== ПОИСК ПО ИМЕНИ ===");

        String name = read("Введите имя или фамилию: ");

        List<User> users = service.searchByName(name);

        printUsers(users);
    }

    // Изменение пользователя
    private void updateUser() {

        System.out.println("\n=== ИЗМЕНЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");

        Long id = readId();

        User oldUser = service.getUserById(id);

        System.out.println("\nТекущие данные:");
        printUser(oldUser);

        System.out.println("\nВведите новые данные.");
        System.out.println("Enter - оставить прежнее значение.");
        System.out.println("- - очистить необязательное поле.");

        String firstName = readUpdated(
                "Имя", oldUser.getFirstName(), false
        );

        String lastName = readUpdated(
                "Фамилия", oldUser.getLastName(), false
        );

        String phone = readUpdated(
                "Телефон", oldUser.getPhone(), false
        );

        String email = readUpdated(
                "Email", oldUser.getEmail(), true
        );

        String license = readUpdated(
                "Водительское удостоверение",
                oldUser.getDriverLicense(),
                true
        );

        User updated = service.updateUser(
                id,
                firstName,
                lastName,
                phone,
                email,
                license
        );

        System.out.println("\nДанные пользователя обновлены!");
        printUser(updated);
    }

    // Удаление пользователя
    private void deleteUser() {

        System.out.println("\n=== УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");

        Long id = readId();

        User user = service.getUserById(id);

        printUser(user);

        String answer = read("Подтвердить удаление? (yes/no): ");

        if (answer.equalsIgnoreCase("yes")) {

            service.deleteUser(id);

            System.out.println("Пользователь удалён!");

        } else {
            System.out.println("Удаление отменено.");
        }
    }

    // Ввод строки
    private String read(String message) {

        System.out.print(message);

        return scanner.nextLine().trim();
    }

    // Ввод телефона с проверкой формата
    private String readPhone() {

        while (true) {

            String phone = read("Телефон (+79991234567): ");

            if (phone.matches("\\+7\\d{10}")) {
                return phone;
            }

            System.out.println(
                    "Ошибка! Номер должен быть в формате +79991234567."
            );

            System.out.println("Попробуйте ещё раз.\n");
        }
    }


    // Ввод ID
    private Long readId() {

        String value = read("Введите ID пользователя: ");

        return Long.parseLong(value);
    }

    // Ввод данных при изменении
    private String readUpdated(
            String field,
            String oldValue,
            boolean optional
    ) {

        String current = oldValue == null
                ? "не указано"
                : oldValue;

        String value = read(
                field + " [" + current + "]: "
        );

        if (value.isEmpty()) {
            return oldValue;
        }

        if (value.equals("-")) {

            if (!optional) {
                throw new IllegalArgumentException(
                        "Это поле нельзя очистить!"
                );
            }

            return null;
        }

        return value;
    }

    // Вывод одного пользователя
    private void printUser(User user) {

        System.out.println("-----------------------------");

        System.out.println("ID: " + user.getId());

        System.out.println(
                "Имя: " + user.getFirstName()
        );

        System.out.println(
                "Фамилия: " + user.getLastName()
        );

        System.out.println(
                "Телефон: " + user.getPhone()
        );

        System.out.println(
                "Email: " + (
                        user.getEmail() == null
                                ? "не указан"
                                : user.getEmail()
                )
        );

        System.out.println(
                "Водительское удостоверение: " + (
                        user.getDriverLicense() == null
                                ? "не указано"
                                : user.getDriverLicense()
                )
        );

        System.out.println("-----------------------------");
    }

    // Вывод списка пользователей
    private void printUsers(List<User> users) {

        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
            return;
        }

        System.out.println("Найдено пользователей: " + users.size());

        for (User user : users) {
            printUser(user);
        }
    }

    // Временный запуск для проверки меню
    public static void main(String[] args) {

        UserService service = new UserService(
                new JdbcUserRepository()
        );

        Scanner scanner = new Scanner(System.in);

        UserMenu menu = new UserMenu(service, scanner);

        menu.run();
    }
}
