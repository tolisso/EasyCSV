package com.tolisso.easycsv.mojo;

import org.apache.maven.plugins.annotations.Parameter;

public class CsvPathAndName {
    private String className;
    private String pathToCsv;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPathToCsv() {
        return pathToCsv;
    }

    public void setPathToCsv(String pathToCsv) {
        this.pathToCsv = pathToCsv;
    }
}
