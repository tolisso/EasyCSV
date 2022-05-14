package io.github.tolisso.easycsv.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static io.github.tolisso.easycsv.generator.GeneratorValidatorUtil.*;

public class DfGeneratorValidator {

    public void validate(Class<?> dfClass, Class<?> rowClass) {
        assertEquals("CabaDf", dfClass.getSimpleName());

        Set<String> methodNames = getMethodNames();
        Map<String, Method> methods = validateAndGetMethods(dfClass, methodNames);

        Constructor<?> dfListConstructor;
        try {
            dfListConstructor = dfClass.getConstructor(List.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("No list constructor class");
        }

        try {
            // creating row instance and row list
            Map<String, Method> rowMethods = getMethods(rowClass);
            Object row = getInstance(rowClass);
            rowMethods.get("setA").invoke(row, 2.5);
            rowMethods.get("setB").invoke(row, "b");
            List<Object> rowList = new ArrayList<>();
            rowList.add(row);

            // creating Df instance
            Object df = dfListConstructor.newInstance(rowList);

            validateColumnGetters(methods, df);
            validateDataGetter(methods, row, df);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Can't access to method", e);
        } catch (InvocationTargetException e) {
            throw new AssertionError("Method has thrown exception", e);
        } catch (InstantiationException e) {
            throw new AssertionError("Constructor has thrown exception", e);
        }
    }

    private void validateDataGetter(Map<String, Method> methods, Object row, Object df) throws IllegalAccessException, InvocationTargetException {
        List<?> data;
        try {
            data = (List<?>) methods.get("getData").invoke(df);
        } catch (ClassCastException exc) {
            throw new AssertionError("getData return not a List");
        }
        assertNotNull(data);
        assertEquals(1, data.size());
        assertEquals(row, data.get(0));
    }

    @SuppressWarnings("unchecked")
    private void validateColumnGetters(Map<String, Method> methods, Object df) throws IllegalAccessException, InvocationTargetException {
        List<Double> aList;
        try {
            aList = (List<Double>) methods.get("getAList").invoke(df);
        } catch (ClassCastException exc) {
            throw new AssertionError("getAList return not a List<Double>");
        }
        assertNotNull(aList);
        assertEquals(1, aList.size());
        assertEquals(2.5, aList.get(0));

        List<String> bList;
        try {
            bList = (List<String>) methods.get("getBList").invoke(df);
        } catch (ClassCastException exc) {
            throw new AssertionError("getBList return not a List<String>");
        }
        assertNotNull(bList);
        assertEquals(1, bList.size());
        assertEquals("b", bList.get(0));
    }

    private Set<String> getMethodNames() {
        Set<String> methodsNames = new HashSet<>();
        methodsNames.add("getAList");
        methodsNames.add("getBList");
        methodsNames.add("getData");
        return methodsNames;
    }
}
