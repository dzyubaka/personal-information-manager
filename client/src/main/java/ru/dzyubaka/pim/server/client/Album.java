package ru.dzyubaka.pim.server.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Album {
    private long id;
    private long band;
    private String name;
    private int year;
    private LocalDateTime listenedAt;

    @JsonIgnore
    private final BooleanProperty listened = new SimpleBooleanProperty();

    @Override
    public String toString() {
        return year + " — " + name;
    }
}
