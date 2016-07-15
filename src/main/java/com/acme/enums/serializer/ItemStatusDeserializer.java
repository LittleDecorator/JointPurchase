package com.acme.enums.serializer;

import com.acme.enums.ItemStatus;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class ItemStatusDeserializer extends JsonDeserializer<ItemStatus>{

    @Override
    public ItemStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        ItemStatus status = ItemStatus.getByName(node.get("id").textValue());
        return status;
    }

}
