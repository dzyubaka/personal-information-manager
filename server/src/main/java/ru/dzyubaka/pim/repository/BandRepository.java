package ru.dzyubaka.pim.repository;

import org.springframework.data.repository.CrudRepository;
import ru.dzyubaka.pim.model.Band;

public interface BandRepository extends CrudRepository<Band, String> {
}
