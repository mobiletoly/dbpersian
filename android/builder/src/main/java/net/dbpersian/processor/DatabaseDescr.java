package net.dbpersian.processor;

import net.dbpersian.annotations.db.Database;

import javax.lang.model.element.TypeElement;

class DatabaseDescr
{
    private final TypeElement dbElement;
    private final Database database;

    public DatabaseDescr(TypeElement dbElement, Database database)
    {
        this.dbElement = dbElement;
        this.database = database;
    }

    public String getFilename()
    {
        return this.database.filename();
    }

    public int getVersion()
    {
        return this.database.version();
    }
}
