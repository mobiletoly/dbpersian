package net.dbpersian.processor;

import java.io.Serializable;
import java.util.*;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbTable;

class DbColumnDescr
{
    private static final String SQLDATATYPE_TEXT = "TEXT";
    private static final String SQLDATATYPE_BLOB = "BLOB";

    private final ProcessingEnvironment mProcessingEnv;
    private final TypeElement dbTableElement;
    private final VariableElement varElement;
    private final DbColumn dbColumn;
    private final String constColumnName;
    private final String sqlDataType;
    private final String sqlCreateDataType;
    private final String sqlCreateIndex;
    private boolean isIndexed;
    private boolean isUnique;
    private boolean isBasicJavaType;
    private boolean isStringJavaType;
    private boolean isArrayJavaType;
    private boolean isBooleanJavaType;
    
    private static final HashMap<TypeKind, String> TYPEKIND_TO_SQLDATATYPE = new HashMap<TypeKind, String>();
    static {
        TYPEKIND_TO_SQLDATATYPE.put(TypeKind.BOOLEAN, "BOOLEAN");
        TYPEKIND_TO_SQLDATATYPE.put(TypeKind.BYTE, "INTEGER");
        TYPEKIND_TO_SQLDATATYPE.put(TypeKind.CHAR, "CHAR(1)");
        TYPEKIND_TO_SQLDATATYPE.put(TypeKind.DOUBLE, "DOUBLE");
        TYPEKIND_TO_SQLDATATYPE.put(TypeKind.FLOAT, "FLOAT");
        TYPEKIND_TO_SQLDATATYPE.put(TypeKind.INT, "INTEGER");
        TYPEKIND_TO_SQLDATATYPE.put(TypeKind.LONG, "INTEGER");
        TYPEKIND_TO_SQLDATATYPE.put(TypeKind.SHORT, "INTEGER");
    }
    
    private static final HashMap<Class, String> CLASS_TO_SQLDATATYPE = new HashMap<Class, String>();
    static {
        CLASS_TO_SQLDATATYPE.put(Boolean.class, "BOOLEAN");
        CLASS_TO_SQLDATATYPE.put(Byte.class, "INTEGER");
        CLASS_TO_SQLDATATYPE.put(Character.class, "CHAR(1)");
        CLASS_TO_SQLDATATYPE.put(Double.class, "DOUBLE");
        CLASS_TO_SQLDATATYPE.put(Float.class, "FLOAT");
        CLASS_TO_SQLDATATYPE.put(Integer.class, "INTEGER");
        CLASS_TO_SQLDATATYPE.put(Long.class, "INTEGER");
        CLASS_TO_SQLDATATYPE.put(Short.class, "INTEGER");
        CLASS_TO_SQLDATATYPE.put(String.class, SQLDATATYPE_TEXT);
    }
    
    private static final HashMap<String, String> BOXEDCLASSNAME_TO_UNBOXEDTYPENAME = new HashMap<String, String>();
    static {
        BOXEDCLASSNAME_TO_UNBOXEDTYPENAME.put("java.lang.Boolean", "boolean");
        BOXEDCLASSNAME_TO_UNBOXEDTYPENAME.put("java.lang.Double", "double");
        BOXEDCLASSNAME_TO_UNBOXEDTYPENAME.put("java.lang.Float", "float");
        BOXEDCLASSNAME_TO_UNBOXEDTYPENAME.put("java.lang.Integer", "int");
        BOXEDCLASSNAME_TO_UNBOXEDTYPENAME.put("java.lang.Long", "long");
        BOXEDCLASSNAME_TO_UNBOXEDTYPENAME.put("java.lang.Short", "short");
    }

    public DbColumnDescr(TypeElement dbTableElement, VariableElement varElement, DbColumn dbColumn,
                         ProcessingEnvironment processingEnvironment)
    {
        mProcessingEnv = processingEnvironment;
        this.dbTableElement = dbTableElement;
        this.dbColumn = dbColumn;
        this.varElement = varElement;
        this.constColumnName = "COLUMN_NAME_" + convertVarNameToConstName(varElement.getSimpleName().toString());
        if (dbColumn.serializable()) {
            this.sqlDataType = null;
            this.sqlCreateDataType = sqlCreateDataType(SQLDATATYPE_BLOB);
            this.sqlCreateIndex = null;
        } else {
            this.sqlDataType = sqlDataType();
            this.sqlCreateDataType = sqlCreateDataType(sqlDataType);
            this.sqlCreateIndex = sqlCreateIndex();
        }
    }

    public DbColumn getDbColumn() {
        return dbColumn;
    }

    @SuppressWarnings("unused")
    public boolean isPrimaryKey()
    {
        return dbColumn.primaryKey();
    }

    public String getSqlName()
    {
        return dbColumn.sqlName();
    }
    
    public String getJavaFieldName()
    {
        return varElement.getSimpleName().toString();
    }

    @SuppressWarnings("unused")
    public String getConstColumnName()
    {
        return constColumnName;
    }

    @SuppressWarnings("unused")
    public String getSqlCreateDataType()
    {
        return sqlCreateDataType;
    }

    @SuppressWarnings("unused")
    public String getSqlCreateIndex()
    {
        return sqlCreateIndex;
    }

    @SuppressWarnings("unused")
    public boolean isUnique()
    {
        return isUnique;
    }

    @SuppressWarnings("unused")
    public boolean isIndexed()
    {
        return isIndexed;
    }

