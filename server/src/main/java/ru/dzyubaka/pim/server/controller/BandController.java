package ru.dzyubaka.pim.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dzyubaka.pim.server.model.Band;
import ru.dzyubaka.pim.server.repository.BandRepository;

import java.util.Optional;

@RestController
@RequestMapping("bands")
@RequiredArgsConstructor
public class BandController {

    private final BandRepository bandRepository;

    @GetMapping
    public Iterable<Band> findAll() {
        return bandRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Band> findById(@PathVariable Long id) {
        return bandRepository.findById(id);
    }
}
