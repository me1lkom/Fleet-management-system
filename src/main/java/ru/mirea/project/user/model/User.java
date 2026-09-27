
package ru.mirea.project.user.model;

import java.util.Locale;

public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String driverLicense;

    public User(
            Long id,
            String firstName,
            String lastName,
            String phone,
            String email,
            String driverLicense
    ) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException(
                    "ID пользователя должен быть положительным!"
            );
        }

        this.id = id;
        setFirstName(firstName);
        setLastName(lastName);
        setPhone(phone);
        setEmail(email);
        setDriverLicense(driverLicense);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException(
                    "Имя не может быть пустым!"
            );
        }

        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(
                    "Фамилия не может быть пустой!"
            );
        }

        this.lastName = lastName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null ||
                !phone.trim().matches("\\+7\\d{10}")) {

            throw new IllegalArgumentException(
                    "Телефон должен быть в формате +79991234567!"
            );
        }

        this.phone = phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            this.email = null;
            return;
        }

        String normalized = email.trim()
                .toLowerCase(Locale.ROOT);

        if (!normalized.matches(
                "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$"
        )) {
            throw new IllegalArgumentException(
                    "Некорректный формат email!"
            );
        }

        this.email = normalized;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        if (driverLicense == null || driverLicense.isBlank()) {
            this.driverLicense = null;
            return;
        }

        String normalized = driverLicense.replaceAll(
                "\\s+", ""
        );

        if (!normalized.matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "Номер водительского удостоверения должен содержать 10 цифр!"
            );
        }

        this.driverLicense = normalized;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", driverLicense='" + driverLicense + '\'' +
                '}';
    }
}
