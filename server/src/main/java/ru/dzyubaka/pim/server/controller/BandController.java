package ru.dzyubaka.pim.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.dzyubaka.pim.server.dto.BandDetailDto;
import ru.dzyubaka.pim.server.dto.BandSummaryDto;
import ru.dzyubaka.pim.server.service.BandService;

import java.util.Optional;

@RestController
@RequestMapping("bands")
@RequiredArgsConstructor
public class BandController {
    private final BandService bandService;

    @GetMapping
    public Iterable<BandSummaryDto> findAll() {
        return bandService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<BandDetailDto> findById(@PathVariable Long id) {
        return bandService.findById(id);
    }

    @PostMapping("/import/{bandName}")
    @ResponseStatus(HttpStatus.CREATED)
    public BandSummaryDto importBand(@PathVariable String bandName) {
        return bandService.importBand(bandName);
    }
}
