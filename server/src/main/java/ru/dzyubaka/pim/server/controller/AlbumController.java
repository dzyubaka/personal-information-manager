package ru.dzyubaka.pim.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dzyubaka.pim.server.dto.AlbumDto;
import ru.dzyubaka.pim.server.service.AlbumService;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping
    public Iterable<AlbumDto> findAll() {
        return albumService.findAll();
    }
}
