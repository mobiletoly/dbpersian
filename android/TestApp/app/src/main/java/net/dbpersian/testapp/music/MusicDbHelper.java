package net.dbpersian.testapp.music;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.dbpersian.annotations.db.Database;

import java.util.LinkedList;
import java.util.Map;

@Database(filename = "music.db")
public class MusicDbHelper extends AbstractMusicDbHelper
{
    private static final String TAG = MusicDbHelper.class.getSimpleName();

    /**
     * Collection of all genres. The key of this map represents [code] field from a Genre's entity.
     */
    private Map<String, Genre> allGenres;

    public MusicDbHelper(Context context)
    {
        super(context);
    }

    /**
     * This method is getting called only when database is created at first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        super.onCreate(db);
        // You must always call super.onCreate() method first
        // At this point database tables will be created based on all classes with @DbTable annotations

        Log.i(TAG, "Database " + getDatabaseName() + " was created");

        // onCreate() is called in a transaction context, therefore we don't have to explicitly start
        // a transaction when working with a database inside onCreate().
        final DatabaseDefaultData dbDefData = new DatabaseDefaultData(db);
        dbDefData.create();
    }

    /**
     * This method is always getting called when database is open. Even if database was created, this method
     * will be called after onCreate().
     */
    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        // You must always call super.onOpen() method first.

        Log.i(TAG, "Database " + getDatabaseName() + " was open");

        if (allGenres == null) {
            // Let's say we know that we need a quick access to all genres and therefore we want to pre-load
            // all genres to quickly access them throughout the application. onOpen() is a good place for that
            final GenreDAO genreDAO = new GenreDAO(db);
            // If entity has an index fields then convenient methods queryAllAs[XXX]Map() will be generated for DAO
            // class that deal with this kind of entities. In this case
            Log.i(TAG, "Query all genres");
            allGenres = genreDAO.queryAllAsCodeMap(/*ORDER BY*/ "name");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public Map<String, Genre> getAllGenres()
    {
        return allGenres;
    }

    /**
     * Populate database with default data
     */
    private class DatabaseDefaultData
    {
        private final GenreDAO genreDAO;
        private final ArtistDAO artistDAO;
        private final AlbumDAO albumDAO;

        public DatabaseDefaultData(SQLiteDatabase db)
        {
            genreDAO = new GenreDAO(db);
            artistDAO = new ArtistDAO(db);
            albumDAO = new AlbumDAO(db);
        }

        public void create()
        {
            Log.i(TAG, "Adding genres/artists/albums into database");
            addGenres();
            addMetallica();
            addTool();
            addMichaelJackson();
        }

        private void addGenres()
        {
            final Genre genreRock = new Genre();
            genreRock.setCode("rock");
            genreRock.setName("Rock");
            genreDAO.insert(genreRock);

            final Genre genrePop = new Genre();
            genrePop.setCode("pop");
            genrePop.setName("Pop");
            genreDAO.insert(genrePop);
        }

        private void addMetallica()
        {
            final Artist artistMetallica = new Artist();
            artistMetallica.setName("Metallica");
            artistDAO.insert(artistMetallica);      // At this point artistMetallica.id will be assigned
                                                    // to a unique id (according to SQLite primary key rules).
                                                    // So it is safe to use artistMetallica.getId() now.

            final Long artistId = artistMetallica.getId();

            final LinkedList<Album> albums = new LinkedList<Album>();

            final Album albumDeathMagnetic = new Album();
            albumDeathMagnetic.setArtistId(artistId);   // Set a foreign key field of an album to point
                                                        // to Metallica artist
            albumDeathMagnetic.setName("Death Magnetic");
            albumDeathMagnetic.setGenreCode(Genre.ROCK);
            albumDeathMagnetic.setYearReleased(2008);
            albumDeathMagnetic.setDescription("Death Magnetic is the ninth studio album");
            albums.add(albumDeathMagnetic);

            final Album albumRideTheLightning = new Album();
            albumRideTheLightning.setArtistId(artistId);
            albumRideTheLightning.setName("The Black Album");
            albumRideTheLightning.setGenreCode(Genre.ROCK);
            albumRideTheLightning.setYearReleased(1991);
            albumRideTheLightning.setDescription("Metallica (The Black Album) is the fifth studio album");
            albums.add(albumRideTheLightning);

            albumDAO.insert(albums);
        }

        private void addTool()
        {
            final Artist artistTool = new Artist();
            artistTool.setName("Tool");
            artistDAO.insert(artistTool);

            final Long artistId = artistTool.getId();
            final Album album10000days = new Album();
            album10000days.setArtistId(artistId);
            album10000days.setName("10,000 Days");
            album10000days.setGenreCode(Genre.ROCK);
            album10000days.setYearReleased(2006);
            album10000days.setDescription("10,000 Days is the fourth studio album");
            albumDAO.insert(album10000days);
        }

        private void addMichaelJackson()
        {
            final Artist artistMJ = new Artist();
            artistMJ.setName("Michael Jackson");
            artistDAO.insert(artistMJ);

            final Long artistId = artistMJ.getId();
            final Album albumThriller = new Album();
            albumThriller.setArtistId(artistId);
            albumThriller.setName("Dangerous");
            albumThriller.setGenreCode(Genre.POP);
            albumThriller.setYearReleased(1991);
            albumThriller.setDescription("Dangerous is the eighth studio album");
            albumDAO.insert(albumThriller);
        }
    }
}
