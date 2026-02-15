package ru.dzyubaka.pim.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dzyubaka.pim.server.dto.AlbumDto;
import ru.dzyubaka.pim.server.repository.AlbumRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;

    public Collection<AlbumDto> findAll() {
        return albumRepository.findAll().stream().map(a -> new AlbumDto(
                a.getId(),
                a.getBand().getName(),
                a.getName(),
                a.getYear(),
                a.getListenedAt()
        )).toList();
    }
}
