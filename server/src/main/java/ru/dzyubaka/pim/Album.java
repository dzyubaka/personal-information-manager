package ru.dzyubaka.pim;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Album {
    @Id
    private String name;
    private String band;
    private int year;
    private LocalDateTime listenedAt;
}
