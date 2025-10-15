package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.entity.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Base class for JPA entities with a generated ID.
 */
@Data
@SuperBuilder
@MappedSuperclass
public class BasicIdJpa {

    /**
     * The unique identifier for the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Default constructor.
     */
    public BasicIdJpa() {
    }

    /**
     * Constructs a BasicIdJpa with the specified ID.
     *
     * @param id the ID to set.
     */
    public BasicIdJpa(Long id) {
        this.id = id;
    }
}
