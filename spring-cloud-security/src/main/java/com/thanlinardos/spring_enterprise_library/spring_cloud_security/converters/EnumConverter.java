package com.thanlinardos.spring_enterprise_library.spring_cloud_security.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EnumConverter implements AttributeConverter<Enum<?>, String> {


    @Override
    public String convertToDatabaseColumn(Enum<?> attribute) {
        return attribute.name();
    }

    @Override
    public Enum<?> convertToEntityAttribute(String dbData) {
        return Enum.valueOf(Enum.class, dbData);
    }
}
