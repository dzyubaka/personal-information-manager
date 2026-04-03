package ru.dzyubaka.pim.server.dto;

import java.time.LocalDateTime;

public record AlbumResponse(
        long id,
        long band,
        Integer number,
        int year,
        String name,
        LocalDateTime listenedAt) {
}
