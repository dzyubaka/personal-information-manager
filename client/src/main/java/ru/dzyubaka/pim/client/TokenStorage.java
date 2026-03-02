package ru.dzyubaka.pim.client;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public class TokenStorage {
    private static final Path PATH = Path.of("token.txt");

    @SneakyThrows
    static void save(String token) {
        Files.writeString(PATH, token);
    }

    @SneakyThrows
    static String load() {
        return Files.exists(PATH) ? Files.readString(PATH) : null;
    }
}
