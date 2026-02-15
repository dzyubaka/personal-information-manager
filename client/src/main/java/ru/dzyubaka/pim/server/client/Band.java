package ru.dzyubaka.pim.server.client;

public record Band(long id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
