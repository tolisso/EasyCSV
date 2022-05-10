package com.tolisso.easycsv.parser;

import javax.lang.model.SourceVersion;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CsvParser {

    public DataInfo getInfoFromString(String stringToParse) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stringToParse.getBytes(StandardCharsets.UTF_8));
        return getInfoFromReader(new BufferedReader(new InputStreamReader(byteArrayInputStream)));
    }

    public DataInfo getInfoFromFile(String stringPathToCsv) {
        Path pathToCsv;
        try {
            pathToCsv = Paths.get(stringPathToCsv);
        } catch (InvalidPathException exc) {
            throw new CsvParseException("Invalid path", exc);
        }
        try {
            return getInfoFromReader(Files.newBufferedReader(pathToCsv));
        } catch (IOException exc) {
            throw new CsvParseException("Can't read from file '" + pathToCsv + "'", exc);
        }
    }

    public DataInfo getInfoFromReader(BufferedReader reader) {
        List<String> names;
        try {
            String namesLine = reader.readLine();
            if (namesLine == null) {
                throw new CsvParseException("Empty names line");
            }
            names = Arrays.asList(namesLine.split(","));
        } catch (IOException exc) {
            throw new CsvParseException("Can't read line", exc);
        }
        List<List<String>> data = reader.lines().map(this::processLine).collect(Collectors.toList());
        validateNames(names);
        validateUnparsedData(data, names.size());

        List<CsvTokenType> types = getTypes(data, names.size());

        List<Map<String, Object>> values = data.stream()
                .map(datum -> parseToMap(names, types, datum))
                .collect(Collectors.toList());
        return new DataInfo(names, types, values);
    }

    private Map<String, Object> parseToMap(List<String> names, List<CsvTokenType> types, List<String> row) {
        Map<String, Object> resMap = new HashMap<>();
        for (int i = 0; i < names.size(); i++) {
            resMap.put(names.get(i), types.get(i).fromString.apply(row.get(i)));
        }
        return resMap;
    }

    private List<CsvTokenType> getTypes(List<List<String>> data, int rowLength) {
        return IntStream.range(0, rowLength)
                .mapToObj(i -> getColumnType(data, i))
                .collect(Collectors.toList());
    }

    private CsvTokenType getColumnType(List<List<String>> data, int ic) {
        List<CsvTokenType> types = data.stream()
                .map(row -> row.get(ic))
                .map(this::getType)
                .distinct()
                .collect(Collectors.toList());
        if (types.size() == 1) {
            return types.get(0);
        } else {
            return CsvTokenType.STRING;
        }
    }

    private List<String> processLine(String line) {
        return Arrays.stream(line.split(",", -1)).collect(Collectors.toList());
    }

    private void validateUnparsedData(List<List<String>> data, int rowSize) {
        check(data != null, "data is null");
        check(!data.isEmpty(), "data is empty");
        for (int i = 0; i < data.size(); i++){
            check(data.get(i) != null, "data row " + i + " is null");
        }
        for (int i = 0; i < data.size(); i++){
            int realSize = data.get(i).size();
            check(realSize == rowSize, "data row " + i + " has size " + realSize + ", but expected " + rowSize);
        }
        for (int i = 0; i < data.size(); i++){
            for (int j = 0; j < rowSize; j++) {
                check(data.get(i) != null, "data element (" + i + ", " + j + ") is null");
            }
        }
    }

    private void validateNames(List<String> names) {
        check(names != null, "names is null");
        check(!names.isEmpty(), "names is empty");
        for (int i = 0; i < names.size(); i++) {
            check(names.get(i) != null, "name " + i + " is null");
            check(SourceVersion.isName(names.get(i)), "name '" + names.get(i) + "' is not valid");
        }
        names.stream().collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()))
                .forEach((name, cnt) -> check(cnt == 1, "name '" + name + "' not unique"));
    }

    private void check(boolean value, String errorMsg) {
        if (!value) {
            throw new CsvParseException(errorMsg);
        }
    }

    public CsvTokenType getType(String token) {
        try {
            Double.parseDouble(token);
            return CsvTokenType.NUMBER;
        } catch (NumberFormatException ignored) {
            return CsvTokenType.STRING;
        }
    }
}
