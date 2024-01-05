package dev.vepo.schema.converter.avro;

import dev.vepo.schema.converter.schema.Field;
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

    @Override
    public Type type() {
        switch (field.schema().getType()) {
            case STRING:
                return Type.STRING;
            case ARRAY:
                return Type.ARRAY;

        }
        return Type.UNDEFINED;
    }

    @Override
    public Type items() {
        switch (field.schema().getValueType().getType()) {
            case STRING:
                return Type.STRING;
        }
        return Type.UNDEFINED;
    }

}
