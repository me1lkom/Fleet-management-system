package ru.mirea.project.request.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.mirea.project.car.repository.JdbcCarRepository;
import ru.mirea.project.request.enums.RequestStatus;
import ru.mirea.project.request.exception.RequestDataAccessException;
import ru.mirea.project.request.model.Request;
import ru.mirea.project.request.repository.JdbcRequestRepository;
import ru.mirea.project.request.service.RequestService;
import ru.mirea.project.user.repository.JdbcUserRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class RequestController {

    private final RequestService service = new RequestService(
            new JdbcRequestRepository(), new JdbcUserRepository(), new JdbcCarRepository());

    @FXML private TableView<Request> requestsTable;
    @FXML private TableColumn<Request, Long> idColumn;
    @FXML private TableColumn<Request, Long> userColumn;
    @FXML private TableColumn<Request, Long> carColumn;
    @FXML private TableColumn<Request, String> startColumn;
    @FXML private TableColumn<Request, String> endColumn;
    @FXML private TableColumn<Request, Long> daysColumn;
    @FXML private TableColumn<Request, Integer> priceColumn;
    @FXML private TableColumn<Request, RequestStatus> statusColumn;
    @FXML private TableColumn<Request, String> notesColumn;
    @FXML private TextField searchTextField;
    @FXML private ComboBox<RequestStatus> statusFilter;
    @FXML private TextField userFilter;
    @FXML private Button exportButton;
    @FXML private Label statTotal;
    @FXML private Label statActive;
    @FXML private Label statApproved;
    @FXML private Label statAvg;
    @FXML private Label statLong;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        userColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getUserId()));
        carColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCarId()));
        startColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(String.valueOf(c.getValue().getStartDate())));
        endColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(String.valueOf(c.getValue().getEndDate())));
        daysColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().rentalDays()));
        priceColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getMonthlyPrice()));
        statusColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStatus()));
        notesColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNotes()));
        statusFilter.setItems(FXCollections.observableArrayList(RequestStatus.values()));
        refreshTable();
    }

    @FXML private void refreshTable() { show(service.getAll()); loadStats(); }
    @FXML private void handleSearch() { show(service.searchByNotes(searchTextField.getText() == null ? "" : searchTextField.getText())); }
    @FXML private void handleFilter() {
        RequestStatus status = statusFilter.getValue();
        String userText = userFilter.getText();
        if (status != null) show(service.filterByStatus(status));
        else if (userText != null && !userText.isBlank()) show(service.filterByUser(Long.parseLong(userText.trim())));
        else refreshTable();
    }
    @FXML private void handleReset() { searchTextField.clear(); userFilter.clear(); statusFilter.setValue(null); refreshTable(); }
    @FXML private void sortDateAsc() { show(service.sortByStartDate(true)); }
    @FXML private void sortDateDesc() { show(service.sortByStartDate(false)); }
    @FXML private void sortPriceAsc() { show(service.sortByMonthlyPrice(true)); }
    @FXML private void sortPriceDesc() { show(service.sortByMonthlyPrice(false)); }

    @FXML
    private void handleOpenAddForm() { openForm(null); }
    @FXML
    private void handleOpenChangeForm() {
        Request selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert(Alert.AlertType.ERROR, "Выберите заявку."); return; }
        openForm(selected);
    }

    @FXML
    private void handleDelete() {
        Request selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert(Alert.AlertType.ERROR, "Выберите заявку."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить заявку #" + selected.getId() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            service.delete(selected.getId());
            refreshTable();
        }
    }

    @FXML
    private void handleExport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранить заявки");
        chooser.setInitialFileName("rentals.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = chooser.showSaveDialog(exportButton.getScene().getWindow());
        if (file != null) {
            service.exportCsv(requestsTable.getItems(), file);
            alert(Alert.AlertType.INFORMATION, "Файл сохранён: " + file.getName());
        }
    }

    private void openForm(Request edited) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forms/request-form.fxml"));
            Parent root = loader.load();
            RequestFormController form = loader.getController();
            form.setEdited(edited);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(edited == null ? "Новая аренда" : "Изменить аренду");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Не удалось открыть форму аренды.");
        }
    }

    private void show(List<Request> requests) {
        requestsTable.setItems(FXCollections.observableArrayList(requests));
    }

    private void loadStats() {
        try {
            RequestService.RentalStatistics s = service.statistics();
            statTotal.setText("Всего: " + s.total());
            statActive.setText("Активные: " + s.active());
            statApproved.setText("Одобренные: " + s.approved());
            statAvg.setText("Средняя цена: " + Math.round(s.avgPrice()));
            statLong.setText("От 90 дней: " + s.longRentals());
        } catch (RequestDataAccessException e) {
            statTotal.setText("Всего: —");
        }
    }

    private void alert(Alert.AlertType type, String text) {
        new Alert(type, text).showAndWait();
    }
}
