package ru.dzyubaka.pim.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.dzyubaka.pim.server.dto.AlbumResponse;
import ru.dzyubaka.pim.server.dto.BandDetailDto;
import ru.dzyubaka.pim.server.dto.BandSummaryDto;
import ru.dzyubaka.pim.server.entity.Album;
import ru.dzyubaka.pim.server.entity.Band;
import ru.dzyubaka.pim.server.repository.BandRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BandService {
    private final BandRepository bandRepository;

    public Iterable<BandSummaryDto> findAll() {
        return bandRepository.findAll(Sort.by("name")).stream().map(b -> new BandSummaryDto(b.getId(), b.getName())).toList();
    }

    public Optional<BandDetailDto> findById(Long id) {
        return bandRepository.findById(id).map(BandService::toBandDetailDto);
    }

    private static BandDetailDto toBandDetailDto(Band band) {
        return new BandDetailDto(band.getId(), band.getName(), band.getAlbums().stream().map(BandService::toAlbumResponse).toList());
    }

    private static AlbumResponse toAlbumResponse(Album album) {
        return new AlbumResponse(album.getId(),
                album.getBand().getId(),
                album.getName(),
                album.getYear(),
                album.getListenedAt());
    }
}
