package com.tolisso.easycsv.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {
    CsvParser parser = new CsvParser();

    @Test
    @DisplayName("Simple valid example parsing")
    void getInfoSimple_success() {
        String csvEntityString = joinLines(
                "a,b,composite",
                "1,hello,2007",
                "3,world,school"
        );
        DataInfo dataInfo = parser.getInfoFromString(csvEntityString);

        assertNotNull(dataInfo);
        assertEquals(3, dataInfo.getRowSize());
        checkNames(dataInfo);
        checkTypes(dataInfo);
        checkValues(dataInfo);
    }

    private void checkValues(DataInfo dataInfo) {
        assertNotNull(dataInfo.getValues());
        assertEquals(2, dataInfo.getValues().size());

        Map<String, Object> rowMap1 = dataInfo.getValues().get(0);
        assertNotNull(rowMap1);
        assertTrue(rowMap1.containsKey("a"));
        assertEquals(1.0, rowMap1.get("a"));
        assertTrue(rowMap1.containsKey("b"));
        assertEquals("hello", rowMap1.get("b"));
        assertTrue(rowMap1.containsKey("composite"));
        assertEquals("2007", rowMap1.get("composite"));
        assertEquals(3, rowMap1.size());

        Map<String, Object> rowMap2 = dataInfo.getValues().get(1);
        assertNotNull(rowMap2);
        assertTrue(rowMap2.containsKey("a"));
        assertEquals(3.0, rowMap2.get("a"));
        assertTrue(rowMap2.containsKey("b"));
        assertEquals("world", rowMap2.get("b"));
        assertTrue(rowMap2.containsKey("composite"));
        assertEquals("school", rowMap2.get("composite"));
        assertEquals(3, rowMap2.size());
    }

    private void checkTypes(DataInfo dataInfo) {
        List<CsvTokenType> types = dataInfo.getTypes();
        assertNotNull(types);
        assertEquals(3, types.size());
        Assertions.assertEquals(CsvTokenType.NUMBER, types.get(0));
        Assertions.assertEquals(CsvTokenType.STRING, types.get(1));
        Assertions.assertEquals(CsvTokenType.STRING, types.get(2));
    }

    private void checkNames(DataInfo dataInfo) {
        List<String> names = dataInfo.getNames();
        assertNotNull(names);
        assertEquals(3, names.size());
        assertEquals("a", names.get(0));
        assertEquals("b", names.get(1));
        assertEquals("composite", names.get(2));
    }

    private static String joinLines(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }

    @Test
    @DisplayName("Plenty fault parsing examples")
    void getInfoSimple_faults() {
        // empty names
        checkIfThrowsOnLines("");
        checkIfThrowsOnLines("", "");
        checkIfThrowsOnLines("", "1,2");
        // values number mismatching
        checkIfThrowsOnLines("a,b", "");
        checkIfThrowsOnLines("a,b", "1");
        checkIfThrowsOnLines("a", "1,2");
        // invalid names
        checkIfThrowsOnLines("1", "1");
        checkIfThrowsOnLines("!ab", "1");

    }

    private void checkIfThrowsOnLines(String... lines) {
        String csvEntityString = joinLines(lines);
        assertThrows(CsvParseException.class, () -> parser.getInfoFromString(csvEntityString));
    }

    @Test
    @DisplayName("Narrow working parsing examples")
    void getInfoNarrowExamples_success() {
        parser.getInfoFromString(joinLines("a,b", ","));
    }

}
