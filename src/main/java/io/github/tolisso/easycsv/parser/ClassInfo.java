package io.github.tolisso.easycsv.parser;


import java.util.Arrays;

public class ClassInfo {
    private String[] packageName;
    private String className;

    public ClassInfo(String fullClassName) {
        String[] splitName = fullClassName.split("\\.");
        className = splitName[splitName.length - 1];
        packageName = Arrays.copyOf(splitName, splitName.length - 1);
    }

    public String[] getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }
}


