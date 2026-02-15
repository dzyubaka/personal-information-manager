package ru.dzyubaka.pim.client;

public record Band(long id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
