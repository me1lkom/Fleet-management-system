package ru.mirea.project.car.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.mirea.project.car.enums.BodyType;
import ru.mirea.project.car.enums.CarStatus;
import ru.mirea.project.car.enums.Transmission;
import ru.mirea.project.car.exception.CarNotFoundException;
import ru.mirea.project.car.exception.DataAccessException;
import ru.mirea.project.car.exception.ExportException;
import ru.mirea.project.car.model.Car;

import javafx.beans.property.ReadOnlyObjectWrapper;
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.car.service.CarService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


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
    private ComboBox<String> searchFieldComboBox;
    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<BodyType> filterBodyTypeComboBox;
    @FXML
    private ComboBox<CarStatus> filterStatusColumnComboBox;

    @FXML
    private Button downloadCarsButton;

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

        filterBodyTypeComboBox.getItems().setAll(BodyType.values());
        filterStatusColumnComboBox.getItems().setAll(CarStatus.values());
        loadCars();
    }

    private void loadCars() {
        try{
            updateRows(carService.getAllCars());
        } catch (DataAccessException e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    "Не удалось загрузить автомобиль. Ошибка работы бд."
            ).showAndWait();
        };

    }

    @FXML
    private void refreshTable() {
        loadCars();
    }

    @FXML
    private void handleOpenAddForm(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/forms/car-form.fxml")
            );

            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass().getResource("/css/app.css").toExternalForm()
            );

            Stage modalStage = new Stage();
            modalStage.setTitle("Add car form");

            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage parentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(parentStage);

            modalStage.setScene(scene);

            modalStage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    "Не удалось открыть форму автомобиля."
            ).showAndWait();
        }
    }

    @FXML
    private void handleOpenChangeForm(ActionEvent event) {

        Car selectedCar = carsTable.getSelectionModel().getSelectedItem();

        System.out.println(selectedCar);

        if (selectedCar == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText("Ошибка! Не выбрана ни одно авто.");

            alert.showAndWait();
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/forms/car-form.fxml")
            );

            Parent root = loader.load();

            CarFormController formController = loader.getController();
            formController.setEditedCar(selectedCar);

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass().getResource("/css/app.css").toExternalForm()
            );

            Stage modalStage = new Stage();
            modalStage.setTitle("Change car form");

            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage parentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(parentStage);

            modalStage.setScene(scene);

            modalStage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    "Не удалось открыть форму автомобиля."
            ).showAndWait();
        }
    }

    @FXML
    private void handleDelete() {
        Car selectedCar = carsTable.getSelectionModel().getSelectedItem();

        if (selectedCar == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText("Ошибка! Не выбрана ни одно авто.");

            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Вы уверены, что хотите удалить " + selectedCar.getId() + " "
                + selectedCar.getBrand() + " " + selectedCar.getModel() + "?");

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                carService.deleteCarById(selectedCar.getId());
                refreshTable();
            } catch (CarNotFoundException e) {
                new Alert(
                        Alert.AlertType.ERROR,
                        e.getMessage()
                ).showAndWait();
            } catch (DataAccessException e) {
                new Alert(
                        Alert.AlertType.ERROR,
                        "Не удалось удалить автомобиль. Ошибка работы с базой данных."
                ).showAndWait();
            }
            refreshTable();
        } else {
            System.out.println("Отменил");
        }
    }

    @FXML
    private void handleSearch(){
        String field = searchFieldComboBox.getValue();
        String value = searchTextField.getText();

        if (value.isBlank()) {
            loadCars();
            return;
        }

        if (field == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Параметр поиска");
            alert.setHeaderText("Пожалуйста введите параметр поиска.");
            alert.showAndWait();
            return;
        }

        try {
            switch (field) {
                case "Марка":
                    updateRows(carService.getCarsByBrand(value));
                    break;
                case "Модель":
                    updateRows(carService.getCarsByModel(value));
                    break;
                case "Госномер":
                    Car car = carService.getCarByLicensePlate(value);
                    if (car == null) {
                        List<Car> resultLicensePlate = List.of();
                        updateRows(resultLicensePlate);
                    } else {
                        List<Car> resultLicensePlate = List.of(car);
                        updateRows(resultLicensePlate);
                    }
                    break;
            }
        } catch (DataAccessException e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    "Ошибка работы с базой данных."
            ).showAndWait();
        }
    }

    private void updateRows(List<Car> cars) {
        ObservableList<Car> rows =
                FXCollections.observableArrayList(cars);
        carsTable.setItems(rows);
    }

    @FXML
    private void handleApplyFilter(){
        BodyType bodyType = filterBodyTypeComboBox.getValue();
        CarStatus carStatus = filterStatusColumnComboBox.getValue();

        if (bodyType == null && carStatus == null) {
            loadCars();
            return;
        }
        try{
            if (carStatus == null) {
                updateRows(carService.filterCarsByBodyType(bodyType));
            } else if (bodyType == null ) {
                updateRows(carService.filterCarsByStatus(carStatus));
            } else {
                updateRows(carService.filterCarsByStatusANDBodyType(carStatus, bodyType));
            }
        } catch (DataAccessException e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    "Ошибка работы с базой данных."
            ).showAndWait();
        }
    }

    @FXML
    private void handleResetFilter() {
        filterBodyTypeComboBox.setValue(null);
        filterStatusColumnComboBox.setValue(null);
        loadCars();
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
                carService.exportCarsToCsv(carsTable.getItems(), file);

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
}
