package ru.dzyubaka.pim.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Band {
    @Id
    private String name;
}
