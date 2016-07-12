package com.acme.util;

import com.acme.enums.OrderStatus;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.base.CaseFormat;

import java.io.IOException;

public class OrderStatusSerializer extends JsonSerializer<OrderStatus> {

    @Override
    public void serialize(OrderStatus status, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("id");
        generator.writeString(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, status.name()));
        generator.writeFieldName("name");
        generator.writeString(status.getText());
        generator.writeEndObject();
    }
}
