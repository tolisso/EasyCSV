package com.tolisso.easycsv.generator;

import org.junit.jupiter.api.Assertions;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple utility class to invoke the java compiler.
 */

class Javac {
    public static List<Class<?>> compile(List<GenFile> files) throws CompileException {
        Path rootDir;
        try {
            rootDir = Files.createTempDirectory("tmp_tolisso_csvreader_test");
        } catch (IOException exc) {
            throw new CompileException("Can't create temporal directory for testing", exc);
        }
        try {
            createDirToCompile(rootDir, files);

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            String[] filePaths = files.stream()
                    .map(GenFile::getClassName)
                    .map(className -> rootDir.toAbsolutePath().resolve(className + ".java"))
                    .map(Object::toString)
                    .toArray(String[]::new);

            int result = compiler.run(null, null, null, filePaths);
            Assertions.assertEquals(0, result, "Compilation error occurred while compiling generated files");
            return getCompiledClasses(files, rootDir);
        } finally {
            deleteFilesAndDir(rootDir, files);
        }
    }

    private static void deleteFilesAndDir(Path rootDir, List<GenFile> files) {
        for (GenFile file : files) {
            Path pathToFile = rootDir.resolve(classNameToFileName(file.getClassName()));
            try {
                Files.deleteIfExists(pathToFile);
            } catch (IOException ignored) {}
        }

        try {
            Files.deleteIfExists(rootDir);
        } catch (IOException ignored) {}
    }

    private static List<Class<?>> getCompiledClasses(List<GenFile> files, Path rootDir) throws CompileException {
        URLClassLoader classLoader;
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{rootDir.toUri().toURL()});
        } catch (MalformedURLException exc) {
            throw new CompileException("Can't convert URI to URL to find compiled test classes", exc);
        }
        List<Class<?>> classes = new ArrayList<>();
        for (GenFile file: files) {
            try {
                Class<?> cls = Class.forName(file.getClassName(), true, classLoader);
                classes.add(cls);
            } catch (ClassNotFoundException exc) {
                throw new AssertionError("Class " + file.getClassName() + " not found after compilation");
            }
        }
        return classes;
    }

    private static void createDirToCompile(Path rootDir, List<GenFile> files) throws CompileException {
        for (GenFile file: files) {
            Path pathToFile;
            try {
                pathToFile = rootDir.resolve(classNameToFileName(file.getClassName()));
                Files.createFile(pathToFile);
            } catch (IOException | SecurityException exc) {
                throw new CompileException("Can't create temporal file " + file.getClassName() + " for testing");
            }
            try (BufferedWriter bf = Files.newBufferedWriter(pathToFile)) {
                bf.write(file.getData());
            } catch (IOException exc) {
                throw new CompileException("Can't write to temporal file " + file.getClassName() + " for testing");
            }
        }
    }

    private static String classNameToFileName(String className) {
        return className + ".java";
    }
}
