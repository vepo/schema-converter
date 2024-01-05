package dev.vepo.schema.converter.avro;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dev.vepo.schema.converter.schema.Field;
import dev.vepo.schema.converter.schema.Schema;
import org.apache.avro.Schema.Parser;

public class AvroSchema implements Schema {

    private org.apache.avro.Schema schema;

    public AvroSchema(InputStream schemaContents) {
        Objects.requireNonNull(schemaContents, "Schema cannot be null!");
        try {
            this.schema = new Parser().parse(schemaContents);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Invalid schema!", ioe);
        }
    }

    public AvroSchema(org.apache.avro.Schema schema) {
        Objects.requireNonNull(schema, "Schema cannot be null!");
        this.schema = schema;
    }

    @Override
    public List<Field> getFields() {
        return this.schema.getFields()
                          .stream()
                          .map(AvroField::new)
                          .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return schema.getName();
    }

    @Override
    public String getNamespace() {
        return schema.getNamespace();
    }

    @Override
    public String getDocumentation() {
        return schema.getDoc();
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
        return Schema.toString("AvroSchema", this);
    }

    public org.apache.avro.Schema toAvro() {
        return schema;
    }

}
