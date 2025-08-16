package com.thanlinardos.spring_enterprise_library.spring_cloud_security.converters;

import com.thanlinardos.spring_enterprise_library.utils.ParserUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.UUID;

@Converter(autoApply = true)
public class UUIDConverter implements AttributeConverter<UUID, String> {

    @Override
    public String convertToDatabaseColumn(final UUID uuid) {
        return ParserUtil.safeParseString(uuid);
    }

    @Override
    public UUID convertToEntityAttribute(final String data) {
        return ParserUtil.safeParseUUID(data);
    }
}
