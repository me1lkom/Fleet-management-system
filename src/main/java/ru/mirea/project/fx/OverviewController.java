package ru.mirea.project.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.car.service.CarService;

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


    private final CarService carService =
            new CarService(new JdbcCarRepository());

    @FXML
    public void initialize() {
        loadStatistics();
    }

    @FXML
    private void loadStatistics(){
        CarService.CarStatistics statistic = carService.getStatistics();
        totalCarLabel.setText(String.valueOf(statistic.total()));
        availableCarLabel.setText(String.valueOf(statistic.avalible()));
        maintenanceCarLabel.setText(String.valueOf(statistic.maintanance()));
    }

    @FXML
    private void handleRefreshStatistic() {
        loadStatistics();
    }

    @FXML
    private void handleDownloadCars() {

    }

    @FXML
    private void handleDownloadUsers() {

    }

    @FXML
    private void handleDownloadRequests() {

    }
}
