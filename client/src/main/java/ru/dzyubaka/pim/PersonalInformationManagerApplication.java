package ru.dzyubaka.pim;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;

public class PersonalInformationManagerApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TextField username = new TextField();
        username.setPromptText("Username");
        TextField password = new TextField();
        password.setPromptText("Password");
        Button authenticate = new Button("Authenticate");
        authenticate.setMaxWidth(Double.MAX_VALUE);
        authenticate.setOnAction(event -> {
            try (HttpClient client = HttpClient.newHttpClient()) {
                String base64 = Base64.getEncoder().encodeToString((username.getText() + ':' + password.getText()).getBytes());
                HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/albums"))
                        .header("Authorization", "Basic " + base64).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 401) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Unauthorized");
                    alert.show();
                } else {
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
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        primaryStage.setScene(new Scene(new VBox(username, password, authenticate)));
        primaryStage.setTitle("Personal Information Manager");
        primaryStage.show();
    }

}
