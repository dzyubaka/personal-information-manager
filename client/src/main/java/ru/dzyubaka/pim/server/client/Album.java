package ru.dzyubaka.pim.server.client;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Album {
    private String name;
    private String band;
    private int year;
    private LocalDateTime listenedAt;

    public BooleanProperty listenedProperty() {
        return new SimpleBooleanProperty(listenedAt != null) {
            @Override
            public void set(boolean newValue) {
                listenedAt = newValue ? LocalDateTime.now() : null;
                super.set(newValue);
            }
        };
    }

    @Override
    public String toString() {
        return year + " — " + name;
    }
}
