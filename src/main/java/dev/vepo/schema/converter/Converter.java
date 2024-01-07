package dev.vepo.schema.converter;

import java.util.List;

import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.ArrayDefault;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.FieldDefault;
import org.apache.avro.SchemaBuilder.FieldTypeBuilder;
import org.apache.avro.SchemaBuilder.NullDefault;
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
            Type currentType = types.get(0);
            switch (currentType) {
                case STRING:
                    if (unionOf != null) {
                        return accumulate(null, unionOf.stringType(), types.subList(1, types.size()));
                    } else {
                        return accumulate(null, acc.and().stringType(), types.subList(1, types.size()));
                    }
                case INTEGER:
                    if (unionOf != null) {
                        return accumulate(null,
                                          unionOf.intType().and().longType(),
                                          types.subList(1, types.size()));
                    } else {
                        return accumulate(null,
                                          acc.and().intType().and().longType(),
                                          types.subList(1, types.size()));
                    }
                case NUMBER:
                    if (unionOf != null) {
                        return accumulate(null,
                                          unionOf.intType().and().longType().and().floatType().and().doubleType(),
                                          types.subList(1, types.size()));
                    } else {
                        return accumulate(null,
                                          acc.and().intType().and().longType().and().floatType().and().doubleType(),
                                          types.subList(1, types.size()));
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
                Schema itemSchema;
                switch (f.type()) {
                    case STRING:
                        if (f.required()) {
                            builder = fieldBuilder.stringType().noDefault();
                        } else {
                            builder = fieldBuilder.unionOf().nullType().and().stringType().endUnion().noDefault();
                        }
                        break;
                    case INTEGER:
                        if (f.required()) {
                            builder = fieldBuilder.unionOf()
                                                  .intType().and()
                                                  .longType().endUnion().noDefault();
                        } else {
                            builder = fieldBuilder.unionOf()
                                                  .nullType().and()
                                                  .intType().and()
                                                  .longType().endUnion().noDefault();
                        }
                        break;
                    case NUMBER:
                        if (f.required()) {
                            builder = fieldBuilder.unionOf()
                                                  .intType().and()
                                                  .longType().and()
                                                  .floatType().and()
                                                  .doubleType().endUnion().noDefault();
                        } else {
                            builder = fieldBuilder.unionOf()
                                                  .nullType().and()
                                                  .intType().and()
                                                  .longType().and()
                                                  .floatType().and()
                                                  .doubleType().endUnion().noDefault();
                        }
                        break;
                    case OBJECT:
                        itemSchema = f.elementSchema();
                        if (f.required()) {
                            builder = buildFields(fieldBuilder.record(itemSchema.getName()).fields(),
                                                  itemSchema).endRecord()
                                                             .noDefault();
                        } else {
                            builder = buildFields(fieldBuilder.unionOf()
                                                              .nullType().and()
                                                              .record(itemSchema.getName()).fields(),
                                                  itemSchema).endRecord().endUnion()
                                                             .noDefault();
                        }
                        break;
                    case ARRAY:
                        if (f.required()) {
                            TypeBuilder<ArrayDefault<T>> arrayBuilder = fieldBuilder.array().items();
                            switch (f.items()) {
                                case STRING:
                                    builder = arrayBuilder.stringType().noDefault();
                                    break;
                                case INTEGER:
                                    builder = arrayBuilder.unionOf()
                                                          .intType().and()
                                                          .longType().endUnion().noDefault();
                                    break;
                                case NUMBER:
                                    builder = arrayBuilder.unionOf()
                                                          .intType().and()
                                                          .longType().and()
                                                          .floatType().and()
                                                          .doubleType().endUnion().noDefault();
                                    break;
                                case OBJECT:
                                    itemSchema = f.elementSchema();
                                    builder = buildFields(arrayBuilder.record(itemSchema.getName()).fields(),
                                                          itemSchema).endRecord()
                                                                     .noDefault();
                            }
                        } else {
                            TypeBuilder<UnionAccumulator<NullDefault<T>>> arrayBuilder =
                                    fieldBuilder.unionOf().nullType().and().array().items();
                            switch (f.items()) {
                                case STRING:
                                    builder = arrayBuilder.stringType().endUnion().noDefault();
                                    break;
                                case INTEGER:
                                    builder = arrayBuilder.unionOf()
                                                          .intType().and()
                                                          .longType().endUnion().endUnion().noDefault();
                                    break;
                                case NUMBER:
                                    builder = arrayBuilder.unionOf()
                                                          .intType().and()
                                                          .longType().and()
                                                          .floatType().and()
                                                          .doubleType().endUnion().endUnion().noDefault();
                                    break;
                                case OBJECT:
                                    itemSchema = f.elementSchema();
                                    builder = buildFields(arrayBuilder.record(itemSchema.getName()).fields(),
                                                          itemSchema).endRecord().endUnion()
                                                                     .noDefault();
                            }
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