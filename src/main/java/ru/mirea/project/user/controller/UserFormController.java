package ru.mirea.project.user.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import ru.mirea.project.user.enums.UserStatus;
import ru.mirea.project.user.exception.UserAlreadyExistsException;
import ru.mirea.project.user.exception.UserDataAccessException;
import ru.mirea.project.user.exception.UserNotFoundException;
import ru.mirea.project.user.model.User;
import ru.mirea.project.user.repository.JdbcUserRepository;
import ru.mirea.project.user.service.UserService;

public class UserFormController {

    private final UserService userService =
            new UserService(new JdbcUserRepository());

    private User editedUser;
    private boolean saved = false;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField driverLicenseField;

    @FXML
    private ComboBox<UserStatus> statusComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private void initialize() {
        statusComboBox.getItems().setAll(
                UserStatus.values()
        );

        statusComboBox.setValue(UserStatus.REGULAR);
    }

    public void setEditedUser(User user) {
        editedUser = user;

        titleLabel.setText("Редактирование пользователя");
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        phoneField.setText(user.getPhone());

        emailField.setText(
                user.getEmail() == null ? "" : user.getEmail()
        );

        driverLicenseField.setText(
                user.getDriverLicense() == null
                        ? ""
                        : user.getDriverLicense()
        );

        statusComboBox.setValue(user.getStatus());
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String driverLicense = driverLicenseField.getText().trim();
        UserStatus status = statusComboBox.getValue();

        if (firstName.isEmpty() ||
                lastName.isEmpty() ||
                phone.isEmpty()) {

            errorLabel.setText(
                    "Заполните имя, фамилию и телефон."
            );
            return;
        }

        if (status == null) {
            errorLabel.setText(
                    "Выберите статус пользователя."
            );
            return;
        }

        try {
            if (editedUser == null) {
                userService.createUser(
                        firstName,
                        lastName,
                        phone,
                        email,
                        driverLicense,
                        status
                );
            } else {
                userService.updateUser(
                        editedUser.getId(),
                        firstName,
                        lastName,
                        phone,
                        email,
                        driverLicense,
                        status
                );
            }

            saved = true;
            closeForm();

        } catch (UserAlreadyExistsException |
                 UserNotFoundException |
                 UserDataAccessException |
                 IllegalArgumentException e) {

            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage)
                cancelButton.getScene().getWindow();

        stage.close();
    }
}
