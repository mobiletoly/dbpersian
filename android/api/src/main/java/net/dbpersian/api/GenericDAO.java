package net.dbpersian.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.*;
import java.util.*;

/**
 * Abstract DAO class.
 * @author tpochkin
 */
public abstract class GenericDAO<T>
{
    private static final String TAG = GenericDAO.class.getSimpleName();

    private final HashMap<Cursor, CursorMetaEntry> cursorMetaEntries = new HashMap<Cursor, CursorMetaEntry>();
    protected final String tableName;
    protected final SQLiteDatabase database;

    private boolean isAutoFetchForeignKeyReaders = true;


    public GenericDAO(final SQLiteDatabase database, String tableName)
    {
        if (database == null) {
            throw new NullPointerException("<database> parameter: must not be null");
        }
        if (!database.isOpen()) {
            throw new IllegalArgumentException("<database> parameter: database is not open");
        }
        this.database = database;
        this.tableName = tableName;
    }

    /**
     * Enable auto-resolving of all foreign key objects to ensure that when user queries for an entity,
     * all fields marked with @DbForeignKeyReader annotation are getting queried from a database. By default
     * this mode is disabled. You can disable auto-fetching and in this case you have to manually call methods
     * fetchAllForeignKeyReaders() or fetchXXXXXX() to fetch objects referred by foreign keys.
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

    public abstract ContentValues getEntityContentValues(T entity);

    protected abstract T readEntityFromCursor(Cursor c, CursorMetaEntry meta);

    public SQLiteDatabase getDatabase()
    {
        return database;
    }

    public String getTableName()
    {
        return tableName;
    }


    /** Drop table. */
    public void dropTable()
    {
        final String sql = "DROP TABLE " + tableName;
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, sql);
        }
        database.execSQL(sql);
    }

    public T readEntityFromCursor(Cursor c)
    {
        final CursorMetaEntry meta = getCursorMetaEntry(c);
        return readEntityFromCursor(c, meta);
    }

    public T queryForSingleResult(String sql, String[] selectionArgs)
    {
        Cursor c = null;
        try {
            c = database.rawQuery(sql, selectionArgs);
            if (!c.moveToFirst()) { return null; }
            final CursorMetaEntry meta = getCursorMetaEntry(c);
            return readEntityFromCursor(c, meta);
        } finally {
            if (c != null) { c.close(); }
        }
    }

    public List<T> query(String sql, String[] selectionArgs)
    {
        final LinkedList<T> entries = new LinkedList<T>();
        Cursor c = null;
        try {
            c = database.rawQuery(sql, selectionArgs);
            final CursorMetaEntry meta = getCursorMetaEntry(c);
            while (c.moveToNext()) {
                final T entity = readEntityFromCursor(c, meta);
                entries.add(entity);
            }
            return entries;
        } finally {
            if (c != null) { c.close(); }
        }
    }

    /**
     * Query all records with specified order.
     * @param orderBy
     *          part of the SQL query that comes after "ORDER BY" clause. null, if you don't need order.
     * @return
     */
    public List<T> queryAll(String orderBy)
    {
        String sql = "SELECT * FROM " + tableName;
        if (orderBy != null) {
            sql += " ORDER BY " + orderBy;
        }
        return query(sql, null);
    }

    public int delete(String whereClause, String[] whereArgs)
    {
        return database.delete(tableName, whereClause, whereArgs);
    }

    /** Insert entity into table. */
    public abstract void insert(T entity) throws SQLException;

    /** Update entity. */
    public abstract void update(T entity) throws SQLException;

    /**
     * Insert multiple entities (in one transaction) into table.
     */
    public void insert(Collection<T> entities)
    {
        database.beginTransaction();
        try {
            for (final T entity : entities) {
                insert(entity);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void update(Collection<T> entities)
    {
        database.beginTransaction();
        try {
            for (final T entity : entities) {
                update(entity);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private synchronized CursorMetaEntry getCursorMetaEntry(Cursor cursor)
    {
        CursorMetaEntry meta = cursorMetaEntries.get(cursor);
        if (meta != null) {
            return meta;
        }
        // Prior to adding new cursor to a map, we want to remove all cursor that are closed.
        for (Iterator<Map.Entry<Cursor, CursorMetaEntry>> it = cursorMetaEntries.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry<Cursor, CursorMetaEntry> entry = it.next();
            final Cursor c = entry.getKey();
            if (c.isClosed()) {
                it.remove();
            }
        }
        meta = new CursorMetaEntry(cursor);
        cursorMetaEntries.put(cursor, meta);
        return meta;
    }

    protected static class CursorMetaEntry
    {
        public final int numColumns;
        public final HashMap<String,Integer> columnNamesToIndexes = new HashMap<String, Integer>();

        public CursorMetaEntry(Cursor c)
        {
            numColumns = c.getColumnCount();
            for (int i = 0; i < numColumns; i++) {
                columnNamesToIndexes.put(c.getColumnName(i), i);
            }
        }
    }


    protected Object deserializeBlob(byte[] data)
    {
        ObjectInputStream os = null;
        try {
            os = new ObjectInputStream(new ByteArrayInputStream(data));
            final Object result = os.readObject();
            os.close();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] serializeToBlob(Object obj)
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream os = new ObjectOutputStream(baos);
            os.writeObject(obj);
            final byte[] result = baos.toByteArray();
            baos.close();
            return result;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
