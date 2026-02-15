package ru.dzyubaka.pim.server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Band band;

    private String name;

    private int year;

    private LocalDateTime listenedAt;
}
