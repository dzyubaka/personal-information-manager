package ru.dzyubaka.pim.client.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ru.dzyubaka.pim.client.model.Band;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BandListView extends ListView<Band> {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    public BandListView(Stage stage, String token) {
        HttpRequest bandsRequest = HttpRequest.newBuilder(URI.create("http://localhost:8080/bands"))
                .header("Authorization", "Bearer " + token).build();
        HttpResponse<String> bandsResponse = CLIENT.send(bandsRequest, HttpResponse.BodyHandlers.ofString());
        ObservableList<Band> bands = FXCollections.observableList(MAPPER.readValue(bandsResponse.body(), new TypeReference<>() {
        }));
        setItems(bands);
        getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends Band> observable, Band oldValue, Band newValue) ->
                        stage.getScene().setRoot(new AlbumsPane(newValue.id(), token, e ->
                                stage.getScene().setRoot(this))));
    }
}
