package ru.dzyubaka.pim.server;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class YandexMusicAlbumFetcher {

    private static final RestClient CLIENT = RestClient.create("https://api.music.yandex.ru");

    @Value("${yandex-music-cookie}")
    private String COOKIE;

    @SneakyThrows
    public Map<String, Integer> getAlbums(String artistName) {
        JsonNode studioAlbums = getAlbums(artistName, true);
        if (studioAlbums.isEmpty()) {
            studioAlbums = getAlbums(artistName, false);
        }
        return studioAlbums
                .valueStream()
                .collect(Collectors.toMap(n -> n.get("title").stringValue(), n -> n.get("year").intValue()));
    }

    @SneakyThrows
    private JsonNode getAlbums(String artistName, boolean studio) {
        String uri = "/artists/" + getArtistId(artistName);
        if (studio) {
            uri += "/discography-albums";
        }
        return CLIENT.get()
                .uri(uri)
                .header("Cookie", COOKIE)
                .retrieve()
                .body(JsonNode.class)
                .get("result")
                .get("albums");
    }

    @SneakyThrows
    private int getArtistId(String artistName) {
        return CLIENT.get()
                .uri("/search?text=" + artistName + "&page=0&type=artist")
                .header("Cookie", COOKIE)
                .retrieve()
                .body(JsonNode.class)
                .get("result")
                .get("artists")
                .get("results")
                .get(0)
                .get("id")
                .intValue();
    }
}
