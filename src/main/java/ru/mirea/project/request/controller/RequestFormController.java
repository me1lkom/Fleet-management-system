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
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.model.Request;
import ru.mirea.project.request.repository.JdbcRequestRepository;
import ru.mirea.project.request.service.RequestService;
import ru.mirea.project.user.repository.JdbcUserRepository;

import java.time.LocalDate;

public class RequestFormController {

    private final RequestService service = new RequestService(
            new JdbcRequestRepository(), new JdbcUserRepository(), new JdbcCarRepository());

    @FXML private TextField userIdField;
    @FXML private TextField carIdField;
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
    }

    public void setEdited(Request request) {
        this.edited = request;
        if (request == null) return;
        userIdField.setText(String.valueOf(request.getUserId()));
        carIdField.setText(String.valueOf(request.getCarId()));
        startField.setText(request.getStartDate().toString());
        endField.setText(request.getEndDate().toString());
        priceField.setText(String.valueOf(request.getMonthlyPrice()));
        statusBox.setValue(request.getStatus());
        notesArea.setText(request.getNotes());
    }

    @FXML
    private void handleSubmit() {
        try {
            Long userId = Long.parseLong(userIdField.getText().trim());
            Long carId = Long.parseLong(carIdField.getText().trim());
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());
            Integer price = Integer.parseInt(priceField.getText().trim());
            RequestStatus status = statusBox.getValue();
            String notes = notesArea.getText();

            if (edited == null) {
                Request created = service.createRental(userId, carId, start, end, price, notes);
                if (status != RequestStatus.NEW) {
                    service.changeStatus(created.getId(), status);
                }
            } else {
                edited.setUserId(userId);
                edited.setCarId(carId);
                edited.setPeriod(start, end);
                edited.setMonthlyPrice(price);
                edited.setStatus(status);
                edited.setNotes(notes);
                service.updateRental(edited);
            }
            handleClose();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
