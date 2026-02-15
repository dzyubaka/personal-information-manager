package ru.dzyubaka.pim.server.client;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Base64;
import java.util.List;

public class PersonalInformationManagerApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    @SneakyThrows
    public void start(Stage primaryStage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        Button authorizeButton = new Button("Authorize");
        authorizeButton.setMaxWidth(Double.MAX_VALUE);
        authorizeButton.setOnAction(new EventHandler<>() {
            @Override
            @SneakyThrows
            public void handle(ActionEvent event) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                HttpClient client = HttpClient.newHttpClient();
                String base64 = Base64.getEncoder().encodeToString((username + ':' + password).getBytes());
                HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/albums"))
                        .header("Authorization", "Basic " + base64).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 401) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Unauthorized");
                    alert.showAndWait();
                } else {
                    List<Album> albums = new JsonMapper().readValue(response.body(), new TypeReference<>() {
                    });
                    ListView<Album> listView = new ListView<>(FXCollections.observableList(albums));
                    listView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Album> observable, Album oldValue, Album newValue) -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        if (newValue.getListenedAt() == null) {
                            alert.setHeaderText("%s — %s (%d) not listened".formatted(newValue.getBand(), newValue.getName(), newValue.getYear()));
                        } else {
                            String formatted = newValue.getListenedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
                            alert.setHeaderText("%s — %s (%d) listened %s".formatted(newValue.getBand(), newValue.getName(), newValue.getYear(), formatted));
                        }
                        alert.showAndWait();
                    });
                    listView.setCellFactory(CheckBoxListCell.forListView(album -> new SimpleBooleanProperty(album.getListenedAt() != null)));
                    primaryStage.setScene(new Scene(listView, 640, 400));
                    primaryStage.centerOnScreen();
                }
            }
        });
        primaryStage.setScene(new Scene(new VBox(usernameField, passwordField, authorizeButton)));
        primaryStage.setTitle("Personal Information Manager");
        primaryStage.show();
    }

}
