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
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class PersonalInformationManagerApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Properties properties = load();
        if (properties == null) {
            TextField username = new TextField();
            username.setPromptText("Username");
            TextField password = new TextField();
            password.setPromptText("Password");
            Button authenticate = new Button("Authenticate");
            authenticate.setMaxWidth(Double.MAX_VALUE);
            authenticate.setOnAction(event -> {
                authenticate(primaryStage, username.getText(), password.getText());
                primaryStage.centerOnScreen();
            });
            primaryStage.setScene(new Scene(new VBox(username, password, authenticate)));
        } else {
            authenticate(primaryStage, (String) properties.get("username"), (String) properties.get("password"));
        }
        primaryStage.setTitle("Personal Information Manager");
        primaryStage.show();
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
    private static void authenticate(Stage stage, String username, String password) {
        HttpClient client = HttpClient.newHttpClient();
        store(username, password);
        String base64 = Base64.getEncoder().encodeToString((username + ':' + password).getBytes());
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
            stage.setScene(new Scene(listView, 640, 400));
        }
    }

}
