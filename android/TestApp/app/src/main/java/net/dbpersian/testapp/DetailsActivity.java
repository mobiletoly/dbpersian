package net.dbpersian.testapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.dbpersian.api.AbstractDbHelper;
import net.dbpersian.testapp.music.Album;
import net.dbpersian.testapp.music.AlbumDAO;
import net.dbpersian.testapp.music.Genre;
import net.dbpersian.testapp.music.MusicDbHelper;

import java.util.List;
import java.util.Map;


public class DetailsActivity extends ActionBarActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PARAM_ALBUM_ID = "albumId";

    private SQLiteDatabase musicDatabase;
    private Long albumId;
    private Album selectedAlbum;

    private TextView albumTextView;
    private TextView artistTextView;
    private TextView genreYearTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        albumId = intent.getLongExtra(PARAM_ALBUM_ID, -1);
        if (albumId == -1) {
            throw new IllegalStateException("This activity should be supplied with [" + PARAM_ALBUM_ID + "] extra");
        }

        albumTextView = (TextView)findViewById(R.id.albumName);
        artistTextView = (TextView)findViewById(R.id.artist);
        genreYearTextView = (TextView)findViewById(R.id.genreAndYear);
        descriptionTextView = (TextView)findViewById(R.id.description);
        openMusicDatabaseAsync();
    }

    private void openMusicDatabaseAsync()
    {
        Log.i(TAG, "Request to open database asynchronously");
        final MusicDbHelper dbHelper = ((MainApplication)getApplicationContext()).getMusicDbHelper();

        dbHelper.openReadableAsync(new AbstractDbHelper.AsyncDatabaseOperation()
        {
            private Album someAlbum;

            @Override
            public void doInBackground(SQLiteDatabase database)
            {
                Log.i(TAG, "Query album by albumId=" + albumId);

                final AlbumDAO albumDAO = new AlbumDAO(database);
                someAlbum = albumDAO.queryById(albumId);
                someAlbum.setGenre(dbHelper.getAllGenres().get(someAlbum.getGenreCode()));
            }

            @Override
            public void onComplete(SQLiteDatabase database)
            {
                musicDatabase = database;
                selectedAlbum = someAlbum;
                showDetails();
            }
        });
    }

    private void showDetails()
    {
        albumTextView.setText(selectedAlbum.getName());
        artistTextView.setText(selectedAlbum.getArtist().getName());
        genreYearTextView.setText("Released: " + selectedAlbum.getYearReleased() +
                ", Genre: " + selectedAlbum.getGenre().getName());
        descriptionTextView.setText(selectedAlbum.getDescription());
    }
}
