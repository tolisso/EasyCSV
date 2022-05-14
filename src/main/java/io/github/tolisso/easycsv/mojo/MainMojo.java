package io.github.tolisso.easycsv.mojo;

import io.github.tolisso.easycsv.generator.DfGenerator;
import io.github.tolisso.easycsv.generator.ParentClassGenerator;
import io.github.tolisso.easycsv.generator.RowGenerator;
import io.github.tolisso.easycsv.parser.ClassInfo;
import io.github.tolisso.easycsv.parser.CsvParseException;
import io.github.tolisso.easycsv.parser.CsvParser;
import io.github.tolisso.easycsv.parser.DataInfo;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;

import javax.lang.model.SourceVersion;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mojo(name = "generate-csv",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MainMojo extends AbstractMojo {
    @Parameter(property = "files")
    private List<CsvPathAndName> files;

    @Override
    public void execute() throws MojoFailureException {
        validate(files);
        try {
            for (CsvPathAndName file : files) {
                ClassInfo info = new ClassInfo(file.getClassName());
                DataInfo di = new CsvParser().getInfoFromFile(file.getPathToCsv());
                new RowGenerator(di, info).generate();
                new DfGenerator(di, info).generate();
                new ParentClassGenerator(info, file.getPathToCsv()).generate();
            }
        } catch (CsvParseException exc) {
            throw new MojoFailureException("Can't parse csv", exc);
        } catch (Exception exc) {
            throw new MojoFailureException("Something went wrong", exc);
        }
    }

    private void validate(List<CsvPathAndName> csvPathAndNameList) throws MojoFailureException {
        Set<String> classNameSet = new HashSet<>();
        for (CsvPathAndName info : csvPathAndNameList) {
            String className = info.getClassName();
            if (classNameSet.contains(className)) {
                throw new MojoFailureException("Class name " + className + " is duplicated");
            }
            classNameSet.add(className);
            if (!SourceVersion.isName(className)) {
                throw new MojoFailureException("Class name '" + className + "' not valid name");
            }
        }

    }
}
