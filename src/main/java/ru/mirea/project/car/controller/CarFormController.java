package ru.mirea.project.car.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mirea.project.car.enums.BodyType;
import ru.mirea.project.car.enums.CarStatus;
import ru.mirea.project.car.enums.Transmission;
import ru.mirea.project.car.model.Car;
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.car.service.CarService;

public class CarFormController {

    CarService carService =
            new CarService(new JdbcCarRepository());

    Car editedCar;

    @FXML
    private TextField brandTextField;

    @FXML
    private TextField modelTextField;

    @FXML
    private ComboBox<Transmission> transmissionComboBox;

    @FXML
    private TextField yearTextField;

    @FXML
    private TextField licensePlateTextField;

    @FXML
    private ComboBox<BodyType> bodyTypeComboBox;

    @FXML
    private ComboBox<CarStatus> carStatusComboBox;

    @FXML
    private TextField mileageTextField;

    @FXML
    private Button submitButton;

    @FXML
    private Button closeButton;

    @FXML
    private Label errorLabel;

    public void setEditedCar (Car car) {
        this.editedCar = car;

        brandTextField.setText(editedCar.getBrand());
        modelTextField.setText(editedCar.getModel());
        transmissionComboBox.setValue(editedCar.getTransmission());
        yearTextField.setText(String.valueOf(editedCar.getYear()));
        licensePlateTextField.setText(editedCar.getLicensePlate());
        bodyTypeComboBox.setValue(editedCar.getBodyType());
        carStatusComboBox.setValue(editedCar.getStatus());
        mileageTextField.setText(String.valueOf(editedCar.getMileage()));
    }

    @FXML
    private void initialize() {
        transmissionComboBox.getItems().setAll(Transmission.values());
        bodyTypeComboBox.getItems().setAll(BodyType.values());
        carStatusComboBox.getItems().setAll(CarStatus.values());
    }


    @FXML
    private void handleSubmitButton() {
        String brand = brandTextField.getText();
        String model = modelTextField.getText();
        Transmission transmission = transmissionComboBox.getValue();
        Integer year = IntegerCheck("Неверный год", yearTextField.getText());
        if (year == null) return;
        String licensePlate = licensePlateTextField.getText();
        BodyType bodyType = bodyTypeComboBox.getValue();
        CarStatus carStatus = carStatusComboBox.getValue();
        Integer mileage = IntegerCheck("Неверный пробег", mileageTextField.getText());
        if (mileage == null) return;

        if (brand.isBlank() || model.isBlank() ||
                transmission == null || licensePlate.isBlank() ||
                bodyType == null || carStatus == null) {
            errorLabel.setText("Все поля обязательные.");
            return;
        }

        System.out.println("Введенные данные: " + brand + " " + model +
                " " + transmission + " " + year + " " + licensePlate + " " +
                bodyType + " " + carStatus + " " + mileage);


        try {

            if (editedCar == null ){
                System.out.println("Машина создаётся");
                Car car = new Car(
                        null,
                        brand,
                        model,
                        transmission,
                        year,
                        licensePlate,
                        bodyType,
                        carStatus,
                        mileage
                );

                carService.createCar(car);
            } else {
                System.out.println("Машина обновляется");
                Car car = new Car(
                        editedCar.getId(),
                        brand,
                        model,
                        transmission,
                        year,
                        licensePlate,
                        bodyType,
                        carStatus,
                        mileage
                );

                carService.updateCar(car);
            }

            handleClose();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Ошибка: " + e.getMessage());
        }

    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private Integer IntegerCheck(String message, String number) {
        try {
            int parseNumber = Integer.parseInt(number);
            return parseNumber;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            errorLabel.setText(message);
            return null;
        }
    }
}
