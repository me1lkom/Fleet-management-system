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
        } catch (DataAccessException e) {
            totalCarLabel.setText("—");
            availableCarLabel.setText("—");
            maintenanceCarLabel.setText("—");

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

    }

    @FXML
    private void handleDownloadRequests() {

    }
}
