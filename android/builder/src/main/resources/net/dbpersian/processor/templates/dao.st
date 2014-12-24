// -------------------------------------------------------------------
//
// THIS FILE IS AUTO-GENERATED
// DO NOT EDIT
//
// -------------------------------------------------------------------

package <package>;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * DAO class with helper functions to handle "<tableDescr.sqlName>" table.
 */
public class <daoClass> extends net.dbpersian.api.GenericDAO\<<entityClassName>>
{
    private static final String TAG = "<daoClass>";
    public static final String TABLE_NAME = "<tableDescr.sqlName>";
    
    <columnDescrList:{columnDescr | public static final String <columnDescr.constColumnName> = "<columnDescr.sqlName>";
    }>

    public <daoClass>(SQLiteDatabase db)
    {
        super(db, TABLE_NAME);
    }

    /**
     * Create new table.
     */
    static void createTable(SQLiteDatabase db)
    {
        final String sqlCreate = "<tableDescr.sqlCreateTable>";
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, sqlCreate);
        }
        db.execSQL(sqlCreate);
        <columnDescrList:{columnDescr | <if(columnDescr.sqlCreateIndex)>
if (Log.isLoggable(TAG, Log.DEBUG)) {
    Log.d(TAG, "<columnDescr.sqlCreateIndex>");
\}
db.execSQL("<columnDescr.sqlCreateIndex>");
        <endif>}>
    }

    <columnDescrList:{columnDescr | <if(columnDescr.indexed)>
/**
<if(columnDescr.unique)>
 * Query a single instance of <entityClassName> class from the table.
<else>
 * Query a list of instances of <entityClassName> class from the table.
<endif>
 * This request is based on "<columnDescr.sqlName>" column (<entityClassName>.<columnDescr.javaFieldType> class member)
 * matching the value provided as a parameter.
 * @param value     Value this request is looking for.
<if(columnDescr.unique)>
 * @return  Instance of <entityClassName> object when object is found; null, otherwise.
<else>
 * @return  List of <entityClassName> objects.
<endif>
 */
    <if(columnDescr.unique)>
public <entityClassName> queryBy<columnDescr.capitalizedJavaFieldName>(<columnDescr.javaFieldType> value)
{
    final String sql = "SELECT * FROM <tableDescr.sqlName> WHERE <columnDescr.sqlName> = ? LIMIT 1";
    <else>
public java.util.List\<<entityClassName>> queryBy<columnDescr.capitalizedJavaFieldName>(<columnDescr.javaFieldType> value)
{
    final String sql = "SELECT * FROM <tableDescr.sqlName> WHERE <columnDescr.sqlName> = ?";
    <endif>

    if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, sql);
    \}
    <if(columnDescr.basicJavaType)>
        <if(columnDescr.boolean)>
    final String valueToQuery = value ? "1" : "0";
        <else>
    final String valueToQuery = String.valueOf(value);
        <endif>
    <elseif(columnDescr.stringJavaType)>
    final String valueToQuery = value;
    <else>
    final String valueToQuery = value.toString();
    <endif>
    final String[] selections = new String[]{ valueToQuery \};
    <if(columnDescr.unique)>
    return queryForSingleResult(sql, selections);
    <else>
    return query(sql, selections);
    <endif>
\}

/**
 * Delete a single instance of <entityClassName> object from the table.
 * This request is based on "<columnDescr.sqlName>" column (<entityClassName>.<columnDescr.javaFieldType> class member)
 * matching the value provided as a parameter.
 * @param value     Value this request is looking for.
 */
public int deleteBy<columnDescr.capitalizedJavaFieldName>(<columnDescr.javaFieldType> value)
{
    <if(columnDescr.basicJavaType)>
        <if(columnDescr.boolean)>
    final String valueToQuery = value ? "1" : "0";
        <else>
    final String valueToQuery = String.valueOf(value);
        <endif>
    <elseif(columnDescr.stringJavaType)>
    final String valueToQuery = value;
    <else>
    final String valueToQuery = value.toString();
    <endif>
    final String[] whereArgs = new String[]{ valueToQuery \};
    return delete("<columnDescr.sqlName>=?", whereArgs);
\}

