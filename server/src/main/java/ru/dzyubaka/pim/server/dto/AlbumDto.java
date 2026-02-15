package ru.dzyubaka.pim.server.dto;

import java.time.LocalDateTime;

public record AlbumDto(
        Long id,
        String band,
        String name,
        int year,
        LocalDateTime listenedAt) {
}
