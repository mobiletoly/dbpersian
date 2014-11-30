package net.dbpersian.processor;

import net.dbpersian.annotations.db.Database;
import org.stringtemplate.v4.ST;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * DAO class builder.
 * @author tpochkin
 */
public class DatabaseHelperClassBuilder
{
    private final TypeElement dbElement;
    private final ProcessingEnvironment processingEnv;
    private final List<DbTableDescr> dbTableDescrs;

    private static final String TEMPLATE_PATH = "/net/dbpersian/processor/templates/dbhelper.st";
    private static final String DBHELPER_CLASS_NAME_FMT = "Abstract%s";

    private final String template;

    public DatabaseHelperClassBuilder(TypeElement dbElement, ProcessingEnvironment processingEnv,
                                      List<DbTableDescr> mDbTableDescrs)
            throws IOException
    {
        this.dbElement = dbElement;
        this.processingEnv = processingEnv;
        this.dbTableDescrs = mDbTableDescrs;
        this.template = Utilities.readResourceAsString(this.getClass(), TEMPLATE_PATH);
    }

    public void build() throws IOException
    {
        final String packageName = this.dbElement.getEnclosingElement().asType().toString();
        final String dbHelperClassName = String.format(DBHELPER_CLASS_NAME_FMT, this.dbElement.getSimpleName());

        final String fullClassName = packageName + '.' + dbHelperClassName;
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                "Processing database helper class " + fullClassName);

        final String template = renderTemplate(packageName, dbHelperClassName);

        final JavaFileObject f = processingEnv.getFiler().createSourceFile(fullClassName);
        final Writer writer = f.openWriter();
        try {
            writer.write(template);
        } finally {
            writer.close();
        }
    }

    private String renderTemplate(String packageName, String dbHelperClassName)
    {
        final Database database = dbElement.getAnnotation(Database.class);
        final DatabaseDescr dbDescr = new DatabaseDescr(dbElement, database);
        final ST st = new ST(template);
        st.add("package", packageName);
        st.add("dbHelperClass", dbHelperClassName);
        st.add("dbDescr", dbDescr);
        st.add("dbTableDescrs", dbTableDescrs);
        return st.render();
    }
}
