package net.dbpersian.processor;

import java.util.List;
import javax.lang.model.element.TypeElement;

import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbTable;

class DbTableDescr
{
    private static final String DAO_CLASS_NAME_FMT = "%sDAO";

    private final TypeElement mDbTableElement;
    private final DbTable mDbTable;
    private final String mSqlCreateTable;
    private final List<DbColumnDescr> mDbColumnDescrList;
    private final List<DbForeignKeyReaderDescr> mDbForeignKeyReaderDescrs;
    private final String mDaoJavaClassName;

    public DbTableDescr(TypeElement dbTableElement,
                        DbTable dbTable,
                        List<DbColumnDescr> dbColumnDescrList,
                        List<DbForeignKeyReaderDescr> dbForeignKeyReaderDescrs)
    {
        mDbTableElement = dbTableElement;
        mDbTable = dbTable;
        mDbColumnDescrList = dbColumnDescrList;
        mDbForeignKeyReaderDescrs = dbForeignKeyReaderDescrs;
        mSqlCreateTable = sqlCreateTable();
        mDaoJavaClassName = String.format(DAO_CLASS_NAME_FMT, this.mDbTableElement.getSimpleName());
    }

    /**
     * @return      SQL statement to create a table.
     */
    public String getSqlCreateTable()
    {
        return mSqlCreateTable;
    }

    /**
     * @return      Table name.
     */
    public String getSqlName()
    {
        return mDbTable.sqlName();
    }

    public String getJavaClassName()
    {
        return mDbTableElement.getSimpleName().toString();
    }

    public List<DbColumnDescr> getDbColumnDescrList() {
        return mDbColumnDescrList;
    }

    public List<DbForeignKeyReaderDescr> getDbForeignKeyReaderDescrs() {
        return mDbForeignKeyReaderDescrs;
    }

    public String getDaoJavaClassName()
    {
        return mDaoJavaClassName;
    }

    public boolean isContainPrimaryKey()
    {
        for (final DbColumnDescr dbColumnDescr : mDbColumnDescrList) {
            if (dbColumnDescr.isPrimaryKey()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prepare SQL statement to create a table.
     */
    private String sqlCreateTable()
    {
        final StringBuilder sbld = new StringBuilder("CREATE TABLE ");
        sbld.append(mDbTable.sqlName());
        sbld.append("(\"\n");
        boolean isFirst = true;
        for (final DbColumnDescr dbColumnDescr : mDbColumnDescrList) {
            sbld.append("                + \"");
            if (isFirst) {
                isFirst = false;
                sbld.append("  ");
            } else {
                sbld.append(", ");
            }
            sbld.append(dbColumnDescr.getSqlName());
            sbld.append(' ');
            sbld.append(dbColumnDescr.getSqlCreateDataType());
            sbld.append("\"\n");
        }
        for (final DbColumnDescr dbColumnDescr : mDbColumnDescrList) {
            DbColumn dbColumn = dbColumnDescr.getDbColumn();
            if (dbColumn.fkTable() != null && dbColumn.fkTable().length() > 0) {
                if (dbColumn.fkColumn() == null || dbColumn.fkColumn().length() == 0) {
                    throw new RuntimeException("Field " + dbColumnDescr.getJavaFieldName() + " has reference" +
                            " to table (fkTable), but does not have reference to column (fkColumn)");
                }
                sbld.append("                + \"");
                sbld.append(", ");
                sbld.append("FOREIGN KEY(");
                sbld.append(dbColumnDescr.getSqlName());
                sbld.append(") REFERENCES ");
                sbld.append(dbColumn.fkTable());
                sbld.append('(');
                sbld.append(dbColumn.fkColumn());
                sbld.append(")\"\n");
            }
        }
        sbld.append("                + \");");
        return sbld.toString();
    }
}
