package com.thanlinardos.spring_enterprise_library.spring_cloud_security.converters;

import com.thanlinardos.spring_enterprise_library.parse.utils.ParserUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.UUID;

/**
 * A JPA attribute converter that converts UUID objects to their String representation
 * for database storage and vice versa.
 */
@Converter(autoApply = true)
public class UUIDConverter implements AttributeConverter<UUID, String> {

    /**
     * Default constructor.
     */
    public UUIDConverter() {
        // Default constructor
    }

    /**
     * Converts a UUID to its String representation for database storage.
     *
     * @param uuid the UUID to convert.
     * @return the String representation of the UUID, or null if the UUID is null.
     */
    @Override
    public String convertToDatabaseColumn(final UUID uuid) {
        return ParserUtil.safeParseString(uuid);
    }

    /**
     * Converts a String representation of a UUID from the database back to a UUID object.
     *
     * @param data the String representation of the UUID.
     * @return the UUID object, or null if the input String is null or invalid.
     */
    @Override
    public UUID convertToEntityAttribute(final String data) {
        return ParserUtil.safeParseUUID(data);
    }
}
