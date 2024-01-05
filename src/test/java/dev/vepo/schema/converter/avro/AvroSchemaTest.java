package dev.vepo.schema.converter.avro;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.avro.Schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import dev.vepo.schema.converter.schema.Field;

public class AvroSchemaTest {
    @Test
    void propertiesTest() throws IOException {
        Schema original = new Schema.Parser().parse(getClass().getResourceAsStream("/avro/sample-1.json"));
        AvroSchema schema = new AvroSchema(getClass().getResourceAsStream("/avro/sample-1.json"));
        assertEquals(original.getName(), schema.getName());
        assertThat(schema.getFields()).extracting(Field::name)
                                      .containsExactly("name", "surname", "phones", "addresses");
    }
}
