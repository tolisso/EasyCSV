package com.tolisso.easycsv.parser;

import java.util.List;
import java.util.Map;

public class DataInfo {

    private final List<String> names;
    private final List<CsvTokenType> types;
    private final List<Map<String, Object>> values;

    public DataInfo(List<String> names, List<CsvTokenType> types, List<Map<String, Object>> values) {
        this.names = names;
        this.types = types;
        this.values = values;
    }

    public List<String> getNames() {
        return names;
    }

    public List<CsvTokenType> getTypes() {
        return types;
    }

    public List<Map<String, Object>> getValues() {
        return values;
    }

    public int getRowSize() {
        return names.size();
    }
}
