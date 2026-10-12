package ru.mirea.project.car.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import ru.mirea.project.car.enums.BodyType;
import ru.mirea.project.car.enums.CarStatus;
import ru.mirea.project.car.enums.Transmission;
import ru.mirea.project.car.model.Car;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import javafx.beans.property.ReadOnlyObjectWrapper;
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.car.service.CarService;


public class CarController {

    private final CarService carService =
            new CarService(new JdbcCarRepository());


    @FXML
    private TableView<Car> carsTable;

    @FXML
    private TableColumn<Car, Long> idColumn;
    @FXML
    private TableColumn<Car, String> brandColumn;
    @FXML
    private TableColumn<Car, String> modelColumn;
    @FXML
    private TableColumn<Car, Transmission> transmissionColumn;
    @FXML
    private TableColumn<Car, Integer> yearColumn;
    @FXML
    private TableColumn<Car, String> licensePlateColumn;
    @FXML
    private TableColumn<Car, BodyType> bodyTypeColumn;
    @FXML
    private TableColumn<Car, CarStatus> statusColumn;
    @FXML
    private TableColumn<Car, Integer> mileageColumn;


    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getId()
                )
        );

        brandColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getBrand()
                )
        );

        modelColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getModel()
                )
        );

        transmissionColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getTransmission()
                )
        );

        yearColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getYear()
                )
        );

        licensePlateColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getLicensePlate()
                )
        );

        bodyTypeColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getBodyType()
                )
        );

        statusColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getStatus()
                )
        );

        mileageColumn.setCellValueFactory(
                cellData -> new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getMileage()
                )
        );

        loadCars();
    }

    private void loadCars() {
        ObservableList<Car> rows =
            FXCollections.observableArrayList(carService.getAllCars());
        carsTable.setItems(rows);
    }

    @FXML
    private void refreshTable() {
        loadCars();
    }
}
