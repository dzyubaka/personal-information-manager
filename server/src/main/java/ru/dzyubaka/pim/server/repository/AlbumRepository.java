package ru.dzyubaka.pim.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dzyubaka.pim.server.entity.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
