package com.tolisso.easycsv.generator;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Generator {

    protected String[] packages;
    private String prefix;

    protected Generator(String[] packageName, String prefix) {
        this.packages = packageName;
        this.prefix = prefix;
    }

    public void generate() throws JClassAlreadyExistsException, IOException {
        Path srcPath = Paths.get("target", "generated-sources", "tolisso-df");
        try {
            Files.createDirectories(srcPath);
        } catch (FileAlreadyExistsException ignored) {

        }
        generateCodeModel().build(srcPath.toFile());
    }

    protected String getParentName() {
        return prefix;
    }

    protected String getRowName() {
        return prefix + "Row";
    }

    protected String getDfName() {
        return prefix + "Df";
    }

    /**
     * Name of current generating class.
     * Equals either of `getDfName`, `getParentName`, `getRowName`
     */
    protected abstract String getGeneratedClassName();

    protected abstract JCodeModel generateCodeModel() throws JClassAlreadyExistsException;
}
