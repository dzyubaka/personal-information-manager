package ru.dzyubaka.pim.server.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumRequest {

    private long bandId;

    private String name;

    private int year;

    private LocalDateTime listenedAt;
}
