package com.tolisso.easycsv.generator;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.tolisso.easycsv.generator.GeneratorValidatorUtil.validateAndGetMethods;

public class ParentGeneratorValidator {

    public void validate(Class<?> parentClass, Class<?> dfClass) {
        assertEquals("Caba", parentClass.getSimpleName());

        Set<String> methodNames = getMethodNames();
        Map<String, Method> methods = validateAndGetMethods(parentClass, methodNames);

        assertEquals(dfClass, methods.get("load").getReturnType());
        assertEquals(dfClass, methods.get("loadFromFile").getReturnType());
        assertEquals(1, methods.get("loadFromFile").getParameterCount());
        assertEquals(String.class, methods.get("loadFromFile").getParameterTypes()[0]);
    }

    private Set<String> getMethodNames() {
        Set<String> methodsNames = new HashSet<>();
        methodsNames.add("load");
        methodsNames.add("loadFromFile");
        return methodsNames;
    }
}
