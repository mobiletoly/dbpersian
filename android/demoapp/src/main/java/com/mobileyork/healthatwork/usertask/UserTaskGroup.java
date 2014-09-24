package com.mobileyork.healthatwork.usertask;

import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbTable;

@DbTable(sqlName = "user_task_group")
public final class UserTaskGroup
{
    @DbColumn(name = "id", primaryKey = true)
    Long id;

    @DbColumn(name = "key", indexName = "idx_usertaskgroup_key", unique = true)
    String key;

    @DbColumn(name = "label")
    String label;

    public Long getId() {
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
}
