package ru.dzyubaka.pim.server.repository;

import org.springframework.data.repository.CrudRepository;
import ru.dzyubaka.pim.server.model.Album;

public interface AlbumRepository extends CrudRepository<Album, String> {
}
