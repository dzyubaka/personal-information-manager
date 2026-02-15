package ru.dzyubaka.pim.server.client;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
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
                String authorization = "Basic " + Base64.getEncoder().encodeToString((username + ':' + password).getBytes());
                HttpRequest bandsRequest = HttpRequest.newBuilder(URI.create("http://localhost:8080/bands"))
                        .header("Authorization", authorization).build();
                HttpResponse<String> bandsResponse = client.send(bandsRequest, HttpResponse.BodyHandlers.ofString());
                if (bandsResponse.statusCode() == 401) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Unauthorized");
                    alert.showAndWait();
                } else {
                    ObjectMapper mapper = new JsonMapper();
                    List<Band> bands = mapper.readValue(bandsResponse.body(), new TypeReference<>() {});
                    ListView<Band> bandsListView = new ListView<>(FXCollections.observableList(bands));
                    Scene scene = new Scene(bandsListView, 640, 400);
                    bandsListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Band> observable, Band oldValue, Band newValue) -> {
                        try {
                            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/bands/" + newValue.id()))
                                    .header("Authorization", authorization).build();
                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                            if (bandsResponse.statusCode() == 401) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText("Unauthorized");
                                alert.showAndWait();
                            } else {
                                Band band = mapper.readValue(response.body(), new TypeReference<>() {});
                                ListView<Album> listView = new ListView<>(FXCollections.observableList(band.albums()));
                                listView.setCellFactory(CheckBoxListCell.forListView(a -> new SimpleBooleanProperty(a.getListenedAt() != null)));
                                BorderPane root = new BorderPane();
                                Button button = new Button("Back");
                                button.setOnAction(event1 -> scene.setRoot(bandsListView));
                                root.setTop(new ToolBar(button));
                                root.setCenter(listView);
                                scene.setRoot(root);
                            }
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    primaryStage.setScene(scene);
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