<endif>}>

    <columnDescrList:{columnDescr | <if(columnDescr.unique)>
/**
 * Query all instances of <entityClassName> objects from the table and putting them in map with
 * a key based on "<columnDescr.sqlName>" column (<entityClassName>.<columnDescr.javaFieldType> class member).
 * @param orderBy
 *          part of the SQL query that comes after "ORDER BY" clause. null, if you don't need order.
 * @return  Map with instances of <entityClassName> object.
 */
public LinkedHashMap\<<columnDescr.javaFieldType>,<entityClassName>> queryAllAs<columnDescr.capitalizedJavaFieldName>Map(String orderBy)
{
    final Collection\<<entityClassName>> entities = queryAll(orderBy);
    final LinkedHashMap\<<columnDescr.javaFieldType>,<entityClassName>> map = new LinkedHashMap\<<columnDescr.javaFieldType>,<entityClassName>>(entities.size());
    for (final <entityClassName> entity : entities) {
        map.put(entity.<columnDescr.javaFieldName>, entity);
    \}
    return map;
\}<endif>}>

    @Override
    protected <entityClassName> readEntityFromCursor(Cursor c, CursorMetaEntry meta)
    {
        final <entityClassName> entity = new <entityClassName>();
        final HashMap\<String, Integer> columnNamesToIndexes = meta.columnNamesToIndexes;
        <columnDescrList:{columnDescr |
final Integer <columnDescr.javaFieldName>Idx = columnNamesToIndexes.get("<columnDescr.sqlName>");
if (<columnDescr.javaFieldName>Idx != null) {
    <if(columnDescr.arrayJavaType||columnDescr.serializable)>
    entity.<columnDescr.javaFieldName> = (<columnDescr.javaFieldType>)deserializeBlob(c.getBlob(<columnDescr.javaFieldName>Idx));
    <elseif(columnDescr.boolean)>
    entity.<columnDescr.javaFieldName> = c.<columnDescr.methodNameToReadFromCursor>(<columnDescr.javaFieldName>Idx) != 0;
    <else>
    entity.<columnDescr.javaFieldName> = c.<columnDescr.methodNameToReadFromCursor>(<columnDescr.javaFieldName>Idx);
    <endif>
\}
        }>
        if (isAutoFetchForeignKeyReaders()) {
            fetchAllForeignKeyReaders(entity);
        }
    <if(tableDescr.onEntityLoad)>
        entity.<tableDescr.onEntityLoad>();
    <endif>
        return entity;
    }

    @Override
    public ContentValues getEntityContentValues(<entityClassName> entity)
    {
        final ContentValues cv = new ContentValues();
        <columnDescrList:{columnDescr |
        <if(!columnDescr.primaryKey)>
        <if(columnDescr.arrayJavaType||columnDescr.serializable)>
cv.put("<columnDescr.sqlName>", serializeToBlob((Object)entity.<columnDescr.javaFieldName>));
        <else>
cv.put("<columnDescr.sqlName>", entity.<columnDescr.javaFieldName>);
        <endif>
        <endif>
        }>
        return cv;
    }

    /**
     * This call queries for all objects annotated by @DbForeignKeyReader. If auto-fetching of foreign key objects
     * is disabled by calling setAutoFetchForeignKeyReaders(false) then querying for a <entityClassName> object
     * will not result in querying for foreign key objects this object might refer to. So fetchAllForeignKeyReaders()
     * call takes care of that and makes sure that records referred by a foreign key specified by fields:
     <fkReaderDescrList:{fkrDescr |
*     - <fkrDescr.javaFieldName>
}>     * are fetched from a database.
     * @param entity
     *      Entity object to resolve foreign key objects for.
     */
    public void fetchAllForeignKeyReaders(<entityClassName> entity)
    {
        <fkReaderDescrList:{fkrDescr |
fetch<fkrDescr.capitalizedJavaFieldName>(entity);
        }>
    }

    <fkReaderDescrList:{fkrDescr |
/**
 * Fetch a record from database to resolve a foreign key referred by "<fkrDescr.javaFieldName>" field.
 * @param entity
 *      Entity object to resolve foreign key object for.
 */
public void fetch<fkrDescr.capitalizedJavaFieldName>(<entityClassName> entity)
{
    final <fkrDescr.javaFieldType>DAO dao = new <fkrDescr.javaFieldType>DAO(database);
    final <fkrDescr.javaFieldType> entityRef = dao.queryBy<fkrDescr.capitalizedRefColumnName>(entity.<fkrDescr.refDbColumnDescr.javaFieldName>);
    entity.<fkrDescr.javaFieldName> = entityRef;
\}

}>

    /**
     * Insert new entity into table.
     */
    @Override
    public void insert(<entityClassName> entity) throws SQLException
    {
        final ContentValues cv = getEntityContentValues(entity);
        <if(tableDescr.containPrimaryKey)>
        <columnDescrList:{columnDescr | <if(columnDescr.primaryKey)>
entity.<columnDescr.javaFieldName> = (<columnDescr.javaFieldType>)database.insertOrThrow(tableName, null, cv);
        <endif>
            }>
        <else>
        database.insertOrThrow(tableName, null, cv);
        <endif>
    }


    /**
     * Update entity.
     */
    @Override
    public void update(<entityClassName> entity) throws SQLException
    {
        final ContentValues cv = getEntityContentValues(entity);
        String whereClause = null;
        String whereArgs = null;
        <columnDescrList:{columnDescr | <if(columnDescr.primaryKey)>
whereClause = "<columnDescr.sqlName> = ?";
whereArgs = entity.<columnDescr.javaFieldName>.toString();
        <endif>
        }>
        final int numUpdatedRows = database.update(tableName, cv, whereClause, new String[]{whereArgs});
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Number of updated records: " + numUpdatedRows);
        }
    }
}
