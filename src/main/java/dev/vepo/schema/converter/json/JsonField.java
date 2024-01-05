package dev.vepo.schema.converter.json;

import com.fasterxml.jackson.databind.JsonNode;

import dev.vepo.schema.converter.schema.Field;
import dev.vepo.schema.converter.schema.Type;

public class JsonField implements Field {

    private String name;
    private JsonNode spec;

    public JsonField(String name, JsonNode spec) {
        this.name = name;
        this.spec = spec;
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
        return false;
    }

    @Override
    public Type type() {
        if (spec.has("type")) {
            switch (spec.get("type").asText()) {
                case "string":
                    return Type.STRING;
                case "array":
                    return Type.ARRAY;
            }
        }
        return Type.UNDEFINED;
    }

    @Override
    public Type items() {
        if (spec.has("items")) {
            JsonNode items = spec.get("items");
            if (items.has("type")) {
                switch (items.get("type").asText()) {
                    case "string":
                        return Type.STRING;
                }
            }
        }
        return Type.UNDEFINED;
    }

}
