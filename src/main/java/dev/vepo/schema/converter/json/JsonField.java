package dev.vepo.schema.converter.json;

import com.fasterxml.jackson.databind.JsonNode;

import dev.vepo.schema.converter.schema.Field;

public class JsonField implements Field {

    private String name;

    public JsonField(String name, JsonNode spec) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Field)) {
            return false;
        } else if (other == this) {
            return true;
        } else {
            return Field.areEquals(this, (Field) other);
        }
    }

    @Override
    public boolean union() {
        return true;
    }

}
