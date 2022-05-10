package com.tolisso.easycsv.generator;

import com.sun.codemodel.*;
import com.tolisso.easycsv.parser.ClassInfo;
import com.tolisso.easycsv.parser.CsvParser;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

public class ParentClassGenerator extends Generator {

    private final String defaultPathToCsv;
    private JCodeModel codeModel;
    private JDefinedClass clazz;

    public ParentClassGenerator(ClassInfo classInfo, String defaultPathToCsv) {
        super(classInfo.getPackageName(), classInfo.getClassName());
        this.defaultPathToCsv = defaultPathToCsv;
    }

    @Override
    protected JCodeModel generateCodeModel() throws JClassAlreadyExistsException {
        codeModel = new JCodeModel();
        JPackage pack = codeModel._package(String.join(".", packages));
        clazz = pack._class(getParentName());
        JMethod loadFromFileJMethod = generateLoadFromFileMethod();
        generateLoadMethod(loadFromFileJMethod);
        return codeModel;
    }

    private void generateLoadMethod(JMethod loadFromFileJMethod) {
        JClass dfJClass =  codeModel.directClass(getDfName());
        JMethod loadJMethod = clazz.method(STATIC + PUBLIC, dfJClass, "load");
        loadJMethod.body()._return(JExpr.invoke(loadFromFileJMethod).arg(defaultPathToCsv));
    }

    private JMethod generateLoadFromFileMethod() {
        JClass dfJClass =  codeModel.directClass(getDfName());
        JMethod loadJMethod = clazz.method(STATIC + PUBLIC, dfJClass, "loadFromFile");
        JVar pathToCsvParam = loadJMethod.param(String.class, "pathToCsv");
        JInvocation csvParser = _new(codeModel.ref(CsvParser.class));
        JInvocation dataInfo = csvParser.invoke("getInfoFromFile").arg(pathToCsvParam);
        JInvocation mapList = dataInfo.invoke("getValues");
        loadJMethod.body()._return(dfJClass.staticInvoke("fromMapList").arg(mapList));
        return loadJMethod;
    }

    @Override
    protected String getGeneratedClassName() {
        return getParentName();
    }
}
