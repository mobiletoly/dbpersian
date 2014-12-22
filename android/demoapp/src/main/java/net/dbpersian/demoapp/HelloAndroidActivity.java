package net.dbpersian.demoapp;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import com.mobileyork.healthatwork.usertask.UserTask;
import com.mobileyork.healthatwork.usertask.UserTaskDAO;
import com.mobileyork.healthatwork.usertask.UserTaskDbHelper;
import net.dbpersian.api.AbstractDbHelper;
import net.dbpersian.api.GenericDAO;
import net.dbpersian.processor.DAOClassBuilder;

import java.util.LinkedHashMap;
import java.util.List;

public class HelloAndroidActivity extends Activity
{
    private SQLiteDatabase database;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final UserTaskDbHelper dbHelper = ((MainApplication)getApplicationContext()).getUserTaskDbHelper();
        dbHelper.openReadableAsync(new AbstractDbHelper.AsyncDatabaseOperation() {
            @Override
            public void doInBackground(SQLiteDatabase db) {
            }
            @Override
            public void onComplete(SQLiteDatabase db) {
                HelloAndroidActivity.this.database = db;
                System.err.println("**** userTaskDAO onComplete");
                final UserTaskDAO userTaskDAO = new UserTaskDAO(db);
                final LinkedHashMap<String, UserTask> userTasks = userTaskDAO.queryAllAsKeyMap(null);
                System.out.println("::: " + userTasks);
            }
        });

        {
            /*
            final SQLiteDatabase db = dbHelper.getReadableDatabase();
            final UserTaskDAO userTaskDAO = new UserTaskDAO(db);
            //UserTask userTask = userTaskDAO.findByKey("key002");
            final UserTask userTask = userTaskDAO.queryForSingleResult(
                    "select key,label from user_task where key = ?",
                    new String[]{"key002"});
            System.out.println("::: " + userTask);
            */
        }
    }

    @Override
    protected void onDestroy()
    {
        if (this.database != null) {
            this.database.close();
            this.database = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bdar if it is present.
    	getMenuInflater().inflate(R.menu.main, menu);
	    return true;
    }

}

