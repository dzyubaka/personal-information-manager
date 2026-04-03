package ru.dzyubaka.pim.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ru.dzyubaka.pim.client.view.BandListView;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PersonalInformationManagerApplication extends Application {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String token = TokenStorage.load();
        if (token == null) {
            showAuthScene(primaryStage);
        } else {
            primaryStage.setScene(new Scene(new BandListView(primaryStage, token), 640, 480));
        }
        primaryStage.setTitle("Personal Information Manager");
        primaryStage.show();
    }

    private static void showAuthScene(Stage stage) {
        TextField username = new TextField("username");
        username.setPromptText("Username");
        TextField password = new TextField("password");
        password.setPromptText("Password");
        Button authorizeButton = new Button("Authorize");
        authorizeButton.setMaxWidth(Double.MAX_VALUE);
        authorizeButton.setOnAction(event -> {
            String token = auth(username.getText(), password.getText());
            if (token == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Unauthorized");
                alert.showAndWait();
            } else {
                TokenStorage.save(token);
                stage.setScene(new Scene(new BandListView(stage, token), 640, 480));
                stage.centerOnScreen();
            }
        });
        stage.setScene(new Scene(new VBox(username, password, authorizeButton)));
    }

    @SneakyThrows
    private static String auth(String username, String password) {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/auth"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("username=%s&password=%s".formatted(username, password)))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 401 ? null : response.body();
    }
}
