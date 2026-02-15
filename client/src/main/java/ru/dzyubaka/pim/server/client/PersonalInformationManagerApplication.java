package ru.dzyubaka.pim.server.client;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
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
    @SneakyThrows
    public void start(Stage primaryStage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        Button authorizeButton = new Button("Authorize");
        authorizeButton.setMaxWidth(Double.MAX_VALUE);
        authorizeButton.setOnAction(event -> {
            try {
                String username = usernameField.getText();
                String password = passwordField.getText();
                HttpClient client = HttpClient.newHttpClient();
                String base64 = Base64.getEncoder().encodeToString((username + ':' + password).getBytes());
                HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/bands"))
                        .header("Authorization", "Basic " + base64).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 401) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Unauthorized");
                    alert.showAndWait();
                } else {
                    List<Band> bands = new JsonMapper().readValue(response.body(), new TypeReference<>() {});
                    ListView<Band> listView = new ListView<>(FXCollections.observableList(bands));
                    primaryStage.setScene(new Scene(listView, 640, 400));
                    primaryStage.centerOnScreen();
                }
            } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
            }
        });
        primaryStage.setScene(new Scene(new VBox(usernameField, passwordField, authorizeButton)));
        primaryStage.setTitle("Personal Information Manager");
        primaryStage.show();
    }

}
