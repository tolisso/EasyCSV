package com.tolisso.easycsv.generator;

/**
 * Generated file info
 * Only default package allowed
 */
class GenFile {
    /**
     * Contents of generated source file
     */
    private String data;

    /**
     * Name of the class
     */
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        assert !className.contains(".");
        this.className = className;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
