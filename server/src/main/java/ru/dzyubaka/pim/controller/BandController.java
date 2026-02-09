package ru.dzyubaka.pim.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dzyubaka.pim.model.Band;
import ru.dzyubaka.pim.repository.BandRepository;

@RestController
@RequestMapping("bands")
@RequiredArgsConstructor
public class BandController {

    private final BandRepository bandRepository;

    @GetMapping
    public Iterable<Band> findAll() {
        return bandRepository.findAll();
    }

}
