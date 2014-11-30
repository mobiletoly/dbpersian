package com.mobileyork.healthatwork.usertask;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import net.dbpersian.annotations.db.Database;

@Database(filename = "UserTask.db", version = 2)
public class UserTaskDbHelper extends AbstractUserTaskDbHelper
{
    public UserTaskDbHelper(Context context)
    {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        super.onCreate(db);

        final UserTaskCategoryDAO userTaskCategoryDAO = UserTaskCategoryDAO.createForDatabase(db);
        final UserTaskCategory utcat01 = new UserTaskCategory();
        utcat01.setKey("home");
        utcat01.setLabel("Home");
        final UserTaskCategory utcat02 = new UserTaskCategory();
        utcat02.setKey("work");
        utcat02.setLabel("Work");
        userTaskCategoryDAO.insert(utcat01);
        userTaskCategoryDAO.insert(utcat02);

        final UserTaskGroupDAO userTaskGroupDAO = UserTaskGroupDAO.createForDatabase(db);
        final UserTaskGroup userTaskGroup = new UserTaskGroup();
        userTaskGroup.setKey("task001");
        userTaskGroup.setLabel("First task group");
        userTaskGroupDAO.insert(userTaskGroup);

        final UserTask userTask01 = new UserTask();
        userTask01.setKey("key001");
        userTask01.setLabel("label001");
        userTask01.setRepeat(10);
        userTask01.setUserTaskGroupKey(userTaskGroup.getKey());
        final UserTask userTask02 = new UserTask();
        userTask02.setKey("key002");
        userTask02.setLabel("label002");
        userTask02.setRepeat(5);
        userTask02.setUserTaskGroupKey(userTaskGroup.getKey());

        final UserTaskDAO userTaskDAO = UserTaskDAO.createForDatabase(db);
        userTaskDAO.insert(userTask01);
        userTaskDAO.insert(userTask02);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}
