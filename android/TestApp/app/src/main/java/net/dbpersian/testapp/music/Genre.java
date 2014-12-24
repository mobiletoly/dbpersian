package net.dbpersian.testapp.music;

import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbTable;

@DbTable(sqlName = "genre")
public class Genre
{
    public static final String ROCK = "rock";
    public static final String POP = "pop";

    // We don't necessary need a primary key. Here we create a unique indexed column that can be referred
    // via foreign key by other tables.
    @DbColumn(sqlName = "code", indexName = "idx_genre_code", unique = true)
    String code;

    @DbColumn(sqlName = "name", indexName = "idx_genre_name", unique = true)
    String name;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Genre genre = (Genre) o;
        if (code != null ? !code.equals(genre.code) : genre.code != null) { return false; }
        return true;
    }

    @Override
    public int hashCode()
    {
        return code != null ? code.hashCode() : 0;
    }
}
