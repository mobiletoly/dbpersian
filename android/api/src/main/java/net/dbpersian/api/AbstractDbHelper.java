package net.dbpersian.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;

/**
 * Abstract database helper.
 */
public abstract class AbstractDbHelper extends SQLiteOpenHelper
{

    public AbstractDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AbstractDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                            DatabaseErrorHandler errorHandler)
    {
        super(context, name, factory, version, errorHandler);
    }

    protected void openAsync(final boolean isWritableDatabase, final AsyncDatabaseOperation dbOp)
    {
        new AsyncTask<Void,Void,Void>() {
            private SQLiteDatabase db;
            @Override
            protected Void doInBackground(Void... params) {
                this.db = isWritableDatabase ? getWritableDatabase() : getReadableDatabase();
                dbOp.doInBackground(db);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                dbOp.onComplete(this.db);
            }
        }.execute();
    }

    public void openWritableAsync(final AsyncDatabaseOperation dbOp)
    {
        openAsync(true, dbOp);
    }

    public void openReadableAsync(final AsyncDatabaseOperation dbOp)
    {
        openAsync(false, dbOp);
    }

    public SQLiteDatabase openWritable()
    {
        return getWritableDatabase();
    }

    public SQLiteDatabase openReadable()
    {
        return getReadableDatabase();
    }

    /**
     * Perform asynchronous operation inside a transaction.
     */
    public void asyncTransaction(final SQLiteDatabase db, final AsyncDatabaseOperation dbOp)
    {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                db.beginTransaction();
                try {
                    dbOp.doInBackground(db);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                dbOp.onComplete(db);
            }
        }.execute();
    }

    public interface AsyncDatabaseOperation
    {
        /**
         * Executed on background thread. At this point database operation was already completed.
         * Here you can perform all asynchronous operations, that will be executed prior to calling onComplete
         * on main UI thread. */
        void doInBackground(SQLiteDatabase db);
        /** Executed on main UI thread when database . */
        void onComplete(SQLiteDatabase db);
    }
}
