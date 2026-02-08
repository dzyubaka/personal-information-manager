package ru.dzyubaka.pim;

import org.springframework.data.repository.CrudRepository;
import ru.dzyubaka.pim.Album;

public interface AlbumRepository extends CrudRepository<Album, String> {
}
