package net.dbpersian.processor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class DbForeignKeyReaderDescr
{
    private TypeElement mDbTableElement;
    private VariableElement mVarElement;
    private DbColumnDescr mRefDbColumnDescr;


    public DbForeignKeyReaderDescr(TypeElement dbTableElement, VariableElement varElement,
                                   DbColumnDescr refDbColumnDescr)
    {
        mDbTableElement = dbTableElement;
        mVarElement = varElement;
        mRefDbColumnDescr = refDbColumnDescr;
    }

    public DbColumnDescr getRefDbColumnDescr()
    {
        return mRefDbColumnDescr;
    }

    @SuppressWarnings("unused")
    public String getCapitalizedRefColumnName()
    {
        return Utilities.capitalize(mRefDbColumnDescr.getDbColumn().fkColumn());
    }

    @SuppressWarnings("unused")
    public String getJavaFieldName()
    {
        return mVarElement.getSimpleName().toString();
    }

    @SuppressWarnings("unused")
    public String getCapitalizedJavaFieldName()
    {
        return Utilities.capitalize(mVarElement.getSimpleName().toString());
    }

    @SuppressWarnings("unused")
    public String getJavaFieldType()
    {
        return mVarElement.asType().toString();
    }
}
