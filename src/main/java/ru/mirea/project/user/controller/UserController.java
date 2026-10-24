
package ru.mirea.project.user.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ru.mirea.project.user.enums.UserStatus;
import ru.mirea.project.user.exception.UserDataAccessException;
import ru.mirea.project.user.exception.UserNotFoundException;
import ru.mirea.project.user.model.User;
import ru.mirea.project.user.repository.JdbcUserRepository;
import ru.mirea.project.user.service.UserService;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class UserController {

    private final UserService userService =
            new UserService(new JdbcUserRepository());

    private final ObservableList<User> allUsers =
            FXCollections.observableArrayList();

    private FilteredList<User> filteredUsers;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Long> idColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TableColumn<User, String> phoneColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> driverLicenseColumn;

    @FXML
    private TableColumn<User, UserStatus> statusColumn;

    @FXML
    private ComboBox<String> searchFieldComboBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<UserStatus> statusFilterComboBox;

    @FXML
    private ComboBox<String> licenseFilterComboBox;

    @FXML
    private Label countLabel;

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(
                        cell.getValue().getId()
                )
        );

        firstNameColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(
                        cell.getValue().getFirstName()
                )
        );

        lastNameColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(
                        cell.getValue().getLastName()
                )
        );

        phoneColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(
                        cell.getValue().getPhone()
                )
        );

        emailColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(
                        cell.getValue().getEmail()
                )
        );

        driverLicenseColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(
                        cell.getValue().getDriverLicense()
                )
        );

        statusColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(
                        cell.getValue().getStatus()
                )
        );

        searchFieldComboBox.getItems().setAll(
                "Имя или фамилия",
                "Телефон",
                "Email",
                "ID"
        );

        searchFieldComboBox.setValue(
                "Имя или фамилия"
        );

        statusFilterComboBox.getItems().setAll(
                UserStatus.values()
        );

        licenseFilterComboBox.getItems().setAll(
                "Есть",
                "Нет"
        );

        filteredUsers = new FilteredList<>(
                allUsers,
                user -> true
        );

        SortedList<User> sortedUsers =
                new SortedList<>(filteredUsers);

        sortedUsers.comparatorProperty().bind(
                usersTable.comparatorProperty()
        );

        usersTable.setItems(sortedUsers);

        usersTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        usersTable.setPlaceholder(
                new Label("Пользователи не найдены")
        );

        searchTextField.setOnAction(
                event -> applyFilters()
        );

        loadUsers();
    }

    private void loadUsers() {
        try {
            allUsers.setAll(
                    userService.getAllUsers()
            );

            applyFilters();

        } catch (UserDataAccessException e) {
            showError(
                    "Ошибка загрузки",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleApplyFilter() {
        applyFilters();
    }

    private void applyFilters() {
        String query = searchTextField.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        String searchField =
                searchFieldComboBox.getValue();

        UserStatus selectedStatus =
                statusFilterComboBox.getValue();

        String selectedLicense =
                licenseFilterComboBox.getValue();

        filteredUsers.setPredicate(user -> {

            if (selectedStatus != null &&
                    user.getStatus() != selectedStatus) {
                return false;
            }

            if ("Есть".equals(selectedLicense) &&
                    user.getDriverLicense() == null) {
                return false;
            }

            if ("Нет".equals(selectedLicense) &&
                    user.getDriverLicense() != null) {
                return false;
            }

            if (query.isEmpty()) {
                return true;
            }

            if ("Телефон".equals(searchField)) {
                return user.getPhone()
                        .toLowerCase(Locale.ROOT)
                        .contains(query);
            }

            if ("Email".equals(searchField)) {
                return user.getEmail() != null &&
                        user.getEmail()
                                .toLowerCase(Locale.ROOT)
                                .contains(query);
            }

            if ("ID".equals(searchField)) {
                return String.valueOf(user.getId())
                        .equals(query);
            }

            return user.getFirstName()
                    .toLowerCase(Locale.ROOT)
                    .contains(query)
                    ||
                    user.getLastName()
                            .toLowerCase(Locale.ROOT)
                            .contains(query);
        });

        countLabel.setText(
                "Найдено: " + filteredUsers.size()
        );
    }

    @FXML
    private void handleResetFilter() {
        searchTextField.clear();

        searchFieldComboBox.setValue(
                "Имя или фамилия"
        );

        statusFilterComboBox.setValue(null);
        licenseFilterComboBox.setValue(null);

        applyFilters();
    }

    @FXML
    private void handleOpenAddForm() {
        openUserForm(null);
    }

    @FXML
    private void handleOpenEditForm() {
        User selectedUser =
                usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showWarning(
                    "Выберите пользователя для редактирования."
            );
            return;
        }

        openUserForm(selectedUser);
    }

    private void openUserForm(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/fxml/forms/user-form.fxml"
                    )
            );

            Parent root = loader.load();

            UserFormController controller =
                    loader.getController();

            if (user != null) {
                controller.setEditedUser(user);
            }

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass().getResource(
                            "/css/app.css"
                    ).toExternalForm()
            );

            Stage stage = new Stage();

            stage.setTitle(
                    user == null
                            ? "Добавление пользователя"
                            : "Редактирование пользователя"
            );

            stage.initModality(Modality.WINDOW_MODAL);

            stage.initOwner(
                    usersTable.getScene().getWindow()
            );

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isSaved()) {
                loadUsers();
            }

        } catch (IOException e) {
            showError(
                    "Ошибка открытия формы",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleDelete() {
        User selectedUser =
                usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showWarning(
                    "Выберите пользователя для удаления."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.initOwner(
                usersTable.getScene().getWindow()
        );

        confirmation.setTitle(
                "Подтверждение удаления"
        );

        confirmation.setHeaderText(
                "Удалить пользователя?"
        );

        confirmation.setContentText(
                selectedUser.getFirstName() + " " +
                        selectedUser.getLastName() +
                        " (ID: " + selectedUser.getId() + ")"
        );

        confirmation.getButtonTypes().setAll(
                ButtonType.YES,
                ButtonType.NO
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isEmpty() ||
                result.get() != ButtonType.YES) {
            return;
        }

        try {
            userService.deleteUser(
                    selectedUser.getId()
            );

            loadUsers();

        } catch (UserNotFoundException |
                 UserDataAccessException e) {
            showError(
                    "Ошибка удаления",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleExportCsv() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(
                "Сохранить пользователей в CSV"
        );

        fileChooser.setInitialFileName(
                "users.csv"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "CSV файлы (*.csv)",
                        "*.csv"
                )
        );

        File file = fileChooser.showSaveDialog(
                usersTable.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        try {
            userService.exportToCsv(
                    usersTable.getItems(),
                    file.toPath()
            );

            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION
            );

            alert.setTitle("Экспорт CSV");
            alert.setHeaderText(null);

            alert.setContentText(
                    "Файл успешно сохранён:\n" +
                            file.getAbsolutePath()
            );

            alert.showAndWait();

        } catch (RuntimeException e) {
            showError(
                    "Ошибка экспорта",
                    e.getMessage()
            );
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(
                Alert.AlertType.WARNING
        );

        alert.setTitle("Внимание");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(
            String title,
            String message
    ) {
        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
