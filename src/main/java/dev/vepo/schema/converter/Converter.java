package dev.vepo.schema.converter;

import java.util.List;

import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.ArrayDefault;
import org.apache.avro.SchemaBuilder.BaseTypeBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.FieldDefault;
import org.apache.avro.SchemaBuilder.FieldTypeBuilder;
import org.apache.avro.SchemaBuilder.StringDefault;
import org.apache.avro.SchemaBuilder.TypeBuilder;
import org.apache.avro.SchemaBuilder.UnionAccumulator;
import org.apache.avro.SchemaBuilder.UnionFieldTypeBuilder;

import dev.vepo.schema.converter.avro.AvroSchema;
import dev.vepo.schema.converter.schema.Field;
import dev.vepo.schema.converter.schema.Schema;
import dev.vepo.schema.converter.schema.Type;

public class Converter {
    public Converter() {
    }

    private static <T, V extends FieldDefault<T, ?>> FieldAssembler<T> accumulate(UnionFieldTypeBuilder<T> unionOf,
                                                                                  UnionAccumulator<V> acc,
                                                                                  List<Type> types) {
        if (types.size() > 0) {
            Type t = types.get(0);
            switch (t) {
                case STRING:
                    if (unionOf != null) {
                        return accumulate(null, unionOf.stringType(), types.subList(1, types.size()));
                    } else {
                        return accumulate(null, acc.and().stringType(), types.subList(1, types.size()));
                    }
                case INTEGER:
                    if (unionOf != null) {
                        return accumulate(null, unionOf.intType(), types.subList(1, types.size()));
                    } else {
                        return accumulate(null, acc.and().intType(), types.subList(1, types.size()));
                    }
                case NULL:
                    if (unionOf != null) {
                        return accumulate(null, unionOf.nullType(), types.subList(1, types.size()));
                    } else {
                        return accumulate(null, acc.and().nullType(), types.subList(1, types.size()));
                    }
            }
            throw new IllegalStateException("Not implemetend type");
        } else {
            return acc.endUnion().noDefault();
        }
    }

    private <T> FieldAssembler<T> buildFields(FieldAssembler<T> initialBuilder, Schema schema) {
        FieldAssembler<T> builder = initialBuilder;
        for (Field f : schema.getFields()) {
            FieldTypeBuilder<T> fieldBuilder = builder.name(f.name()).type();
            if (f.union()) {
                builder = (FieldAssembler<T>) accumulate(fieldBuilder.unionOf(), null, f.types());
            } else {
                switch (f.type()) {
                    case STRING:
                        builder = fieldBuilder.stringType().noDefault();
                        break;
                    case INTEGER:
                        builder = fieldBuilder.intType().noDefault();
                        break;
                    case ARRAY:
                        TypeBuilder<ArrayDefault<T>> arrayBuilder = fieldBuilder.array().items();
                        switch (f.items()) {
                            case STRING:
                                builder = arrayBuilder.stringType().noDefault();
                                break;
                            case OBJECT:
                                Schema itemSchema = f.elementSchema();
                                builder = buildFields(arrayBuilder.record(itemSchema.getName()).fields(),
                                                      itemSchema).endRecord()
                                                                 .noDefault();
                        }
                        break;
                    case NULL:
                        builder = fieldBuilder.nullType().noDefault();
                        break;
                }
            }
        }
        return builder;
    }

    public AvroSchema toAvro(Schema schema) {
        FieldAssembler<org.apache.avro.Schema> builder = SchemaBuilder.record(schema.getName())
                                                                      .namespace(schema.getNamespace())
                                                                      .doc(schema.getDocumentation())
                                                                      .fields();
        buildFields(builder, schema);
        return new AvroSchema(builder.endRecord());
    }
}