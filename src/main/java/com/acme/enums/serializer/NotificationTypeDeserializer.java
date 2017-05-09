package com.acme.enums.serializer;

import com.acme.enums.NotificationType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class NotificationTypeDeserializer extends JsonDeserializer<NotificationType>{

    @Override
    public NotificationType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        NotificationType type = NotificationType.getByName(node.get("id").textValue());
        return type;
    }

}
