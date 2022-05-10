package com.tolisso.easycsv.parser;

import java.util.function.Function;

public enum CsvTokenType {
    STRING(String.class, String::valueOf),
    NUMBER(Double.class, Double::valueOf);

    public final Class<?> clazz;
    public final Function<String, Object> fromString;

    CsvTokenType(Class<?> clazz, Function<String, Object> fromString) {
        this.clazz = clazz;
        this.fromString = fromString;
    }
}
