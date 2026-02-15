package ru.dzyubaka.pim.client;

import java.util.List;

public record Band(long id, String name, List<Album> albums) {
    @Override
    public String toString() {
        return name;
    }
}
