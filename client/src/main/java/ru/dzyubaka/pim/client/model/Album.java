package ru.dzyubaka.pim.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Album {

    private long id;

    private long band;

    private Integer number;

    private int year;

    private String name;

    private LocalDateTime listenedAt;

    @JsonIgnore
    private final BooleanProperty listened = new SimpleBooleanProperty();
}
