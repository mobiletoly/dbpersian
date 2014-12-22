package net.dbpersian.processor;

import java.util.List;
import javax.lang.model.element.TypeElement;

import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbTable;

class DbTableDescr
{
    private static final String DAO_CLASS_NAME_FMT = "%sDAO";

    private final TypeElement dbTableElement;
    private final DbTable dbTable;
    private final String sqlCreateTable;
    private final List<DbColumnDescr> dbColumnDescrList;
    private final List<DbForeignKeyReaderDescr> dbForeignKeyReaderDescrs;
    private final String daoJavaClassName;
    private final String daoJavaVariableName;

    public DbTableDescr(TypeElement dbTableElement,
                        DbTable dbTable,
                        List<DbColumnDescr> dbColumnDescrList,
                        List<DbForeignKeyReaderDescr> dbForeignKeyReaderDescrs)
    {
        this.dbTableElement = dbTableElement;
        this.dbTable = dbTable;
        this.dbColumnDescrList = dbColumnDescrList;
        this.dbForeignKeyReaderDescrs = dbForeignKeyReaderDescrs;
        sqlCreateTable = sqlCreateTable();
        daoJavaClassName = String.format(DAO_CLASS_NAME_FMT, this.dbTableElement.getSimpleName());
        this.daoJavaVariableName = Character.toLowerCase(daoJavaClassName.charAt(0)) + daoJavaClassName.substring(1);
    }

    /**
     * @return      SQL statement to create a table.
     */
    public String getSqlCreateTable()
    {
        return sqlCreateTable;
    }

    /**
     * @return      Table name in database.
     */
    public String getSqlName()
    {
        return dbTable.sqlName();
    }

    /**
     * @return      Java class name for table in database.
     */
    public String getJavaClassName()
    {
        return dbTableElement.getSimpleName().toString();
    }

    /**
     * @return      List of column descriptors for this table.
     */
    public List<DbColumnDescr> getDbColumnDescrList() {
        return dbColumnDescrList;
    }

    public List<DbForeignKeyReaderDescr> getDbForeignKeyReaderDescrs()
    {
        return dbForeignKeyReaderDescrs;
    }

    /**
     * @return      Java name for DAO class.
     */
    public String getDaoJavaClassName()
    {
        return daoJavaClassName;
    }

    /**
     * @return      Java name for variable of DAO class type.
     */
    public String getDaoJavaVariableName()
    {
        return daoJavaVariableName;
    }

    /**
     * @return      true, if this table contains primary key.
     */
    public boolean isContainPrimaryKey()
    {
        for (final DbColumnDescr dbColumnDescr : dbColumnDescrList) {
            if (dbColumnDescr.isPrimaryKey()) {
                return true;
            }
        }
        return false;
    }

    public String getOnEntityLoad()
    {
        if (dbTable.onEntityLoad() == null || dbTable.onEntityLoad().isEmpty()) {
            return null;
        }
        return dbTable.onEntityLoad();
    }

    /**
     * Prepare SQL statement to create a table.
     */
    private String sqlCreateTable()
    {
        final StringBuilder sbld = new StringBuilder("CREATE TABLE ");
        sbld.append(dbTable.sqlName());
        sbld.append("(\"\n");
        buildColumnListToCreateTable(sbld);
        buildForeignKeyConstraintsToCreateTable(sbld);
        sbld.append("                + \");");
        return sbld.toString();
    }

    private void buildColumnListToCreateTable(StringBuilder sbld)
    {
        boolean isFirst = true;
        for (final DbColumnDescr dbColumnDescr : dbColumnDescrList) {
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
    }

    private void buildForeignKeyConstraintsToCreateTable(StringBuilder sbld)
    {
        for (final DbColumnDescr dbColumnDescr : dbColumnDescrList) {
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
    }
}
