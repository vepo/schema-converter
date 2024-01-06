package dev.vepo.schema.converter.avro;

import java.util.List;
import java.util.stream.Collectors;

import dev.vepo.schema.converter.schema.Field;
import dev.vepo.schema.converter.schema.Schema;
import dev.vepo.schema.converter.schema.Type;

public class AvroField implements Field {

    private org.apache.avro.Schema.Field field;

    public AvroField(org.apache.avro.Schema.Field field) {
        this.field = field;
    }

    @Override
    public String name() {
        return field.name();
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
        return field.schema().isUnion();
    }

    private static Type type(org.apache.avro.Schema.Type type) {
        switch (type) {
            case STRING:
                return Type.STRING;
            case ARRAY:
                return Type.ARRAY;
        }
        return Type.UNDEFINED;
    }

    @Override
    public Type type() {
        return type(field.schema().getType());
    }

    @Override
    public List<Type> types() {
        return field.schema()
                    .getTypes()
                    .stream()
                    .map(org.apache.avro.Schema::getType)
                    .map(AvroField::type)
                    .collect(Collectors.toList());
    }

    @Override
    public Type items() {
        switch (field.schema().getValueType().getType()) {
            case STRING:
                return Type.STRING;
        }
        return Type.UNDEFINED;
    }

    @Override
    public Schema elementSchema() {
        return new AvroSchema(field.schema().getElementType());
    }

}
