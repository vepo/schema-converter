package dev.vepo.schema.converter.json;

import java.security.InvalidKeyException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.JsonNode;

import dev.vepo.schema.converter.schema.Field;
import dev.vepo.schema.converter.schema.Schema;
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
        return spec.has("oneOf");
    }

    private static Type type(JsonNode specNode) {
        if (specNode.has("type")) {
            switch (specNode.get("type").asText()) {
                case "string":
                    return Type.STRING;
                case "integer":
                    return Type.INTEGER;
                case "array":
                    return Type.ARRAY;
                case "object":
                    return Type.OBJECT;
                case "null":
                    return Type.NULL;

            }
        }
        return Type.UNDEFINED;
    }

    @Override
    public Type type() {
        return type(spec);
    }

    @Override
    public List<Type> types() {
        if (spec.has("oneOf")) {
            return IntStream.range(0, spec.get("oneOf").size())
                            .mapToObj(spec.get("oneOf")::get)
                            .map(JsonField::type)
                            .collect(Collectors.toList());
        } else {
            throw new IllegalStateException("Not oneOf field!");
        }
    }

    @Override
    public Type items() {
        if (spec.has("items")) {
            JsonNode items = spec.get("items");
            if (items.has("type")) {
                switch (items.get("type").asText()) {
                    case "string":
                        return Type.STRING;
                    case "object":
                        return Type.OBJECT;
                }
            }
        }
        return Type.UNDEFINED;
    }

    @Override
    public Schema elementSchema() {
        return new JsonSchema(spec.get("items"));
    }

}
