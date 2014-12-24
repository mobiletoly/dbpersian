package net.dbpersian.testapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.dbpersian.api.AbstractDbHelper;
import net.dbpersian.testapp.music.Album;
import net.dbpersian.testapp.music.AlbumDAO;
import net.dbpersian.testapp.music.Genre;
import net.dbpersian.testapp.music.MusicDbHelper;

import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private SQLiteDatabase musicDatabase;

    private TextView albumTextView;
    private TextView artistTextView;
    private TextView genreYearTextView;
    private TextView descriptionTextView;

    private Album selectedAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albumTextView = (TextView)findViewById(R.id.albumName);
        artistTextView = (TextView)findViewById(R.id.artist);

        // Generally it is a good practice to open database asynchronously, because it might take
        // some time to create a database - create all tables, indices, constraints etc. Also onCreate method
        // of MusicDbHelper will be called at some point and in this method we have a bunch of "insert" calls
        // to add Genre, Artist, Album entities.
        // Even if database was previously created, you might want to add some long running queries in onOpen method.
        // In our case we are querying all genres onOpen() method of MusicDbHelper class. Who know, it might take
        // a while.
        openMusicDatabaseAsync();
    }

    private void openMusicDatabaseAsync()
    {
        Log.i(TAG, "Request to open database asynchronously");
        final MusicDbHelper dbHelper = ((MainApplication)getApplicationContext()).getMusicDbHelper();
        dbHelper.openWritableAsync(new AbstractDbHelper.AsyncDatabaseOperation()
        {
            private Album someAlbum;
            /**
             * This method executed on a background thread, after database was open. You may do any
             * database related activities here, such as querying data etc.
             */
            @Override
            public void doInBackground(SQLiteDatabase database)
            {
                Log.i(TAG, "Let's finish some database work on a background thread");

                Log.i(TAG, "---- Genres ----");
                final Map<String, Genre> allGenres = dbHelper.getAllGenres();
                for (final Genre genre : allGenres.values()) {
                    Log.i(TAG, "*   " + genre.getCode() + " / " + genre.getName());
                }

                Log.i(TAG, "---- Albums ----");
                final AlbumDAO albumDAO = new AlbumDAO(database);
                final List<Album> albums = albumDAO.queryAll(/*ORDER BY*/"name");
                printAlbums(albums);

                Log.i(TAG, "---- Albums released in 1991 ---");
                final List<Album> albums1991 = albumDAO.queryByYearReleased(1991);
                printAlbums(albums1991);

                someAlbum = albums1991.get(0);
            }

            /**
             * This method is executed on UI thread, so it is safe to updated your user interface
             * with a data that were read from a database.
             */
            @Override
            public void onComplete(SQLiteDatabase database)
            {
                musicDatabase = database;
                selectedAlbum = someAlbum;
                albumTextView.setText(selectedAlbum.getName());
                artistTextView.setText(selectedAlbum.getArtist().getName());
            }
        });
    }

    public void showDetailsOnClick(View button)
    {
        Log.i(TAG, "User has requested to show album details");
        final Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.PARAM_ALBUM_ID, selectedAlbum.getId());
        startActivity(intent);
    }

    private void printAlbums(List<Album> albums)
    {
        final MusicDbHelper dbHelper = ((MainApplication)getApplicationContext()).getMusicDbHelper();

        final Map<String, Genre> allGenres = dbHelper.getAllGenres();
        for (final Album album : albums) {
            // Since "genre" field of Album doesn't not have a @DbForeignKeyReader annotation, then
            // it is null at this point. But since we already have list of all genres available as a map,
            // we can quickly resolve this "issue". This trick can be used to avoid unnecessary database
            // hits to read an entities referred by foreign keys.
            final Genre genre = allGenres.get(album.getGenreCode());
            album.setGenre(genre);

            Log.i(TAG, "*   " + album.getYearReleased() + " " + album.getGenre().getName() + " album "
                    + album.getName() + " (" + album.getArtist().getName() + "): " + album.getDescription());
        }
    }
}
