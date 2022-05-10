package com.tolisso.easycsv.generator;

import com.sun.codemodel.*;
import com.tolisso.easycsv.parser.ClassInfo;
import com.tolisso.easycsv.parser.DataInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sun.codemodel.JMod.*;
import static com.tolisso.easycsv.Util.upFirstChar;

public class DfGenerator extends Generator {

    private final DataInfo dataInfo;
    private JCodeModel codeModel;
    private JDefinedClass clazz;

    public DfGenerator(DataInfo dataInfo, ClassInfo classInfo) {
        super(classInfo.getPackageName(), classInfo.getClassName());
        this.dataInfo = dataInfo;
    }

    public JCodeModel generateCodeModel() throws JClassAlreadyExistsException {
        codeModel = new JCodeModel();
        JPackage pack = codeModel._package(String.join(".", packages));
        String className = getDfName();
        clazz = pack._class(className);

        generateDataField();
        for (int i = 0; i < dataInfo.getRowSize(); i++) {
            generateGetter(i);
        }
        generateListOfMapsCreateMethod();
        generateSimpleConstructor();
        generateDataConstructor();
        generateDataGetter();

        return codeModel;
    }

    private void generateDataGetter() {
        JMethod dataGetter = clazz.method(PUBLIC, getDataJClass(), "getData");
        dataGetter.body()._return(JExpr._this().ref("data"));
    }

    private void generateDataConstructor() {
        JMethod constructor = clazz.constructor(PUBLIC);
        JVar dataParam = constructor.param(getDataJClass(), "data");
        constructor.body().assign(JExpr._this().ref("data"), dataParam);
    }

    private JClass getDataJClass() {
        JClass listJClass = codeModel.ref(List.class);
        return listJClass.narrow(codeModel.directClass(getRowName()));
    }

    private void generateSimpleConstructor() {
        JMethod constructor = clazz.constructor(PUBLIC);
        JClass arrListJClass = codeModel.ref(ArrayList.class);
        JClass dataFieldJClass = arrListJClass.narrow(codeModel.directClass(getRowName()));
        constructor.body().assign(JExpr._this().ref("data"), JExpr._new(dataFieldJClass));
    }

    private void generateListOfMapsCreateMethod() {
        JClass listJClass = codeModel.ref(List.class);
        JClass mapJClass = codeModel.ref(Map.class);
        JClass narrowedMapJClass = mapJClass.narrow(String.class, Object.class);
        JMethod constructor = clazz.method(PROTECTED + STATIC, clazz, "fromMapList");
        constructor.param(listJClass.narrow(narrowedMapJClass), "rawData");
        constructor.body()._return(JExpr._new(clazz).arg(JExpr.direct("rawData.stream().map(" + getRowName() + "::new).collect(Collectors.toList())")));
    }

    private void generateGetter(int i) {
        JClass collectorsJClass = codeModel.ref(Collectors.class);
        JClass listJClass = codeModel.ref(List.class);
        JClass retType = listJClass.narrow(dataInfo.getTypes().get(i).clazz);
        String methodName = "get" + upFirstChar(dataInfo.getNames().get(i)) + "List";
        String rowValueGetterName = "get" + upFirstChar(dataInfo.getNames().get(i));
        JMethod method = clazz.method(PUBLIC, retType, methodName);
        method.body()
                ._return(JExpr.direct("data.stream().map(row -> row." + rowValueGetterName + "())")
                .invoke("collect").arg(collectorsJClass.staticInvoke("toList")));
    }

    private void generateDataField() {
        String rowName = getRowName();
        codeModel.ref(List.class);
        clazz.direct("private final List<" + rowName + "> data;");
    }

    @Override
    protected String getGeneratedClassName() {
        return getDfName();
    }
}
