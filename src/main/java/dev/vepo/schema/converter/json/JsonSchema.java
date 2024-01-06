package dev.vepo.schema.converter.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.vepo.schema.converter.schema.Field;
import dev.vepo.schema.converter.schema.Schema;

public class JsonSchema implements Schema {

    private static final String FULL_NAME_REGEX =
            "\\A([a-zA-Z][a-zA-Z0-9]*(?:\\.[a-zA-Z][a-zA-Z0-9]*)*)\\.([a-zA-Z][a-zA-Z0-9]*)\\z";
    private static final Pattern FULL_NAME_PATTERN = Pattern.compile(FULL_NAME_REGEX);

    private JsonNode schema;

    public JsonSchema(InputStream schema) {
        Objects.requireNonNull(schema, "Schema cannot be null!");
        try {
            this.schema = new ObjectMapper().readTree(schema);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Invalid schema!", ioe);
        }
    }

    public JsonSchema(JsonNode schema) {
        Objects.requireNonNull(schema, "Schema cannot be null!");
        this.schema = schema;
    }

    @Override
    public List<Field> getFields() {
        Iterable<Map.Entry<String, JsonNode>> properties = () -> schema.get("properties").fields();
        return StreamSupport.stream(properties.spliterator(), false)
                            .map(spec -> new JsonField(spec.getKey(), spec.getValue()))
                            .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        String title = schema.get("title").asText();
        Matcher matcher = FULL_NAME_PATTERN.matcher(title);
        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return title;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Schema)) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            return Schema.areEquals(this, (Schema) obj);
        }
    }

    @Override
    public int hashCode() {
        return Schema.hashCode(this);
    }

    @Override
    public String toString() {
        return Schema.toString("JsonSchema", this);
    }

    @Override
    public String getNamespace() {
        String title = schema.get("title").asText();
        Matcher matcher = FULL_NAME_PATTERN.matcher(title);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return title;
        }
    }

    @Override
    public String getDocumentation() {
        return Optional.ofNullable(schema.get("description"))
                       .map(JsonNode::asText)
                       .orElse("");
    }
}
