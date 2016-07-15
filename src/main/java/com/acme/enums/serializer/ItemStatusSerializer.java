package com.acme.enums.serializer;

import com.acme.enums.ItemStatus;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.base.CaseFormat;

import java.io.IOException;

public class ItemStatusSerializer extends JsonSerializer<ItemStatus>{

    @Override
    public void serialize(ItemStatus status, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("id");
        generator.writeString(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, status.name()));
        generator.writeFieldName("name");
        generator.writeString(status.getText());
        generator.writeEndObject();
    }

}
