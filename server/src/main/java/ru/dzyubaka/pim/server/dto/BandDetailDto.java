package ru.dzyubaka.pim.server.dto;

import java.util.List;

public record BandDetailDto(Long id, String name, List<AlbumResponse> albums) {
}
