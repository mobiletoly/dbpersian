package com.mobileyork.healthatwork.usertask;

import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbForeignKeyReader;
import net.dbpersian.annotations.db.DbTable;

@DbTable(sqlName = "user_task", onEntityLoad = "onLoad")
public final class UserTask
{
    @DbColumn(sqlName = "id", primaryKey = true)
    Long id;
    
    @DbColumn(sqlName = "key", indexName = "idx_usertask_key", unique = true, notNull = true)
    String key;

    @DbColumn(sqlName = "usertaskgroup_key", fkTable = "user_task_group", fkColumn = "key")
    String userTaskGroupKey;
    @DbForeignKeyReader(refField = "userTaskGroupKey")
    UserTaskGroup userTaskGroup;

    @DbColumn(sqlName = "usertaskcategory_key", fkTable = "user_task_category", fkColumn = "key")
    String userTaskCategoryKey;
    @DbForeignKeyReader(refField = "userTaskCategoryKey")
    UserTaskGroup userTaskCategory;
    
    @DbColumn(sqlName = "label", notNull = true)
    String label;
    
    @DbColumn(sqlName = "repeat")
    Integer repeat;

    @DbColumn(sqlName = "frame_indices")
    int[] frameIndices;

    @DbColumn(sqlName = "full_name", serializable = true)
    UserFullName userFullName;

    @DbColumn(sqlName = "enabled")
    boolean enabled;


    private String tag;

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }

    public String getUserTaskGroupKey() {
        return userTaskGroupKey;
    }

    public void setUserTaskGroupKey(String userTaskGroupKey) {
        this.userTaskGroupKey = userTaskGroupKey;
    }

    public UserTaskGroup getUserTaskGroup() {
        return userTaskGroup;
    }

    public int[] getFrameIndices() {
        return frameIndices;
    }

    public void setFrameIndices(int[] frameIndices) {
        this.frameIndices = frameIndices;
    }

    public UserFullName getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(UserFullName userFullName) {
        this.userFullName = userFullName;
    }

    void onLoad()
    {
        System.out.println("::::::::::::: ONLOAD :::::::::::::::::");
    }
}
