package com.acme.util;

import com.acme.enums.OrderStatus;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class OrderStatusDeserializer extends JsonDeserializer<OrderStatus>{

    @Override
    public OrderStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        System.out.println(node);
        System.out.println(node.get("id"));
        System.out.println(node.get("id").textValue());
        OrderStatus status = OrderStatus.getByName(node.get("id").textValue());
        System.out.println(status);
        return status;
//        return new User(null, node.get("username").getTextValue(), node.get("password").getTextValue());
    }
}
