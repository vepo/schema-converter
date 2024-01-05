package dev.vepo.schema.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.vepo.schema.converter.avro.AvroSchema;
import dev.vepo.schema.converter.json.JsonSchema;

@DisplayName("Converter")
public class ConverterTest {
    @Test
    void compatibilityTest() {
        JsonSchema jsonSchema = new JsonSchema(getClass().getResourceAsStream("/json/sample-1.json"));
        AvroSchema avroSchema = new AvroSchema(getClass().getResourceAsStream("/avro/sample-1.json"));
        assertThat(jsonSchema).isEqualTo(avroSchema);
    }

    @Test
    void simpleTest() throws IOException {
        Converter converter = new Converter();
        Parser parser = new Parser();
        Schema original = parser.parse(getClass().getResourceAsStream("/avro/sample-1.json"));
        Schema converted = converter.toAvro(new JsonSchema(getClass().getResourceAsStream("/json/sample-1.json"))).toAvro();
        assertEquals(original, converted);
    }
}
