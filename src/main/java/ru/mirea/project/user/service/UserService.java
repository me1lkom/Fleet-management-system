
package ru.mirea.project.user.service;

import ru.mirea.project.user.exception.UserAlreadyExistsException;
import ru.mirea.project.user.exception.UserNotFoundException;
import ru.mirea.project.user.model.User;
import ru.mirea.project.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    // Проверка уникальности телефона и email
    private void checkDuplicates(User user) {

        User existingPhone = repository.findByPhone(user.getPhone());

        if (existingPhone != null &&
                !Objects.equals(existingPhone.getId(), user.getId())) {

            throw new UserAlreadyExistsException(
                    "Пользователь с таким телефоном уже существует!"
            );
        }

        if (user.getEmail() != null) {

            User existingEmail = repository.findByEmail(user.getEmail());

            if (existingEmail != null &&
                    !Objects.equals(existingEmail.getId(), user.getId())) {

                throw new UserAlreadyExistsException(
                        "Пользователь с таким email уже существует!"
                );
            }
        }
    }

    // Добавление нового пользователя
    public User createUser(
            String firstName,
            String lastName,
            String phone,
            String email,
            String driverLicense
    ) {

        User user = new User(
                null,
                firstName,
                lastName,
                phone,
                email,
                driverLicense
        );

        checkDuplicates(user);

        return repository.save(user);
    }

    // Поиск пользователя по ID
    public User getUserById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Некорректный ID пользователя!"
            );
        }

        User user = repository.findById(id);

        if (user == null) {
            throw new UserNotFoundException(
                    "Пользователь с ID " + id + " не найден!"
            );
        }

        return user;
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // Поиск пользователя по телефону
    public User findByPhone(String phone) {

        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException(
                    "Телефон не может быть пустым!"
            );
        }

        User user = repository.findByPhone(phone.trim());

        if (user == null) {
            throw new UserNotFoundException(
                    "Пользователь с таким телефоном не найден!"
            );
        }

        return user;
    }

    // Поиск по имени или фамилии
    public List<User> searchByName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Введите имя или фамилию для поиска!"
            );
        }

        return repository.searchByName(name.trim());
    }

    // Сортировка по фамилии
    public List<User> sortByLastName() {

        return repository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                User::getLastName,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .collect(Collectors.toList());
    }

    // Фильтрация пользователей с водительским удостоверением
    public List<User> filterWithDriverLicense() {

        return repository.findAll()
                .stream()
                .filter(user -> user.getDriverLicense() != null)
                .collect(Collectors.toList());
    }

    // Изменение пользователя
    public User updateUser(
            Long id,
            String firstName,
            String lastName,
            String phone,
            String email,
            String driverLicense
    ) {

        getUserById(id);

        User updatedUser = new User(
                id,
                firstName,
                lastName,
                phone,
                email,
                driverLicense
        );

        checkDuplicates(updatedUser);

        return repository.update(updatedUser);
    }

    // Удаление пользователя
    public void deleteUser(Long id) {

        getUserById(id);

        repository.deleteById(id);
    }
}
