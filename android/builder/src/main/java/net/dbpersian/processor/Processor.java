package net.dbpersian.processor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import net.dbpersian.annotations.db.Database;
import net.dbpersian.annotations.db.DbTable;


/**
 * Processor.
 * @author tpochkin
 */
@SupportedAnnotationTypes({"net.dbpersian.annotations.db.DbTable"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor
{
    public Processor()
    {
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        final LinkedList<DbTableDescr> dbTableDescrs = new LinkedList<DbTableDescr>();
        for (Element e : roundEnv.getElementsAnnotatedWith(DbTable.class)) {
            if (e.getKind() != ElementKind.CLASS) {
                throw new RuntimeException("@DbTable must be a class level annotation, but used in " + e);
            }
            final DbTableDescr dbTableDescr = buildDaoClass((TypeElement)e);
            dbTableDescrs.add(dbTableDescr);
        }

        for (Element e : roundEnv.getElementsAnnotatedWith(Database.class)) {
            if (e.getKind() != ElementKind.CLASS) {
                throw new RuntimeException("@Database must be a class level annotation, but used in " + e);
            }
            buildDatabaseHelperClass((TypeElement)e, dbTableDescrs);
        }

        return true;
    }

    private void buildDatabaseHelperClass(TypeElement dbHelperElement, LinkedList<DbTableDescr> dbTableDescrs)
    {
        try {
            final DatabaseHelperClassBuilder dbHelperClassBuilder = new DatabaseHelperClassBuilder(
                    dbHelperElement, processingEnv, dbTableDescrs);
            dbHelperClassBuilder.build();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private DbTableDescr buildDaoClass(TypeElement dbTableElement)
    {
        try {
            final DAOClassBuilder daoClassBuilder = new DAOClassBuilder(dbTableElement, processingEnv);
            return daoClassBuilder.build();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
