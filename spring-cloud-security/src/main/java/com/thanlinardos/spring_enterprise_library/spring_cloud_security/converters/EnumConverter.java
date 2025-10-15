package com.thanlinardos.spring_enterprise_library.spring_cloud_security.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * A JPA attribute converter that converts Enum objects to their String representation
 * for database storage and vice versa.
 */
@Converter(autoApply = true)
public class EnumConverter implements AttributeConverter<Enum<?>, String> {

    /**
     * Default constructor.
     */
    public EnumConverter() {
        // Default constructor
    }


    /**
     * Converts an Enum to its String representation for database storage.
     *
     * @param attribute the Enum to convert.
     * @return the String representation of the Enum, or null if the Enum is null.
     */
    @Override
    public String convertToDatabaseColumn(Enum<?> attribute) {
        return attribute.name();
    }

    /**
     * Converts a String representation of an Enum from the database back to an Enum object.
     *
     * @param dbData the String representation of the Enum.
     * @return the Enum object, or null if the input String is null or invalid.
     */
    @Override
    public Enum<?> convertToEntityAttribute(String dbData) {
        return Enum.valueOf(Enum.class, dbData);
    }
}
