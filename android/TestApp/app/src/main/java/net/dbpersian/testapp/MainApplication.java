package net.dbpersian.testapp;


import android.app.Application;

import net.dbpersian.testapp.music.MusicDbHelper;


public class MainApplication extends Application
{
    private MusicDbHelper musicDbHelper;

    @Override
    public void onCreate()
    {
        super.onCreate();
        musicDbHelper = new MusicDbHelper(this);
    }

    public MusicDbHelper getMusicDbHelper()
    {
        return musicDbHelper;
    }
}
