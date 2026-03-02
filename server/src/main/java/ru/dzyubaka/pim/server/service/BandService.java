package ru.dzyubaka.pim.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.dzyubaka.pim.server.YandexMusicAlbumFetcher;
import ru.dzyubaka.pim.server.dto.AlbumResponse;
import ru.dzyubaka.pim.server.dto.BandDetailDto;
import ru.dzyubaka.pim.server.dto.BandSummaryDto;
import ru.dzyubaka.pim.server.entity.Album;
import ru.dzyubaka.pim.server.entity.Band;
import ru.dzyubaka.pim.server.repository.BandRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BandService {

    private final BandRepository bandRepository;

    private final YandexMusicAlbumFetcher albumFetcher;

    public Iterable<BandSummaryDto> findAll() {
        return bandRepository.findAll(Sort.by("name")).stream().map(b -> new BandSummaryDto(b.getId(), b.getName())).toList();
    }

    public Optional<BandDetailDto> findById(Long id) {
        return bandRepository.findById(id).map(BandService::toBandDetailDto);
    }

    public BandSummaryDto importBand(String bandName) {
        Band band = new Band(null, bandName, null);
        List<Album> albums = albumFetcher.getAlbums(bandName).entrySet().stream()
                .map(e -> new Album(null, band, e.getKey(), e.getValue(), null))
                .collect(Collectors.toList());
        band.setAlbums(albums);
        bandRepository.save(band);
        return new BandSummaryDto(band.getId(), band.getName());
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
