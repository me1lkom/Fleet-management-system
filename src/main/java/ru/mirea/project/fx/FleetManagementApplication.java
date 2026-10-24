package ru.mirea.project.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FleetManagementApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                getClass().getResource("/css/app.css").toExternalForm()
        );

        stage.setTitle("Fleet Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}