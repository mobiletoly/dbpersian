// -------------------------------------------------------------------
//
// THIS FILE IS AUTO-GENERATED
// DO NOT EDIT
//
// -------------------------------------------------------------------

package <package>;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Base database open helper class.
 */
public abstract class <dbHelperClass> extends SQLiteOpenHelper
{
    private static final String DB_NAME = "<dbDescr.filename>";
    private static final int DB_VERSION = <dbDescr.version>;

    public <dbHelperClass>(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called when database is created. You can override this method to insert you own default data
     * into tables when database is created. If override this method, make sure to always call
     * super.onCreate(db) prior to your other database calls, to ensure that all tables are created.
     * Transactions are used to make sure the database is always in a sensible state, so you don't need
     * to start your own transaction in this method.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        <dbTableDescrs:{dbTableDescr | <dbTableDescr.daoJavaClassName>.createTable(db);
        }>
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}
