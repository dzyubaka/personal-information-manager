package ru.dzyubaka.pim;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PersonalInformationManagerApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/albums")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Album> albums = new JsonMapper().readValue(response.body(), new TypeReference<>() {
            });
            ListView<Album> listView = new ListView<>(FXCollections.observableList(albums));
            listView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Album> observable, Album oldValue, Album newValue) -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                if (newValue.getListenedAt() == null) {
                    alert.setHeaderText("%s — %s (%d) not listened".formatted(newValue.getBand(), newValue.getName(), newValue.getYear()));
                } else {
                    alert.setHeaderText("%s — %s (%d) listened %s".formatted(newValue.getBand(), newValue.getName(), newValue.getYear(), newValue.getListenedAt()));
                }
                alert.show();
            });
            listView.setCellFactory(CheckBoxListCell.forListView(album -> new SimpleBooleanProperty(album.getListenedAt() != null)));
            primaryStage.setScene(new Scene(listView, 640, 400));
            primaryStage.setTitle("Personal Information Manager");
            primaryStage.show();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
