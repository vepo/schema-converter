package dev.vepo.schema.converter.schema;

import java.util.List;
import java.util.Objects;

public interface Field {

    public static boolean areEquals(Field f1, Field f2) {
        return Objects.equals(f1.name(), f2.name());
    }

    String name();

    boolean union();

    Type type();

    List<Type> types();

    Type items();

    Schema elementSchema();

    boolean required();
}
