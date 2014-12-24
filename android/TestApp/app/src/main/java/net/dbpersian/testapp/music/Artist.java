package net.dbpersian.testapp.music;

import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbTable;


@DbTable(sqlName = "artist")
public class Artist
{
    // This field is a primary key column. It is unique and automatically assigned by SQLite
    @DbColumn(sqlName = "id", primaryKey = true)
    Long id;

    // Text field to hold a name of an artist. Each name should be unique.
    // Also we want for this column to have an index for fast access. In order to create an index
    // you want to specify a name of this index, because we are trying to stay as close to SQLite as possible.
    // Here we create an index following a convention such as idx_[TableName]_[ColumnName], but you can
    // name it any way you want. And yes, column will not accept a NULL values.
    @DbColumn(sqlName = "name", indexName = "idx_artist_name", unique = true, notNull = true)
    String name;

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Artist artist = (Artist) o;
        if (id != null ? !id.equals(artist.id) : artist.id != null) { return false; }
        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
