package ru.mirea.project.user.service;

import ru.mirea.project.user.exception.UserAlreadyExistsException;
import ru.mirea.project.user.exception.UserNotFoundException;
import ru.mirea.project.user.model.User;
import ru.mirea.project.user.repository.UserRepository;
import java.nio.file.Path;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserService {

    private final UserRepository repository;

    public UserService(
            UserRepository repository
    ) {

        this.repository =
                Objects.requireNonNull(
                        repository
                );
    }


    private void checkDuplicates(
            User user
    ) {

        User existingPhone =
                repository.findByPhone(
                        user.getPhone()
                );

        if (
                existingPhone != null &&
                        !Objects.equals(
                                existingPhone.getId(),
                                user.getId()
                        )
        ) {

            throw new UserAlreadyExistsException(
                    "Пользователь с таким телефоном уже существует!"
            );
        }

        if (user.getEmail() != null) {

            User existingEmail =
                    repository.findByEmail(
                            user.getEmail()
                    );

            if (
                    existingEmail != null &&
                            !Objects.equals(
                                    existingEmail.getId(),
                                    user.getId()
                            )
            ) {

                throw new UserAlreadyExistsException(
                        "Пользователь с таким email уже существует!"
                );
            }
        }
    }

    public User createUser(
            String firstName,
            String lastName,
            String phone,
            String email,
            String driverLicense
    ) {

        User user =
                new User(
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

    public User getUserById(
            Long id
    ) {

        if (
                id == null ||
                        id <= 0
        ) {

            throw new IllegalArgumentException(
                    "Некорректный ID пользователя!"
            );
        }

        User user =
                repository.findById(id);

        if (user == null) {

            throw new UserNotFoundException(
                    "Пользователь с ID " +
                            id +
                            " не найден!"
            );
        }

        return user;
    }


    public List<User> getAllUsers() {

        return repository.findAll();
    }

    public User findByPhone(
            String phone
    ) {

        if (
                phone == null ||
                        phone.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Телефон не может быть пустым!"
            );
        }

        User user =
                repository.findByPhone(
                        phone.trim()
                );

        if (user == null) {

            throw new UserNotFoundException(
                    "Пользователь с таким телефоном не найден!"
            );
        }

        return user;
    }

    public List<User> searchByName(
            String name
    ) {

        if (
                name == null ||
                        name.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Введите имя или фамилию для поиска!"
            );
        }

        return repository.searchByName(
                name.trim()
        );
    }


    public User updateUser(
            Long id,
            String firstName,
            String lastName,
            String phone,
            String email,
            String driverLicense
    ) {

        getUserById(id);

        User updatedUser =
                new User(
                        id,
                        firstName,
                        lastName,
                        phone,
                        email,
                        driverLicense
                );

        checkDuplicates(
                updatedUser
        );

        return repository.update(
                updatedUser
        );
    }


    public void deleteUser(
            Long id
    ) {

        getUserById(id);

        repository.deleteById(id);
    }


    public List<User> filterWithDriverLicense() {

        return repository.findAll()
                .stream()
                .filter(
                        user ->
                                user.getDriverLicense()
                                        != null
                )
                .collect(
                        Collectors.toList()
                );
    }


    public List<User> sortByLastNameAsc() {

        return repository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                User::getLastName,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .collect(
                        Collectors.toList()
                );
    }


    public List<User> sortByLastNameDesc() {

        return repository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                User::getLastName,
                                String.CASE_INSENSITIVE_ORDER
                        ).reversed()
                )
                .collect(
                        Collectors.toList()
                );
    }


    public List<User> sortByIdAsc() {

        return repository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                User::getId
                        )
                )
                .collect(
                        Collectors.toList()
                );
    }


    public List<User> sortByIdDesc() {

        return repository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                User::getId
                        ).reversed()
                )
                .collect(
                        Collectors.toList()
                );
    }


    public void exportToCsv(String fileName) {

        List<User> users = repository.findAll();

        try {

            Path exportDir = Paths.get("exports");

            Files.createDirectories(exportDir);

            Path filePath = exportDir.resolve(fileName);

            try (
                    BufferedWriter writer =
                            Files.newBufferedWriter(
                                    filePath,
                                    StandardCharsets.UTF_8
                            )
            ) {

                writer.write('\uFEFF');

                writer.write(
                        "ID,Имя,Фамилия,Телефон,Email,Водительское удостоверение"
                );

                writer.newLine();

                for (User user : users) {

                    writer.write(
                            user.getId() + "," +
                                    csvValue(user.getFirstName()) + "," +
                                    csvValue(user.getLastName()) + "," +
                                    csvValue(user.getPhone()) + "," +
                                    csvValue(user.getEmail()) + "," +
                                    csvValue(user.getDriverLicense())
                    );

                    writer.newLine();
                }
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Ошибка при экспорте пользователей в CSV.",
                    e
            );
        }
    }

    private String csvValue(
            String value
    ) {

        if (value == null) {
            return "";
        }

        String escaped =
                value.replace(
                        "\"",
                        "\"\""
                );

        return "\"" +
                escaped +
                "\"";
    }
}