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
import lombok.SneakyThrows;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class PersonalInformationManagerApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    @SneakyThrows
    public void start(Stage primaryStage) {
        Properties properties = load();
        if (properties == null) {
            primaryStage.setScene(createAuthorizationScene(primaryStage));
        } else {
            String username = (String) properties.get("username");
            String password = (String) properties.get("password");
            HttpResponse<String> response = authorize(username, password);
            if (response.statusCode() == 401) {
                primaryStage.setScene(createAuthorizationScene(primaryStage));
                showUnauthorizedError();
                Files.delete(Path.of(".properties"));
            } else {
                store(username, password);
                List<Album> albums = new JsonMapper().readValue(response.body(), new TypeReference<>() {});
                primaryStage.setScene(createAlbumsScene(albums));
            }
        }
        primaryStage.setTitle("Personal Information Manager");
        primaryStage.show();
    }

    private Scene createAuthorizationScene(Stage stage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        Button authorizeButton = new Button("Authorize");
        authorizeButton.setMaxWidth(Double.MAX_VALUE);
        authorizeButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            HttpResponse<String> response = authorize(username, password);
            if (response.statusCode() == 401) {
                showUnauthorizedError();
            } else {
                store(username, password);
                List<Album> albums = new JsonMapper().readValue(response.body(), new TypeReference<>() {});
                stage.setScene(createAlbumsScene(albums));
                stage.centerOnScreen();
            }
        });
        return new Scene(new VBox(usernameField, passwordField, authorizeButton));
    }

    private Scene createAlbumsScene(List<Album> albums) {
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
        return new Scene(listView, 640, 400);
    }

    private void showUnauthorizedError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Unauthorized");
        alert.showAndWait();
    }

    @SneakyThrows
    private static void store(String username, String password) {
        Properties properties = new Properties();
        properties.put("username", username);
        properties.put("password", password);
        properties.store(Files.newOutputStream(Path.of(".properties")), null);
    }

    @SneakyThrows
    private static Properties load() {
        Path path = Path.of(".properties");
        if (Files.exists(path)) {
            Properties properties = new Properties();
            properties.load(Files.newInputStream(path));
            return properties;
        } else return null;
    }

    @SneakyThrows
    private static HttpResponse<String> authorize(String username, String password) {
        HttpClient client = HttpClient.newHttpClient();
        String base64 = Base64.getEncoder().encodeToString((username + ':' + password).getBytes());
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/albums"))
                .header("Authorization", "Basic " + base64).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

}
