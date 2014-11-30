package com.mobileyork.healthatwork.usertask;

import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbForeignKeyReader;
import net.dbpersian.annotations.db.DbTable;

@DbTable(sqlName = "user_task", onEntityLoad = "onLoad")
public final class UserTask
{
    @DbColumn(name = "id", primaryKey = true)
    Long id;
    
    @DbColumn(name = "key", indexName = "idx_usertask_key", unique = true, notNull = true)
    String key;

    @DbColumn(name = "usertaskgroup_key", fkTable = "user_task_group", fkColumn = "key")
    String userTaskGroupKey;
    @DbForeignKeyReader(refField = "userTaskGroupKey")
    UserTaskGroup userTaskGroup;

    @DbColumn(name = "usertaskcategory_key", fkTable = "user_task_category", fkColumn = "key")
    String userTaskCategoryKey;
    @DbForeignKeyReader(refField = "userTaskCategoryKey")
    UserTaskGroup userTaskCategory;
    
    @DbColumn(name = "label", notNull = true)
    String label;
    
    @DbColumn(name = "repeat")
    Integer repeat;

    @DbColumn(name = "frame_indices")
    int[] frameIndices;

    @DbColumn(name = "full_name", serializable = true)
    UserFullName userFullName;

    @DbColumn(name = "enabled")
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
