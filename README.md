dbpersian
=========

DBPersian - Database Persistence Annotations for Android.

Do you want to deal with something like this

    @DbTable(sqlName = "artist")
    public class Artist
    {
        // This field is a primary key column. It is unique and automatically assigned by SQLite
        @DbColumn(sqlName = "id", primaryKey = true)
        Long id;
    
        // Text field to hold a name of an artist.
        @DbColumn(sqlName = "name", indexName = "idx_artist_name", unique = true, notNull = true)
        String name;
    
        public Long getId() { return id; }
    
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

instead of writing a boring SQLite code? Well, it is easy to do, just read our tutorial:

http://mobiletoly.github.io/dbpersian/

