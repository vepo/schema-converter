package dev.vepo.schema.converter;

import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.FieldTypeBuilder;

import dev.vepo.schema.converter.avro.AvroSchema;
import dev.vepo.schema.converter.schema.Field;
import dev.vepo.schema.converter.schema.Schema;

public class Converter {
    public Converter() {
    }

    public AvroSchema toAvro(Schema schema) {
        FieldAssembler<org.apache.avro.Schema> builder = SchemaBuilder.record(schema.getName())
                                                                      .namespace(schema.getNamespace())
                                                                      .doc(schema.getDocumentation())
                                                                      .fields();
        for (Field f : schema.getFields()) {
            FieldTypeBuilder<org.apache.avro.Schema> fieldBuilder = builder.name(f.name()).type();
            if (f.union()) {
                fieldBuilder.unionOf();
            } else {

            }
        }
        return new AvroSchema(builder.endRecord());
    }
}