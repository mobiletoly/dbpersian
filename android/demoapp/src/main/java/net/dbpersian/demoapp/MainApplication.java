package net.dbpersian.demoapp;

import android.app.Application;
import com.mobileyork.healthatwork.usertask.UserTask;
import com.mobileyork.healthatwork.usertask.UserTaskDAO;
import com.mobileyork.healthatwork.usertask.UserTaskDbHelper;


public class MainApplication extends Application
{
    private UserTaskDbHelper userTaskDbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        userTaskDbHelper = new UserTaskDbHelper(this);
    }

    public UserTaskDbHelper getUserTaskDbHelper() {
        return userTaskDbHelper;
    }
}
