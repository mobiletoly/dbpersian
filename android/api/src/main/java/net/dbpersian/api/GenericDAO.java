package net.dbpersian.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
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

    private final HashMap<Cursor, CursorMetaEntry> mCursorMetaEntries = new HashMap<Cursor, CursorMetaEntry>();
    protected final String mTableName;
    protected SQLiteDatabase mDatabase;
    private boolean mOwnerOfDatabase;

    public GenericDAO(SQLiteOpenHelper sqliteOpenHelper, String tableName)
    {
        this(sqliteOpenHelper, tableName, false, null);
    }

    /**
     * Create DAO.
     * @param sqliteOpenHelper
     * @param tableName
     * @param dbOp
     */
    public GenericDAO(final SQLiteOpenHelper sqliteOpenHelper, String tableName,
                      boolean isAsync,
                      final AsyncDatabaseOperation<T> dbOp)
    {
        if (sqliteOpenHelper == null) {
            throw new NullPointerException("<sqliteOpenHelper> parameter: must not be null");
        }
        if (tableName == null) {
            throw new NullPointerException("<tableName> parameter: must not be null");
        }
        mOwnerOfDatabase = true;
        mTableName = tableName;
        if (!isAsync) {
            if (dbOp != null) {
                throw new IllegalArgumentException("<dbOp> parameter must be null when isAsync parameter is false");
            }
            mDatabase = sqliteOpenHelper.getWritableDatabase();
            return;
        }
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mDatabase = sqliteOpenHelper.getWritableDatabase();
                if (dbOp != null) {
                    dbOp.doInBackground(GenericDAO.this);
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                if (dbOp != null) {
                    dbOp.onComplete(GenericDAO.this);
                }
            }
        }.execute();
    }

    public GenericDAO(final SQLiteDatabase database, String tableName)
    {
        if (database == null) {
            throw new NullPointerException("<database> parameter: must not be null");
        }
        if (!database.isOpen()) {
            throw new IllegalArgumentException("<database> parameter: database is not open");
        }
        mDatabase = database;
        mTableName = tableName;
    }

    public abstract ContentValues getEntityContentValues(T entity);

    protected abstract T readEntityFromCursor(Cursor c, CursorMetaEntry meta);


    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    /** Close DAO object. */
    public void close()
    {
        if (mOwnerOfDatabase) {
            mDatabase.close();
        }
        mDatabase = null;
    }

    /** Drop table. */
    public void dropTable()
    {
        final String sql = "DROP TABLE " + mTableName;
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, sql);
        }
        mDatabase.execSQL(sql);
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
            c = mDatabase.rawQuery(sql, selectionArgs);
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
            c = mDatabase.rawQuery(sql, selectionArgs);
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
        String sql = "SELECT * FROM " + mTableName;
        if (orderBy != null) {
            sql += " ORDER BY " + orderBy;
        }
        return query(sql, null);
    }

    public int delete(String whereClause, String[] whereArgs)
    {
        return mDatabase.delete(mTableName, whereClause, whereArgs);
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
        mDatabase.beginTransaction();
        try {
            for (final T entity : entities) {
                insert(entity);
            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    public void update(Collection<T> entities)
    {
        mDatabase.beginTransaction();
        try {
            for (final T entity : entities) {
                update(entity);
            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    public void startAsync(final AsyncDatabaseOperation<T> op)
    {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                op.doInBackground(GenericDAO.this);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                op.onComplete(GenericDAO.this);
            }
        }.execute();
    }

    /**
     * Perform asynchronous transaction.
     * @param op
     */
    public void asyncTransaction(final AsyncDatabaseOperation<T> op)
    {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final SQLiteDatabase db = GenericDAO.this.mDatabase;
                db.beginTransaction();
                try {
                    op.doInBackground(GenericDAO.this);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                op.onComplete(GenericDAO.this);
            }
        }.execute();
    }


    public void transaction(DatabaseOperation<T> dbOp)
    {
        final SQLiteDatabase db = GenericDAO.this.mDatabase;
        db.beginTransaction();
        try {
            dbOp.execute(GenericDAO.this);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private synchronized CursorMetaEntry getCursorMetaEntry(Cursor cursor)
    {
        CursorMetaEntry meta = mCursorMetaEntries.get(cursor);
        if (meta != null) {
            return meta;
        }
        // Prior to adding new cursor to a map, we want to remove all cursor that are closed.
        for (Iterator<Map.Entry<Cursor, CursorMetaEntry>> it = mCursorMetaEntries.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry<Cursor, CursorMetaEntry> entry = it.next();
            final Cursor c = entry.getKey();
            if (c.isClosed()) {
                it.remove();
            }
        }
        meta = new CursorMetaEntry(cursor);
        mCursorMetaEntries.put(cursor, meta);
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


    public interface AsyncDatabaseOperation<T>
    {
        /** Executed on background thread. Here you can perform all asynchronous operations,
         * that will be executed prior to calling onComplete on main UI thread. */
        void doInBackground(GenericDAO<T> dao);
         /** Executed on main UI thread. */
        void onComplete(GenericDAO<T> dao);
    }

    public interface DatabaseOperation<T>
    {
        /** Executed on caller's thread. */
        void execute(GenericDAO<T> dao);
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
