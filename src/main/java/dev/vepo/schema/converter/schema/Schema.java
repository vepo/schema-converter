package dev.vepo.schema.converter.schema;

import java.util.List;
import java.util.Objects;

public interface Schema {
    public static boolean areEquals(Schema s1, Schema s2) {
        return s1 != null &&
                s2 != null &&
                Objects.equals(s1.getFields(), s2.getFields()) &&
                Objects.equals(s1.getName(), s2.getName()) &&
                Objects.equals(s1.getNamespace(), s2.getNamespace()) &&
                Objects.equals(s1.getDocumentation(), s2.getDocumentation());
    }

    public static int hashCode(Schema schema) {
        return Objects.hash(schema.getName(), schema.getNamespace(), schema.getDocumentation(), schema.getFields());
    }

    public static String toString(String type, Schema schema) {
        return String.format("%s [name=%s, namespace=%s, documentation=%s, fields=%s]", type, schema.getName(),
                             schema.getNamespace(), schema.getDocumentation(), schema.getFields());
    }

    List<Field> getFields();

    public String getName();

    String getNamespace();

    String getDocumentation();
}
