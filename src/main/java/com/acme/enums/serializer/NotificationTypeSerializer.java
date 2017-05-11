package com.acme.enums.serializer;

import com.acme.enums.NotificationType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.base.CaseFormat;

import java.io.IOException;

public class NotificationTypeSerializer extends JsonSerializer<NotificationType>{

    @Override
    public void serialize(NotificationType type, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("id");
        generator.writeString(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, type.name()));
        generator.writeFieldName("name");
        generator.writeString(type.getText());
        generator.writeEndObject();
    }

}
