package ru.dzyubaka.pim.client.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ru.dzyubaka.pim.client.model.Band;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BandsPane extends BorderPane {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    public BandsPane(Stage stage, String token) {
        HttpRequest bandsRequest = HttpRequest.newBuilder(URI.create("http://localhost:8080/bands"))
                .header("Authorization", "Bearer " + token).build();
        HttpResponse<String> bandsResponse = CLIENT.send(bandsRequest, HttpResponse.BodyHandlers.ofString());
        ObservableList<Band> bands = FXCollections.observableList(MAPPER.readValue(bandsResponse.body(), new TypeReference<>() {
        }));
        ListView<Band> listView = new ListView<>(bands);
        Button button = new Button("Import");
        button.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Enter band name");
            dialog.showAndWait().ifPresent(s -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/bands/import/" + s))
                            .header("Authorization", "Bearer " + token).POST(HttpRequest.BodyPublishers.noBody()).build();
                    HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                    bands.add(MAPPER.readValue(response.body(), Band.class));
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        setTop(new ToolBar(button));
        setCenter(listView);
        listView.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends Band> observable, Band oldValue, Band newValue) ->
                        stage.getScene().setRoot(new AlbumsPane(newValue.id(), token, e ->
                                stage.getScene().setRoot(this))));
    }
}
