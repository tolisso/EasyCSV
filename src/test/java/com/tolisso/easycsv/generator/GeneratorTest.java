package com.tolisso.easycsv.generator;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.tolisso.easycsv.parser.ClassInfo;
import com.tolisso.easycsv.parser.CsvTokenType;
import com.tolisso.easycsv.parser.DataInfo;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.tolisso.easycsv.parser.CsvTokenType.NUMBER;
import static com.tolisso.easycsv.parser.CsvTokenType.STRING;

class GeneratorTest {
    private RowGeneratorValidator rowGeneratorValidator = new RowGeneratorValidator();
    private DfGeneratorValidator dfGeneratorValidator = new DfGeneratorValidator();
    private ParentGeneratorValidator parentGeneratorValidator = new ParentGeneratorValidator();

    @Test
    @DisplayName("Generate, compile and validate CSV DTO classes")
    void testGenerators() throws CompileException {
        List<GenFile> files = getGenerators().stream()
                .map(this::generateGenFile)
                .collect(Collectors.toList());
        List<Class<?>> compiledClasses = Javac.compile(files);

        Class<?> parentClass = compiledClasses.get(0);
        Class<?> dfClass = compiledClasses.get(1);
        Class<?> rowClass = compiledClasses.get(2);

        rowGeneratorValidator.validate(rowClass);
        dfGeneratorValidator.validate(dfClass, rowClass);
        parentGeneratorValidator.validate(parentClass, dfClass);
    }

    private String getCode(JCodeModel codeModel) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        CodeWriter codeWriter = new CodeWriter() {
            public OutputStream openBinary(JPackage pkg, String fileName) {
                return outputStream;
            }

            public void close() {
            }
        };

        try {
            codeModel.build(codeWriter);
        } catch (IOException exc) {
            throw new RuntimeException("Unexpected IOException", exc);
        }
        return outputStream.toString();
    }

    private GenFile generateGenFile(Generator generator) {
        JCodeModel codeModel;
        try {
            codeModel = generator.generateCodeModel();
        } catch (JClassAlreadyExistsException e) {
            throw new RuntimeException("Generated class already exists", e);
        }
        GenFile genFile = new GenFile();
        genFile.setClassName(generator.getGeneratedClassName());
        genFile.setData(getCode(codeModel));
        return genFile;
    }

    private List<Generator> getGenerators() {
        ClassInfo classInfo = new ClassInfo("Caba");
        List<String> names = List.of("a", "b");
        List<CsvTokenType> types = List.of(NUMBER, STRING);
        DataInfo dataInfo = new DataInfo(names, types, null);

        Generator parentGen = new ParentClassGenerator(classInfo, "path-to-csv");
        Generator dfGen = new DfGenerator(dataInfo, classInfo);
        Generator rowGen = new RowGenerator(dataInfo, classInfo);

        return List.of(parentGen, dfGen, rowGen);
    }
}
