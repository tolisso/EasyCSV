package com.tolisso.easycsv.generator;

import com.sun.codemodel.*;
import com.tolisso.easycsv.Util;
import com.tolisso.easycsv.parser.ClassInfo;
import com.tolisso.easycsv.parser.DataInfo;

import java.util.Map;
import java.util.stream.IntStream;

import static com.sun.codemodel.JMod.*;

public class RowGenerator extends Generator {
    private final DataInfo dataInfo;

    public RowGenerator(DataInfo dataInfo, ClassInfo classInfo) {
        super(classInfo.getPackageName(), classInfo.getClassName());
        this.dataInfo = dataInfo;
    }

    public JCodeModel generateCodeModel() throws JClassAlreadyExistsException {
        JCodeModel codeModel = new JCodeModel();
        JPackage pack = codeModel._package(String.join(".", packages));
        JDefinedClass clazz = pack._class(getRowName());
        IntStream.range(0, dataInfo.getRowSize())
                .forEach(i -> addField(clazz, i));
        IntStream.range(0, dataInfo.getRowSize())
                .forEach(i -> addGetter(clazz, i));
        IntStream.range(0, dataInfo.getRowSize())
                .forEach(i -> addSetter(clazz, i));
        addBasicConstructor(clazz);
        addMapConstructor(codeModel, clazz);
        return codeModel;
    }

    private void addField(JDefinedClass clazz, int i) {
        clazz.field(PRIVATE, dataInfo.getTypes().get(i).clazz, dataInfo.getNames().get(i));
    }

    private void addBasicConstructor(JDefinedClass clazz) {
        clazz.constructor(PUBLIC);
    }

    private void addMapConstructor(JCodeModel codeModel, JDefinedClass clazz) {
        JClass map = codeModel.ref(Map.class);
        JClass mapWithGenerics = map.narrow(String.class, Object.class);

        JMethod constructor = clazz.constructor(PROTECTED);
        String valueMap = "map";
        constructor.param(mapWithGenerics, valueMap);
        JBlock body = constructor.body();
        for (int i = 0; i < dataInfo.getRowSize(); i++) {
            String name = dataInfo.getNames().get(i);
            body.directStatement("this." + name
                    + " = ("
                    + dataInfo.getTypes().get(i).clazz.getSimpleName()
                    + ") "
                    + valueMap + ".get(\"" + name + "\");");
        }
    }

    private void addGetter(JDefinedClass clazz, int pos) {
        String fieldName = dataInfo.getNames().get(pos);

        String methodName = "get" + Util.upFirstChar(fieldName);
        Class<?> type = dataInfo.getTypes().get(pos).clazz;
        clazz.method(PUBLIC, type, methodName)
                .body()
                ._return(JExpr.direct("this." + fieldName));
    }

    private void addSetter(JDefinedClass clazz, int pos) {
        String fieldName = dataInfo.getNames().get(pos);

        String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        Class<?> type = dataInfo.getTypes().get(pos).clazz;
        JMethod method = clazz.method(PUBLIC, void.class, methodName);
        method.param(type, fieldName);
        method.body().directStatement("this." + fieldName + " = " + fieldName + ";");
    }

    @Override
    protected String getGeneratedClassName() {
        return getRowName();
    }
}
