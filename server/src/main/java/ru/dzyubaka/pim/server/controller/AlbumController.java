package ru.dzyubaka.pim.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.dzyubaka.pim.server.dto.AlbumResponse;
import ru.dzyubaka.pim.server.dto.AlbumRequest;
import ru.dzyubaka.pim.server.service.AlbumService;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping
    public Iterable<AlbumResponse> findAll() {
        return albumService.findAll();
    }

    @PutMapping("/{id}")
    public void put(@PathVariable long id, @RequestBody AlbumRequest albumRequest) {
        albumService.update(id, albumRequest);
    }
}
