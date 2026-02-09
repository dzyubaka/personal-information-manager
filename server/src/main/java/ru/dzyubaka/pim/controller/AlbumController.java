package ru.dzyubaka.pim.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dzyubaka.pim.model.Album;
import ru.dzyubaka.pim.repository.AlbumRepository;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumRepository albumRepository;

    @GetMapping
    public Iterable<Album> findAll() {
        return albumRepository.findAll();
    }

}
