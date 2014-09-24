package net.dbpersian.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbForeignKeyReader;
import net.dbpersian.annotations.db.DbTable;
import org.stringtemplate.v4.ST;

/**
 * DAO class builder.
 * @author tpochkin
 */
public class DAOClassBuilder
{
    private final TypeElement mDbTableElement;
    private final ProcessingEnvironment mProcessingEnv;

    private static final String TEMPLATE_PATH = "/net/dbpersian/processor/templates/dao.st";

    private final String mTemplate;

    public DAOClassBuilder(TypeElement dbTableElement, ProcessingEnvironment processingEnv)
            throws IOException
    {
        mDbTableElement = dbTableElement;
        mProcessingEnv = processingEnv;
        mTemplate = Utilities.readResourceAsString(this.getClass(), TEMPLATE_PATH);
    }

    public DbTableDescr build() throws IOException
    {
        final String packageName = mDbTableElement.getEnclosingElement().asType().toString();

        final DbTable dbTable = mDbTableElement.getAnnotation(DbTable.class);
        final List<DbColumnDescr> dbColumnDescrList = prepareDbColumnDescrList();
        List<DbForeignKeyReaderDescr> fkReaderDescrList = prepareDbForeignKeyReaderDescrList(dbColumnDescrList);
        final DbTableDescr dbTableDescr = new DbTableDescr(mDbTableElement, dbTable, dbColumnDescrList,
                fkReaderDescrList);

        final String fullClassName = packageName + '.' + dbTableDescr.getDaoJavaClassName();
        mProcessingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                "Processing DAO class " + fullClassName);

        final String template = renderTemplate(packageName, dbTableDescr.getDaoJavaClassName(), dbTableDescr);

        final JavaFileObject f = mProcessingEnv.getFiler().createSourceFile(fullClassName);
        final Writer writer = f.openWriter();
        try {
            writer.write(template);
        } finally {
            writer.close();
        }

        return dbTableDescr;
    }

    private String renderTemplate(String packageName, String daoClassName, DbTableDescr dbTableDescr)
    {
        final ST st = new ST(mTemplate);
        st.add("package", packageName);
        st.add("entityClassName", mDbTableElement.getSimpleName().toString());
        st.add("daoClass", daoClassName);
        st.add("columnDescrList", dbTableDescr.getDbColumnDescrList());
        st.add("fkReaderDescrList", dbTableDescr.getDbForeignKeyReaderDescrs());
        st.add("tableDescr", dbTableDescr);
        return st.render();
    }

    private List<DbColumnDescr> prepareDbColumnDescrList()
    {
        final List<DbColumnDescr> dbColumnDescrList = new LinkedList<DbColumnDescr>();
        final List<? extends Element> enclosedElements = mDbTableElement.getEnclosedElements();
        for (final Element enclosedElement : enclosedElements) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                final VariableElement varElement = (VariableElement)enclosedElement;
                final DbColumn dbColumn = varElement.getAnnotation(DbColumn.class);
                if (dbColumn != null) {
                    final DbColumnDescr dbColumnDescr = new DbColumnDescr(mDbTableElement, varElement, dbColumn,
                            mProcessingEnv);
                    dbColumnDescrList.add(dbColumnDescr);
                }
            }
        }
        return dbColumnDescrList;
    }

    private List<DbForeignKeyReaderDescr> prepareDbForeignKeyReaderDescrList(List<DbColumnDescr> dbColumnDescrList)
    {
        final List<DbForeignKeyReaderDescr> fkReaderDescrList = new LinkedList<DbForeignKeyReaderDescr>();
        final List<? extends Element> enclosedElements = mDbTableElement.getEnclosedElements();
        for (final Element enclosedElement : enclosedElements) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                final VariableElement varElement = (VariableElement)enclosedElement;
                final DbForeignKeyReader fkReader = varElement.getAnnotation(DbForeignKeyReader.class);
                if (fkReader != null) {
                    final DbColumnDescr columnDescr = findDbColumnDescrByJavaFieldName(dbColumnDescrList,
                            fkReader.refField());
                    validateForeignKeyReader(varElement, fkReader, columnDescr);
                    final DbForeignKeyReaderDescr fkReaderDescr = new DbForeignKeyReaderDescr(
                            mDbTableElement, varElement, columnDescr);
                    fkReaderDescrList.add(fkReaderDescr);
                }
            }
        }
        return fkReaderDescrList;
    }

    private DbColumnDescr findDbColumnDescrByJavaFieldName(List<DbColumnDescr> dbColumnDescrList, String javaFieldName)
    {
        for (final DbColumnDescr dbColumnDescr : dbColumnDescrList) {
            if (dbColumnDescr.getJavaFieldName().equals(javaFieldName)) {
                return dbColumnDescr;
            }
        }
        return null;
    }

    private void validateForeignKeyReader(VariableElement varElement,
                                          DbForeignKeyReader fkReader,
                                          DbColumnDescr dbColumnDescr)
    {
        DbColumn dbColumn = null;
        if (dbColumnDescr != null) {
            dbColumn = dbColumnDescr.getDbColumn();
        }
        if (dbColumn == null
                || dbColumn.fkTable() == null || dbColumn.fkTable().length() == 0
                || dbColumn.fkColumn() == null || dbColumn.fkColumn().length() == 0) {
            throw new RuntimeException("Field " + mDbTableElement.getSimpleName() + "." +
                    varElement.getSimpleName() + " has refField = '" + fkReader.refField()
                    + "'. Make sure that class field " + fkReader.refField() + " exists and has "
                    + "@DbColumn annotation with proper fkTable and fkColumn parameters.");
        }
    }
}
