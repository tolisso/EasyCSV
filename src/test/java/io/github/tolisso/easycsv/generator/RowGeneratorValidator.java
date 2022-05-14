package io.github.tolisso.easycsv.generator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.github.tolisso.easycsv.generator.GeneratorValidatorUtil.getInstance;
import static io.github.tolisso.easycsv.generator.GeneratorValidatorUtil.validateAndGetMethods;

public class RowGeneratorValidator {

    public void validate(Class<?> rowClass) {
        assertEquals("CabaRow", rowClass.getSimpleName());

        Set<String> methodNames = getMethodNames();
        Map<String, Method> methods = validateAndGetMethods(rowClass, methodNames);

        Object df = getInstance(rowClass);

        try {
            methods.get("setA").invoke(df, 1.);
            Object a = methods.get("getA").invoke(df);
            assertTrue(a instanceof Double);
            assertEquals(1., (Double) a);

            methods.get("setB").invoke(df, "aba");
            Object b = methods.get("getB").invoke(df);
            assertTrue(b instanceof String);
            assertEquals("aba", b);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unexpected exception: public method is not accessible");
        } catch (InvocationTargetException | ClassCastException e) {
            throw new AssertionError("Exception while executing methods");
        }
    }

    private Set<String> getMethodNames() {
        Set<String> methodsNames = new HashSet<>();
        methodsNames.add("getA");
        methodsNames.add("getB");
        methodsNames.add("setA");
        methodsNames.add("setB");
        return methodsNames;
    }
}
