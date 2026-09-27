package ru.mirea.project.user;

import ru.mirea.project.user.model.User;
import ru.mirea.project.user.repository.JdbcUserRepository;
import ru.mirea.project.user.service.UserService;
import ru.mirea.project.user.exception.UserAlreadyExistsException;

public class UserCrudDemo {

    public static void main(String[] args) {

        UserService service = new UserService(
                new JdbcUserRepository()
        );

        User created = null;

        try {

            // 1. CREATE - добавление пользователя
            System.out.println("=== CREATE ===");

            created = service.createUser(
                    "Иван",
                    "Петров",
                    "+79990001122",
                    "ivan.test@example.com",
                    null
            );

            System.out.println("Создан: " + created);


            // 2. READ - поиск пользователя
            System.out.println("\n=== READ ===");

            User found = service.getUserById(
                    created.getId()
            );

            System.out.println("Найден: " + found);


            // 3. UPDATE - изменение пользователя
            System.out.println("\n=== UPDATE ===");

            User updated = service.updateUser(
                    created.getId(),
                    "Иван",
                    "Сидоров",
                    "+79990001122",
                    "ivan.test@example.com",
                    "7712345678"
            );

            System.out.println("Изменён: " + updated);


            // 4. Получение списка пользователей
            System.out.println("\n=== ALL USERS ===");

            service.getAllUsers().forEach(
                    System.out::println
            );


            // 5. Проверка сортировки
            System.out.println("\n=== SORT ===");

            service.sortByLastName().forEach(
                    System.out::println
            );


            // 6. Проверка фильтрации
            System.out.println("\n=== FILTER ===");

            service.filterWithDriverLicense().forEach(
                    System.out::println
            );


            // 7. Проверка дубликата
            System.out.println("\n=== DUPLICATE TEST ===");

            try {

                service.createUser(
                        "Пётр",
                        "Иванов",
                        "+79990001122",
                        null,
                        null
                );

            } catch (UserAlreadyExistsException e) {

                System.out.println(
                        "Ошибка: " + e.getMessage()
                );
            }


            // 8. Проверка некорректного телефона
            System.out.println("\n=== VALIDATION TEST ===");

            try {

                service.createUser(
                        "Алексей",
                        "Иванов",
                        "123",
                        null,
                        null
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Ошибка: " + e.getMessage()
                );
            }

        } finally {

            // 9. DELETE - удаление тестового пользователя
            if (created != null) {

                System.out.println("\n=== DELETE ===");

                service.deleteUser(created.getId());

                System.out.println(
                        "Тестовый пользователь удалён!"
                );
            }
        }
    }
}
