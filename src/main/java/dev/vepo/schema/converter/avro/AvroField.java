package dev.vepo.schema.converter.avro;

import dev.vepo.schema.converter.schema.Field;

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

}
