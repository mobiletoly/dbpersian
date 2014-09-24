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
import android.database.sqlite.SQLiteOpenHelper;
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

    private boolean isAutoFetchForeignKeyReaders = false;
    
    <columnDescrList:{columnDescr | public static final String <columnDescr.constColumnName> = "<columnDescr.sqlName>";
    }>

    private <daoClass>(SQLiteOpenHelper dbOpenHelper, boolean isAsync, OnDatabaseOperationCompleteListener complete)
    {
        super(dbOpenHelper, TABLE_NAME, isAsync, complete);
    }

    private <daoClass>(SQLiteDatabase db)
    {
        super(db, TABLE_NAME);
    }

    /**
     * Creates DAO object. This method is executed synchronously in a context of current thread.
     * If database is not open yet, it will be open. If database is not created yet, it will be created.
     * Since there is always a chance that this method might take some time to finish execution, it is
     * recommended not to call it from main UI thread, unless you are absolutely sure that database is
     * already open.
     * @param dbOpenHelper
     *              Database open helper.
     * @return
     *              New instance of <daoClass> with database open.
     */
    public static <daoClass> open(SQLiteOpenHelper dbOpenHelper)
    {
        return new <daoClass>(dbOpenHelper, false, null);
    }

    /**
     * Creates DAO object. This method is executed asynchronously on its own thread..
     * If database is not open yet, it will be open. If database is not created yet, it will be created.
     * @param dbOpenHelper
     *              Database open helper.
     * @param complete
     *              Completion listener to notify, when database is open and ready to use.
     * @return
     *              New instance of <daoClass>. Since this method is asynchronous, most likely database will
     *              still be closed when this method returns.
     */
    public static <daoClass> openAsync(SQLiteOpenHelper dbOpenHelper, OnDatabaseOperationCompleteListener complete)
    {
        return new <daoClass>(dbOpenHelper, true, complete);
    }

    /**
     * Creates DAO object for already open database. New instance will not owe db object, passed as an argument,
     * calling close() for this DAO object does nothing, so you don't have to call it all.
     */
    public static <daoClass> createForDatabase(SQLiteDatabase db)
    {
        return new <daoClass>(db);
    }

    /**
     * Enable auto-resolving of all foreign key objects to ensure that when user queries for an entity,
     * all fields marked with @DbForeignKeyReader annotation are getting queried from a database. By default
     * this mode is enabled. You can disable auto-fetching and in this case you have to manually call methods
     * to fetch objects referred by foreign keys:
     *     - fetchAllForeignKeyReaders(entity)
     <fkReaderDescrList:{fkrDescr |
*     - fetch<fkrDescr.capitalizedJavaFieldName>(entity)
     }>     * are fetched from a database.
     */
    public void setAutoFetchForeignKeyReaders(boolean flag)
    {
        isAutoFetchForeignKeyReaders = flag;
    }

    /**
     * @return  true, if auto fetch mode to resolve foreign key objects is enabled; false, otherwise.
     */
    public boolean isAutoFetchForeignKeyReaders()
    {
        return isAutoFetchForeignKeyReaders;
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
 * Query a single instance of <entityClassName> object from the table.
 * This request is based on "<columnDescr.sqlName>" column (<entityClassName>.<columnDescr.javaFieldType> class member)
 * matching the value provided as a parameter.
 * @param value     Value this request is looking for.
 * @return  Instance of <entityClassName> object when object is found; null, otherwise.
 */
public <entityClassName> findBy<columnDescr.capitalizedJavaFieldName>(<columnDescr.javaFieldType> value)
{
    final String sql = "SELECT * FROM <tableDescr.sqlName> WHERE <columnDescr.sqlName> = ? LIMIT 1";
    if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, sql);
    \}
    <if(columnDescr.basicJavaType)>
    final String valueToQuery = String.valueOf(value);
    <elseif(columnDescr.stringJavaType)>
    final String valueToQuery = value;
    <else>
    final String valueToQuery = value.toString();
    <endif>
    final String[] selections = new String[]{ valueToQuery \};
    return queryForSingleResult(sql, selections);
\}<endif>}>

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
    final <fkrDescr.javaFieldType>DAO dao = <fkrDescr.javaFieldType>DAO.createForDatabase(mDatabase);
    final <fkrDescr.javaFieldType> entityRef = dao.findBy<fkrDescr.capitalizedRefColumnName>(entity.<fkrDescr.refDbColumnDescr.javaFieldName>);
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
entity.<columnDescr.javaFieldName> = (<columnDescr.javaFieldType>)mDatabase.insertOrThrow(mTableName, null, cv);
        <endif>
            }>
        <else>
        mDatabase.insertOrThrow(mTableName, null, cv);
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
        final int numUpdatedRows = mDatabase.update(mTableName, cv, whereClause, new String[]{whereArgs});
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Number of updated records: " + numUpdatedRows);
        }
    }
}
