package ru.dzyubaka.pim.server.dto;

import java.time.LocalDateTime;

public record AlbumResponse(
        Long id,
        String band,
        String name,
        int year,
        LocalDateTime listenedAt) {
}
