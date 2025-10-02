package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.mapped.base;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.entity.base.BasicIdJpa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@AllArgsConstructor
@SuperBuilder
public class BasicIdModel implements Serializable {

    private Long id;

    public BasicIdModel() {
    }

    protected BasicIdModel(BasicIdJpa entity) {
        setId(entity.getId());
    }
}
