package com.tolisso.easycsv.parser;

public class CsvParseException extends RuntimeException {
    public CsvParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvParseException(String message) {
        super(message);
    }
}
