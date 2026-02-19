package ru.dzyubaka.pim.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dzyubaka.pim.server.dto.AlbumResponse;
import ru.dzyubaka.pim.server.dto.AlbumRequest;
import ru.dzyubaka.pim.server.entity.Album;
import ru.dzyubaka.pim.server.entity.Band;
import ru.dzyubaka.pim.server.repository.AlbumRepository;
import ru.dzyubaka.pim.server.repository.BandRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final BandRepository bandRepository;
    private final AlbumRepository albumRepository;

    public Collection<AlbumResponse> findAll() {
        return albumRepository.findAll().stream().map(a -> new AlbumResponse(
                a.getId(),
                a.getBand().getId(),
                a.getName(),
                a.getYear(),
                a.getListenedAt()
        )).toList();
    }

    public void update(long id, AlbumRequest albumRequest) {
        Album album = albumRepository.findById(id).orElseThrow();
        album.setBand(bandRepository.findById(albumRequest.getBand()).orElseThrow());
        album.setName(albumRequest.getName());
        album.setYear(albumRequest.getYear());
        album.setListenedAt(albumRequest.getListenedAt());
        albumRepository.save(album);
    }
}
