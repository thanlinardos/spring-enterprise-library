package com.thanlinardos.spring_enterprise_library.model.mapped.base;

import com.thanlinardos.spring_enterprise_library.model.api.WithId;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * A basic model class that includes an ID field.
 * This class can be extended by other model classes to inherit the ID property.
 */
@Data
@AllArgsConstructor
@SuperBuilder
public class BasicIdModel implements Serializable, WithId {

    /**
     * The unique identifier for the model.
     */
    private Long id;

    /**
     * Default constructor.
     */
    public BasicIdModel() {
        // Default constructor
    }

    /**
     * Constructs a BasicIdModel from a BasicIdJpa entity.
     *
     * @param entity the BasicIdJpa entity to copy the ID from.
     */
    protected BasicIdModel(BasicIdJpa entity) {
        setId(entity.getId());
    }
}
