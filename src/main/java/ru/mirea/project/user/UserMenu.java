
package ru.mirea.project.user;

import ru.mirea.project.user.enums.UserStatus;
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
            System.out.println("8. Сортировка пользователей");
            System.out.println("9. Пользователи с водительским удостоверением");
            System.out.println("10. Экспорт пользователей в CSV");
            System.out.println("11. Фильтрация по статусу");
            System.out.println("0. Вернуться в главное меню");

            String choice = read("\nВыберите действие: ");

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
                        sortUsers();
                        break;

                    case "9":
                        printUsers(service.filterWithDriverLicense());
                        break;

                    case "10":
                        exportUsers();
                        break;

                    case "11":
                        filterByStatus();
                        break;

                    case "0":
                        return;

                    default:
                        System.out.println("Ошибка! Такой команды нет.");
                }

            } catch (UserDataAccessException e) {
                System.out.println(
                        "Ошибка базы данных: " + e.getMessage()
                );

            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void createUser() {
        System.out.println("\n=== ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");

        while (true) {
            String firstName = readRequired("Имя: ");
            String lastName = readRequired("Фамилия: ");
            String phone = readPhone();
            String email = readEmail();
            String driverLicense = readDriverLicense();
            UserStatus status = readStatus();

            try {
                User user = service.createUser(
                        firstName,
                        lastName,
                        phone,
                        email,
                        driverLicense,
                        status
                );

                System.out.println("\nПользователь успешно создан!");
                printUser(user);
                return;

            } catch (UserAlreadyExistsException e) {
                System.out.println("Ошибка: " + e.getMessage());
                System.out.println("Введите данные ещё раз.");

            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
                System.out.println("Введите данные ещё раз.");
            }
        }
    }

    private void showAllUsers() {
        System.out.println("\n=== СПИСОК ПОЛЬЗОВАТЕЛЕЙ ===");
        printUsers(service.getAllUsers());
    }

    private void findById() {
        System.out.println("\n=== ПОИСК ПО ID ===");
        User user = readExistingUser();
        printUser(user);
    }

    private void findByPhone() {
        System.out.println("\n=== ПОИСК ПО ТЕЛЕФОНУ ===");

        while (true) {
            String phone = readPhone();

            try {
                User user = service.findByPhone(phone);
                printUser(user);
                return;

            } catch (UserNotFoundException e) {
                System.out.println(
                        "Пользователь с таким телефоном не найден."
                );
                System.out.println("Введите телефон ещё раз.");
            }
        }
    }

    private void searchByName() {
        System.out.println("\n=== ПОИСК ПО ИМЕНИ ИЛИ ФАМИЛИИ ===");

        String name = readRequired(
                "Введите имя или фамилию: "
        );

        printUsers(service.searchByName(name));
    }

    private void updateUser() {
        System.out.println("\n=== ИЗМЕНЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");

        User oldUser = readExistingUser();

        System.out.println("\nТекущие данные:");
        printUser(oldUser);

        while (true) {
            System.out.println("\nВведите новые данные.");
            System.out.println("Enter - оставить старое значение.");
            System.out.println("- - очистить необязательное поле.");

            String firstName = readUpdatedRequired(
                    "Имя",
                    oldUser.getFirstName()
            );

            String lastName = readUpdatedRequired(
                    "Фамилия",
                    oldUser.getLastName()
            );

            String phone = readUpdatedPhone(
                    oldUser.getPhone()
            );

            String email = readUpdatedEmail(
                    oldUser.getEmail()
            );

            String driverLicense = readUpdatedDriverLicense(
                    oldUser.getDriverLicense()
            );

            UserStatus status = readUpdatedStatus(
                    oldUser.getStatus()
            );

            try {
                User updated = service.updateUser(
                        oldUser.getId(),
                        firstName,
                        lastName,
                        phone,
                        email,
                        driverLicense,
                        status
                );

                System.out.println(
                        "\nПользователь успешно изменён!"
                );

                printUser(updated);
                return;

            } catch (UserAlreadyExistsException e) {
                System.out.println("Ошибка: " + e.getMessage());
                System.out.println("Введите новые данные ещё раз.");

            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
                System.out.println("Введите новые данные ещё раз.");
            }
        }
    }

    private void deleteUser() {
        System.out.println("\n=== УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===");

        User user = readExistingUser();

        System.out.println("\nБудет удалён пользователь:");
        printUser(user);

        while (true) {
            String answer = read(
                    "Удалить пользователя? (yes/no): "
            );

            if (answer.equalsIgnoreCase("yes")) {
                service.deleteUser(user.getId());
                System.out.println("Пользователь успешно удалён!");
                return;
            }

            if (answer.equalsIgnoreCase("no")) {
                System.out.println("Удаление отменено.");
                return;
            }

            System.out.println("Ошибка! Введите yes или no.");
        }
    }

    private void sortUsers() {
        while (true) {
            System.out.println("\n=== СОРТИРОВКА ПОЛЬЗОВАТЕЛЕЙ ===");
            System.out.println("1. По фамилии А -> Я");
            System.out.println("2. По фамилии Я -> А");
            System.out.println("3. По ID по возрастанию");
            System.out.println("4. По ID по убыванию");
            System.out.println("0. Назад");

            String choice = read("\nВыберите действие: ");

            switch (choice) {
                case "1":
                    printUsers(service.sortByLastNameAsc());
                    break;

                case "2":
                    printUsers(service.sortByLastNameDesc());
                    break;

                case "3":
                    printUsers(service.sortByIdAsc());
                    break;

                case "4":
                    printUsers(service.sortByIdDesc());
                    break;

                case "0":
                    return;

                default:
                    System.out.println("Ошибка! Такой команды нет.");
            }
        }
    }

    private void filterByStatus() {
        System.out.println("\n=== ФИЛЬТРАЦИЯ ПО СТАТУСУ ===");

        UserStatus status = readStatus();

        printUsers(service.filterByStatus(status));
    }

    private void exportUsers() {
        System.out.println("\n=== ЭКСПОРТ ПОЛЬЗОВАТЕЛЕЙ В CSV ===");

        String fileName = "users.csv";

        service.exportToCsv(fileName);

        System.out.println(
                "Пользователи успешно экспортированы в файл: exports/" +
                        fileName
        );
    }

    private String read(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private String readRequired(String message) {
        while (true) {
            String value = read(message);

            if (!value.isBlank()) {
                return value;
            }

            System.out.println(
                    "Ошибка! Поле не может быть пустым."
            );
        }
    }

    private Long readId() {
        while (true) {
            String value = read("Введите ID пользователя: ");

            try {
                long id = Long.parseLong(value);

                if (id <= 0) {
                    System.out.println(
                            "Ошибка! ID должен быть больше 0."
                    );
                    continue;
                }

                return id;

            } catch (NumberFormatException e) {
                System.out.println(
                        "Ошибка! ID должен быть целым числом."
                );
            }
        }
    }

    private User readExistingUser() {
        while (true) {
            Long id = readId();

            try {
                return service.getUserById(id);

            } catch (UserNotFoundException e) {
                System.out.println(
                        "Пользователь с ID " + id + " не найден."
                );
                System.out.println("Введите ID ещё раз.");
            }
        }
    }

    private String readPhone() {
        while (true) {
            String phone = read(
                    "Телефон (+79991234567): "
            );

            if (phone.matches("\\+7\\d{10}")) {
                return phone;
            }

            System.out.println(
                    "Ошибка! Телефон должен быть в формате +79991234567."
            );
        }
    }

    private String readEmail() {
        while (true) {
            String email = read(
                    "Email (можно пропустить): "
            );

            if (email.isBlank()) {
                return null;
            }

            if (isValidEmail(email)) {
                return email;
            }

            System.out.println(
                    "Ошибка! Некорректный email. Пример: user@mail.ru"
            );
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
    }

    private String readDriverLicense() {
        while (true) {
            String license = read(
                    "Водительское удостоверение " +
                            "(10 цифр, можно пропустить): "
            );

            if (license.isBlank()) {
                return null;
            }

            license = license.replaceAll("\\s+", "");

            if (license.matches("\\d{10}")) {
                return license;
            }

            System.out.println(
                    "Ошибка! Водительское удостоверение должно содержать 10 цифр."
            );
        }
    }

    private UserStatus readStatus() {
        while (true) {
            System.out.println("\nВыберите статус пользователя:");
            System.out.println("1. REGULAR");
            System.out.println("2. VIP");

            String choice = read("Статус: ");

            switch (choice) {
                case "1":
                    return UserStatus.REGULAR;

                case "2":
                    return UserStatus.VIP;

                default:
                    System.out.println(
                            "Ошибка! Выберите 1 или 2."
                    );
            }
        }
    }

    private UserStatus readUpdatedStatus(UserStatus oldStatus) {
        while (true) {
            System.out.println("\n1. REGULAR");
            System.out.println("2. VIP");

            String choice = read(
                    "Статус [" + oldStatus + "] " +
                            "(Enter - оставить): "
            );

            if (choice.isEmpty()) {
                return oldStatus;
            }

            switch (choice) {
                case "1":
                    return UserStatus.REGULAR;

                case "2":
                    return UserStatus.VIP;

                default:
                    System.out.println(
                            "Ошибка! Выберите 1, 2 или Enter."
                    );
            }
        }
    }

    private String readUpdatedRequired(
            String field,
            String oldValue
    ) {
        while (true) {
            String value = read(
                    field + " [" + oldValue + "]: "
            );

            if (value.isEmpty()) {
                return oldValue;
            }

            if (value.equals("-")) {
                System.out.println(
                        "Ошибка! Это поле нельзя очистить."
                );
                continue;
            }

            return value;
        }
    }

    private String readUpdatedPhone(String oldPhone) {
        while (true) {
            String value = read(
                    "Телефон [" + oldPhone + "]: "
            );

            if (value.isEmpty()) {
                return oldPhone;
            }

            if (value.matches("\\+7\\d{10}")) {
                return value;
            }

            System.out.println(
                    "Ошибка! Телефон должен быть в формате +79991234567."
            );
        }
    }

    private String readUpdatedEmail(String oldEmail) {
        while (true) {
            String shown = oldEmail == null
                    ? "не указан"
                    : oldEmail;

            String value = read(
                    "Email [" + shown + "]: "
            );

            if (value.isEmpty()) {
                return oldEmail;
            }

            if (value.equals("-")) {
                return null;
            }

            if (isValidEmail(value)) {
                return value;
            }

            System.out.println(
                    "Ошибка! Некорректный email. Пример: user@mail.ru"
            );
        }
    }

    private String readUpdatedDriverLicense(
            String oldDriverLicense
    ) {
        while (true) {
            String shown = oldDriverLicense == null
                    ? "не указано"
                    : oldDriverLicense;

            String value = read(
                    "Водительское удостоверение [" + shown + "]: "
            );

            if (value.isEmpty()) {
                return oldDriverLicense;
            }

            if (value.equals("-")) {
                return null;
            }

            value = value.replaceAll("\\s+", "");

            if (value.matches("\\d{10}")) {
                return value;
            }

            System.out.println(
                    "Ошибка! Водительское удостоверение должно содержать 10 цифр."
            );
        }
    }

    private void printUser(User user) {
        System.out.println("-----------------------------");
        System.out.println("ID: " + user.getId());
        System.out.println("Имя: " + user.getFirstName());
        System.out.println("Фамилия: " + user.getLastName());
        System.out.println("Телефон: " + user.getPhone());

        System.out.println(
                "Email: " +
                        (user.getEmail() == null
                                ? "не указан"
                                : user.getEmail())
        );

        System.out.println(
                "Водительское удостоверение: " +
                        (user.getDriverLicense() == null
                                ? "не указано"
                                : user.getDriverLicense())
        );

        System.out.println("Статус: " + user.getStatus());
        System.out.println("-----------------------------");
    }

    private void printUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
            return;
        }

        System.out.println(
                "Найдено пользователей: " + users.size()
        );

        for (User user : users) {
            printUser(user);
        }
    }

    public static void main(String[] args) {
        UserService service = new UserService(
                new JdbcUserRepository()
        );

        Scanner scanner = new Scanner(System.in);

        UserMenu menu = new UserMenu(service, scanner);
        menu.run();
    }
}
