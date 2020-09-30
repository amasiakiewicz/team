package com.casinoroyale.team.infrastructure;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.money.Money;

public class MoneyDeserializer extends JsonDeserializer<Money> {

    @Override
    public Money deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return Money.parse(jp.readValueAs(String.class));
    }

}