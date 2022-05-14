package io.github.tolisso.easycsv.generator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class GeneratorValidatorUtil {
    public static Map<String, Method> validateAndGetMethods(Class<?> clazz, Set<String> methodsNames) {
        Set<String> defaultMethods = Arrays.stream(Object.class.getMethods())
                .map(Method::getName).collect(Collectors.toSet());
        Map<String, Method> methodNameToMethod = new HashMap<>();

        int methodCnt = 0;
        for (Method method : clazz.getMethods()) {
            if (defaultMethods.contains(method.getName())) {
                continue;
            }

            if (methodsNames.contains(method.getName())) {
                methodNameToMethod.put(method.getName(), method);
                methodCnt++;
            } else {
                throw new AssertionError("Method " + method.getName() + " is unexpected");
            }
        }
        if (methodCnt != methodsNames.size()) {
            throw new AssertionError("Unexpected methods number: expected " + methodsNames.size()
                    + " but was " + methodCnt);
        }
        return methodNameToMethod;
    }


    public static Map<String, Method> getMethods(Class<?> clazz) {
        Set<String> objectMethods = Arrays.stream(Object.class.getMethods())
                .map(Method::getName).collect(Collectors.toSet());
        Map<String, Method> methodNameToMethod = new HashMap<>();

        for (Method method : clazz.getMethods()) {
            if (!objectMethods.contains(method.getName())) {
                methodNameToMethod.put(method.getName(), method);
            }
        }
        return methodNameToMethod;
    }

    /**
     * Get instance of class by default constructor
     */
    public static Object getInstance(Class<?> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new AssertionError("No default constructor", e);
        } catch (InvocationTargetException e) {
            throw new AssertionError("Exception by executing constructor", e);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Constructor not accessible or private", e);
        } catch (InstantiationException e) {
            throw new AssertionError("Class is abstract", e);
        }
    }
}
