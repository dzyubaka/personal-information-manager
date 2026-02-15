package ru.dzyubaka.pim.server.client;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Album {
    private String name;
    private String band;
    private int year;
    private LocalDateTime listenedAt;

    @Override
    public String toString() {
        return band + " — " + name;
    }
}
