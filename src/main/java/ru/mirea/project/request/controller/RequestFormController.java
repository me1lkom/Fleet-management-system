package ru.mirea.project.request.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ru.mirea.project.car.model.Car;
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.exception.InvalidRequestException;
import ru.mirea.project.request.exception.RequestDataAccessException;
import ru.mirea.project.request.model.Request;
import ru.mirea.project.request.repository.JdbcRequestRepository;
import ru.mirea.project.request.service.RequestService;
import ru.mirea.project.user.model.User;
import ru.mirea.project.user.repository.JdbcUserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RequestFormController {

    private final RequestService service = new RequestService(
            new JdbcRequestRepository(), new JdbcUserRepository(), new JdbcCarRepository());
    private final JdbcUserRepository userRepository = new JdbcUserRepository();
    private final JdbcCarRepository carRepository = new JdbcCarRepository();

    @FXML private ComboBox<User> userBox;
    @FXML private ComboBox<Car> carBox;
    @FXML private TextField startField;
    @FXML private TextField endField;
    @FXML private TextField priceField;
    @FXML private ComboBox<RequestStatus> statusBox;
    @FXML private TextArea notesArea;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;
    @FXML private Button closeButton;

    private Request edited;

    @FXML
    public void initialize() {
        statusBox.setItems(FXCollections.observableArrayList(RequestStatus.values()));
        statusBox.setValue(RequestStatus.NEW);

        userBox.setConverter(new StringConverter<>() {
            @Override public String toString(User user) {
                return user == null ? "" : user.getId() + " — " + user.getLastName() + " " + user.getFirstName();
            }
            @Override public User fromString(String s) { return null; }
        });
        carBox.setConverter(new StringConverter<>() {
            @Override public String toString(Car car) {
                return car == null ? "" : car.getId() + " — " + car.getBrand() + " " + car.getModel() + " (" + car.getLicensePlate() + ")";
            }
            @Override public Car fromString(String s) { return null; }
        });

        try {
            List<User> users = userRepository.findAll();
            List<Car> cars = carRepository.findAll();
            userBox.setItems(FXCollections.observableArrayList(users));
            carBox.setItems(FXCollections.observableArrayList(cars));
        } catch (Exception e) {
            errorLabel.setText("Не удалось загрузить списки арендаторов и автомобилей.");
        }
    }

    public void setEdited(Request request) {
        this.edited = request;
        if (request == null) return;
        selectUser(request.getUserId());
        selectCar(request.getCarId());
        startField.setText(request.getStartDate().toString());
        endField.setText(request.getEndDate().toString());
        priceField.setText(String.valueOf(request.getMonthlyPrice()));
        statusBox.setValue(request.getStatus());
        notesArea.setText(request.getNotes());
    }

    @FXML
    private void handleSubmit() {
        errorLabel.setText("");
        try {
            User user = userBox.getValue();
            Car car = carBox.getValue();
            if (user == null) {
                errorLabel.setText("Выберите арендатора из списка.");
                return;
            }
            if (car == null) {
                errorLabel.setText("Выберите автомобиль из списка.");
                return;
            }
            LocalDate start = parseDate(startField.getText(), "дата начала");
            LocalDate end = parseDate(endField.getText(), "дата окончания");
            Integer price = parsePrice(priceField.getText());
            RequestStatus status = statusBox.getValue();
            if (status == null) status = RequestStatus.NEW;
            String notes = notesArea.getText();

            if (edited == null) {
                Request created = service.createRental(user.getId(), car.getId(), start, end, price, notes);
                if (status != RequestStatus.NEW) {
                    service.changeStatus(created.getId(), status);
                }
            } else {
                edited.setUserId(user.getId());
                edited.setCarId(car.getId());
                edited.setPeriod(start, end);
                edited.setMonthlyPrice(price);
                edited.setStatus(status);
                edited.setNotes(notes);
                service.updateRental(edited);
            }
            handleClose();
        } catch (InvalidRequestException | IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        } catch (RequestDataAccessException e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка базы данных: " + e.getMessage()).showAndWait();
        } catch (Exception e) {
            errorLabel.setText("Проверьте введённые данные.");
        }
    }

    private LocalDate parseDate(String text, String field) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Заполните " + field + ".");
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты (" + field + "). Нужно ГГГГ-ММ-ДД.");
        }
    }

    private Integer parsePrice(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Введите цену в месяц.");
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Цена должна быть целым числом.");
        }
    }

    private void selectUser(Long id) {
        for (User user : userBox.getItems()) {
            if (user.getId().equals(id)) {
                userBox.setValue(user);
                return;
            }
        }
    }

    private void selectCar(Long id) {
        for (Car car : carBox.getItems()) {
            if (car.getId().equals(id)) {
                carBox.setValue(car);
                return;
            }
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}