package ru.dzyubaka.pim.server.client;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class PersonalInformationManagerApplication extends Application {
    private static final Path PATH = Path.of("token.txt");
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new JsonMapper();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String token = loadToken();
        if (token == null) {
            showAuthScene(primaryStage);
        } else {
            showBandsScene(primaryStage, token);
        }
        primaryStage.setTitle("Personal Information Manager");
        primaryStage.show();
    }

    @SneakyThrows
    private static String loadToken() {
        return Files.exists(PATH) ? Files.readString(PATH) : null;
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
                saveToken(token);
                showBandsScene(stage, token);
            }
        });
        stage.setScene(new Scene(new VBox(username, password, authorizeButton)));
    }

    @SneakyThrows
    private static void saveToken(String token) {
        Files.writeString(PATH, token);
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

    @SneakyThrows
    private static void showBandsScene(Stage stage, String token) {
        HttpRequest bandsRequest = HttpRequest.newBuilder(URI.create("http://localhost:8080/bands"))
                .header("Authorization", "Bearer " + token).build();
        HttpResponse<String> bandsResponse = CLIENT.send(bandsRequest, HttpResponse.BodyHandlers.ofString());
        List<Band> bands = MAPPER.readValue(bandsResponse.body(), new TypeReference<>() {});
        ListView<Band> bandsListView = new ListView<>(FXCollections.observableList(bands));
        Scene scene = new Scene(bandsListView, 640, 400);
        bandsListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Band> observable, Band oldValue, Band newValue) ->
                showBandView(scene, newValue.id(), token, bandsListView));
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    @SneakyThrows
    private static void showBandView(Scene scene, long bandId, String token, Parent backView) {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/bands/" + bandId))
                .header("Authorization", "Bearer " + token).build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        Band band = MAPPER.readValue(response.body(), new TypeReference<>() {});
        for (Album album : band.albums()) {
            album.getListened().set(album.getListenedAt() != null);
            album.getListened().addListener((observable, oldValue, newValue) -> {
                album.setListenedAt(newValue ? LocalDateTime.now() : null);
                put(album, token);
            });
        }
        TableView<Album> tableView = new TableView<>(FXCollections.observableList(band.albums()));
        tableView.setEditable(true);
        TableColumn<Album, Boolean> listenedColumn = new TableColumn<>();
        listenedColumn.setCellValueFactory(a -> a.getValue().getListened());
        listenedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(listenedColumn));
        TableColumn<Album, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        TableColumn<Album, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().addAll(listenedColumn, yearColumn, nameColumn);
        BorderPane root = new BorderPane();
        Button button = new Button("Back");
        button.setOnAction(event -> scene.setRoot(backView));
        root.setTop(new ToolBar(button));
        root.setCenter(tableView);
        scene.setRoot(root);
    }

    @SneakyThrows
    private static void put(Album album, String token) {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/albums/" + album.getId()))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(album)))
                .build();
        CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
    }
}
