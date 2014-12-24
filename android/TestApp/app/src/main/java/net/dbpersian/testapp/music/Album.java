package net.dbpersian.testapp.music;


import net.dbpersian.annotations.db.DbColumn;
import net.dbpersian.annotations.db.DbForeignKeyReader;
import net.dbpersian.annotations.db.DbTable;

@DbTable(sqlName = "album")
public class Album
{
    // This field is a primary key column. It is unique and automatically assigned by SQLite
    @DbColumn(sqlName = "id", primaryKey = true)
    Long id;

    // Text field to hold a name of an album.
    @DbColumn(sqlName = "name", indexName = "idx_album_name", notNull = true)
    String name;

    // This field is a foreign key to "id" column in Artist table.
    @DbColumn(sqlName = "artist_id", fkTable = "artist", fkColumn = "id", notNull = true)
    Long artistId;
    // Sometimes we want to simplify reading of an object that foreign key refers to.
    // The @DbForeignKeyReader is a convenient annotation that allows us to instruct DBPersian to query
    // table "Artist" based on a value of foreign key stored in "artistId" field above. So what it actually
    // does is performs something like "SELECT * FROM artist_id WHERE id = :artistId" and deserialize
    // a result into the "artist" field below.
    // So this annotation is simply to make your life easier while reading data from a database. If you change
    // this field and update an entity, nothing is going to happen.
    //
    // Querying for entities referred by a foreign keys is enabled automatically by default. If you want to disable
    // this behaviour, you may choose to call
    //     albumDAO.setAutoFetchForeignKeyReaders(false);
    // In this case querying of fields annotated with @DbForeignKeyReader will not be performed automatically.
    // It might save you an extra database query and improve a performance (let's say at startup time), but later
    // you can query for a foreign key entities manually. You can do it by calling:
    //    final Album album = ...;
    //    albumDao.fetchUserTaskGroup(album);
    // or (to fetch all field annotated by @DbForeignKeyReader):
    //    albumDao.fetchAllForeignKeyReaders(album);
    // Also it is important to mention that @DbForeignKeyReader is a "one way street", it works only for reading
    // entities. E.g. if you have changed a data inside an Artist object of Album and then you try to save that
    // Album entity to a database, then Artist entity will not be updated inside a database. You still have to save
    // an Artist entity manually. E.g.
    //    album.setName("Different Album");
    //    album.artist.setName("Different Artist");
    //    albumDAO.update(album);
    //    artistDAO.update(artist);
    @DbForeignKeyReader(refField = "artistId")
    Artist artist;

    @DbColumn(sqlName = "genre_code", fkTable = "genre", fkColumn = "code", indexName = "fk_album_genrecode")
    String genreCode;
    // We don't necessary need @DbForeignKeyReader for foreign keys. In this case "genre" field will
    // never be initialized from DAO, but we might assign it manually later. This technique can be used to
    // avoid extra SQL query to read a foreign key entity from a database.
    Genre genre;

    @DbColumn(sqlName = "year", indexName = "idx_album_year")
    Integer yearReleased;

    @DbColumn(sqlName = "description")
    String description;

    // Getters/setters

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getArtistId() { return artistId; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    public Artist getArtist() { return artist; }

    public String getGenreCode() { return genreCode; }
    public void setGenreCode(String genreCode) { this.genreCode = genreCode; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    public Integer getYearReleased() { return yearReleased; }
    public void setYearReleased(Integer yearReleased) { this.yearReleased = yearReleased; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Album album = (Album) o;
        if (id != null ? !id.equals(album.id) : album.id != null) { return false; }
        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
