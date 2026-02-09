package ru.dzyubaka.pim.repository;

import org.springframework.data.repository.CrudRepository;
import ru.dzyubaka.pim.model.Album;

public interface AlbumRepository extends CrudRepository<Album, String> {
}
