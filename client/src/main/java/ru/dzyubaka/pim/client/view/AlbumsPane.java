package ru.dzyubaka.pim.client.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import lombok.SneakyThrows;
import ru.dzyubaka.pim.client.model.Album;
import ru.dzyubaka.pim.client.model.Band;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class AlbumsPane extends BorderPane {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    public AlbumsPane(long bandId, String token, EventHandler<ActionEvent> onBack) {
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
        TableColumn<Album, Integer> numberColumn = new TableColumn<>("#");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        TableColumn<Album, Boolean> listenedColumn = new TableColumn<>("✓");
        listenedColumn.setCellValueFactory(a -> a.getValue().getListened());
        listenedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(listenedColumn));
        TableColumn<Album, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Album, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        tableView.getColumns().addAll(numberColumn, listenedColumn, nameColumn, yearColumn);
        Button button = new Button("Back");
        button.setOnAction(onBack);
        setTop(new ToolBar(button));
        setCenter(tableView);
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
