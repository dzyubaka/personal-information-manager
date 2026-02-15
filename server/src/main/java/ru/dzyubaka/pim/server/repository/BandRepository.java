package ru.dzyubaka.pim.server.repository;

import org.springframework.data.repository.CrudRepository;
import ru.dzyubaka.pim.server.model.Band;

public interface BandRepository extends CrudRepository<Band, String> {
}
