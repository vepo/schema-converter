package dev.vepo.schema.converter.schema;

import java.util.Objects;

public interface Field {
    String name();

    boolean union();

    public static boolean areEquals(Field f1, Field f2) {
        return Objects.equals(f1.name(), f2.name());
    }
}
