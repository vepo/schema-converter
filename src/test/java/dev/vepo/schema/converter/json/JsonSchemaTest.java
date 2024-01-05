package dev.vepo.schema.converter.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dev.vepo.schema.converter.schema.Field;

public class JsonSchemaTest {
    @Test
    void propertiesTest() {
        JsonSchema schema = new JsonSchema(getClass().getResourceAsStream("/json/sample-1.json"));
        assertEquals("Fruits", schema.getName());
        assertThat(schema.getFields()).extracting(Field::name)
                                      .containsExactly("fruits", "vegetables");
    }
}
