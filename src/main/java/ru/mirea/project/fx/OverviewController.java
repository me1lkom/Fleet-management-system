package ru.mirea.project.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import ru.mirea.project.car.exception.DataAccessException;
import ru.mirea.project.car.exception.ExportException;
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.car.service.CarService;
import ru.mirea.project.request.repository.JdbcRequestRepository;
import ru.mirea.project.request.service.RequestService;
import ru.mirea.project.user.repository.JdbcUserRepository;
import ru.mirea.project.user.service.UserService;
import ru.mirea.project.user.exception.UserDataAccessException;

import java.io.File;

public class OverviewController {
    @FXML
    private Label totalCarLabel;
    @FXML
    private Label availableCarLabel;
    @FXML
    private Label maintenanceCarLabel;
    @FXML
    private Label totalUserLabel;
    @FXML
    private Label totalRequestsLabel;
    @FXML
    private Label activeRequestsLabel;

    @FXML
    private Button downloadCarsButton;


    private final CarService carService =
            new CarService(new JdbcCarRepository());
    private final RequestService requestService =
            new RequestService(new JdbcRequestRepository(), new JdbcUserRepository(), new JdbcCarRepository());
    private final UserService userService =
            new UserService(new JdbcUserRepository());

    @FXML
    public void initialize() {
        loadStatistics();
    }

    @FXML
    private void loadStatistics(){
        try {
            CarService.CarStatistics statistic = carService.getStatistics();
            totalCarLabel.setText(String.valueOf(statistic.total()));
            availableCarLabel.setText(String.valueOf(statistic.avalible()));
            maintenanceCarLabel.setText(String.valueOf(statistic.maintanance()));

            RequestService.RentalStatistics rental = requestService.statistics();
            totalRequestsLabel.setText(String.valueOf(rental.total()));
            activeRequestsLabel.setText(String.valueOf(rental.active()));

            totalUserLabel.setText(
                    String.valueOf(userService.getAllUsers().size())
            );

        } catch (DataAccessException e) {
            totalCarLabel.setText("—");
            availableCarLabel.setText("—");
            maintenanceCarLabel.setText("—");

            totalRequestsLabel.setText("—");
            activeRequestsLabel.setText("—");



            new Alert(
                    Alert.AlertType.ERROR,
                    "Ошибка работы бд."
            ).showAndWait();
        }
    }

    @FXML
    private void handleRefreshStatistic() {
        loadStatistics();
    }

    @FXML
    private void handleDownloadCars() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Сохранить таблицу cars");
        fileChooser.setInitialFileName("cars.csv");

        FileChooser.ExtensionFilter extensionFilter =
            new FileChooser.ExtensionFilter("CSV файлы (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extensionFilter);


        File file = fileChooser.showSaveDialog(
                downloadCarsButton.getScene().getWindow()
        );

        if (file != null) {
            try {
                carService.exportCarsToCsv(file);

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "Файл успешно сохранён!"
                ).showAndWait();

            } catch (ExportException e) {
                new Alert(
                        Alert.AlertType.ERROR,
                        e.getMessage()
                ).showAndWait();
            }
        }
    }

    @FXML
    private void handleDownloadUsers() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Сохранить пользователей");
        fileChooser.setInitialFileName("users.csv");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV файлы", "*.csv")
        );

        File file = fileChooser.showSaveDialog(
                downloadCarsButton.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        try {
            userService.exportToCsv(
                    userService.getAllUsers(),
                    file.toPath()
            );

            new Alert(
                    Alert.AlertType.INFORMATION,
                    "Пользователи успешно экспортированы!"
            ).showAndWait();

        } catch (RuntimeException e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    "Не удалось экспортировать пользователей: " + e.getMessage()
            ).showAndWait();
        }
    }

    @FXML
    private void handleDownloadRequests() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить заявки");
        fileChooser.setInitialFileName("rentals.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файлы (*.csv)", "*.csv"));

        File file = fileChooser.showSaveDialog(downloadCarsButton.getScene().getWindow());
        if (file != null) {
            try {
                requestService.exportCsv(requestService.getAll(), file);
                new Alert(Alert.AlertType.INFORMATION, "Файл успешно сохранён!").showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Не удалось экспортировать заявки: " + e.getMessage()).showAndWait();
            }
        }
    }
}