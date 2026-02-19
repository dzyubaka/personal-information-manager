package ru.dzyubaka.pim.server.dto;

import java.time.LocalDateTime;

public record AlbumResponse(
        long id,
        long band,
        String name,
        int year,
        LocalDateTime listenedAt) {
}