    @SuppressWarnings("unused")
    public boolean isBasicJavaType()
    {
        return isBasicJavaType;
    }

    @SuppressWarnings("unused")
    public String getCapitalizedJavaFieldName()
    {
        return Utilities.capitalize(varElement.getSimpleName().toString());
    }

    @SuppressWarnings("unused")
    public boolean isStringJavaType()
    {
        return this.isStringJavaType;
    }

    @SuppressWarnings("unused")
    public boolean isArrayJavaType() { return this.isArrayJavaType; }
    
    public String getJavaFieldType()
    {
        return this.varElement.asType().toString();
    }

    public boolean isSerializable()
    {
        return this.dbColumn.serializable();
    }

    public boolean isBoolean()
    {
        return this.varElement.asType().toString().equals("boolean");
    }
    
    private static String convertVarNameToConstName(String name)
    {
        final StringBuilder sbld = new StringBuilder(name.length() + 10);
        for (int i = 0, count = name.length(); i < count; i++) {
            char ch = name.charAt(i);
            if (i > 0 && Character.isUpperCase(ch)) {
                sbld.append('_');
            }
            sbld.append(Character.toUpperCase(ch));
        }
        return sbld.toString();
    }
    
    private String sqlDataType()
    {
        final TypeMirror type = this.varElement.asType();
        final TypeKind typeKind = type.getKind();
        if (typeKind.equals(TypeKind.DECLARED)) {
            final String dataType = sqlDataTypeCorrespondingToJavaClass((DeclaredType)type);
            if (dataType == null) {
                throw new RuntimeException("UNKNOWN TYPE*********");
            }
            this.isStringJavaType = dataType.equals(SQLDATATYPE_TEXT);
            return dataType;
        } else {
            String dataType = TYPEKIND_TO_SQLDATATYPE.get(typeKind);
            if (dataType == null) {
                if (typeKind.equals(TypeKind.ARRAY)) {
                    this.isArrayJavaType = true;
                    dataType = SQLDATATYPE_BLOB;
                }
                else {
                    throw new RuntimeException("Unrecognized data type <" + typeKind + "> of field <"
                            + varElement + "> in class <" + dbTableElement + ">");
                }
            }
            this.isBasicJavaType = true;
            return dataType;
        }
    }

    private String sqlDataTypeCorrespondingToJavaClass(DeclaredType type)
    {
        final TypeElement asType = (TypeElement)type.asElement();
        final String className = asType.getQualifiedName().toString();
        final Class elementClass;
        try {
            elementClass = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            System.err.println(
                    "*** Cannot process class <" + className + ">. If you wanted to read/write serialized class "
                    + "as column in database, make sure to use serializable parameter in @DbColumn annotation: "
                    + "@DbColumn(..., serializable = true)");
            throw new RuntimeException(ex);
        }
        final String dataType = CLASS_TO_SQLDATATYPE.get(elementClass);
        if (dataType != null) {
            return dataType;
        }

        /*if (elementClass.(Serializable.class)) {
            throw new RuntimeException("**** " + elementClass);
        }*/
        return null;
    }

    private String sqlCreateDataType(String sqlDataType)
    {
        //final String indexName = this.dbColumn.indexName();
        final StringBuilder sqlDataTypeBld = new StringBuilder(sqlDataType);
        if (dbColumn.primaryKey()) {
            sqlDataTypeBld.append(" PRIMARY KEY");
            this.isIndexed = true;
            this.isUnique = true;
        }
        else if (dbColumn.notNull()) {
            sqlDataTypeBld.append(" NOT NULL");
        }
        return sqlDataTypeBld.toString();
    }
    
    private String sqlCreateIndex()
    {
        if (this.dbColumn.primaryKey()) {
            return null;
        }
        final String sqlIndexName = this.dbColumn.indexName();
        if (sqlIndexName == null || sqlIndexName.length() == 0) {
            return null;
        }
        
        final DbTable dbTable = dbTableElement.getAnnotation(DbTable.class);
        
        final StringBuilder sqlCreateIndexBld = new StringBuilder("CREATE");
        if (this.dbColumn.unique()) {
            sqlCreateIndexBld.append(" UNIQUE");
            this.isUnique = true;
            new LinkedHashMap<Integer,Integer>();
        }
        sqlCreateIndexBld.append(" INDEX ");
        sqlCreateIndexBld.append(sqlIndexName);
        sqlCreateIndexBld.append(" ON ");
        sqlCreateIndexBld.append(dbTable.sqlName());
        sqlCreateIndexBld.append('(');
        sqlCreateIndexBld.append(this.dbColumn.sqlName());
        sqlCreateIndexBld.append(")");
        sqlCreateIndexBld.append(';');

        this.isIndexed = true;
        return sqlCreateIndexBld.toString();
    }

    @SuppressWarnings("unused")
    public String getMethodNameToReadFromCursor() {
        if (this.isStringJavaType) {
            return "getString";
        }
        String javaFieldType;
        final String className = getJavaFieldType();
        if (this.isBasicJavaType) {
            javaFieldType = className;
        } else {
            javaFieldType = BOXEDCLASSNAME_TO_UNBOXEDTYPENAME.get(className);
            if (javaFieldType == null) {
                throw new RuntimeException("!!!Unrecognized data type <" + className + "> of field <"
                        + varElement + "> in class <" + dbTableElement + ">");
            }
        }
        if (javaFieldType.equals("boolean")) {
            javaFieldType = "int";
        }
        return "get" + Utilities.capitalize(javaFieldType);
    }
}
