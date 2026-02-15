package ru.dzyubaka.pim.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dzyubaka.pim.server.entity.Band;

public interface BandRepository extends JpaRepository<Band, Long> {
}
