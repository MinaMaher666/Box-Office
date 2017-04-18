package com.example.mina.boxoffice.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mina.boxoffice.Data.FavoriteContract.FavoriteEntry;

/**
 * Created by mina on 14/04/17.
 */

public class FavoriteDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "favorites.db";
    public static final int DB_VERSION = 1;

    public FavoriteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE_MOVIES =
                "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " ( " +
                FavoriteEntry.MOVIE_ID      + " INTEGER, " +
                FavoriteEntry.MOVIE_TITLE   + " TEXT, " +
                FavoriteEntry.RELEASE_DATE  + " TIMESTAMP, " +
                FavoriteEntry.PLOT          + " TEXT, " +
                FavoriteEntry.RATE          + " REAL, " +
                FavoriteEntry.POSTER        + " BLOB, " +
                "PRIMARY KEY(" + FavoriteEntry.MOVIE_ID  + ")" + " )";

        final String SQL_CREATE_TABLE_REVIEWS =
                "CREATE TABLE " + FavoriteContract.ReviewEntry.TABLE_NAME + " ( " +
                        FavoriteContract.ReviewEntry.MOVIE_ID  + " INTEGER, " +
                        FavoriteContract.ReviewEntry.AUTHOR    + " TEXT, " +
                        FavoriteContract.ReviewEntry.CONTENT   + " TEXT, " +
                        "FOREIGN KEY(" + FavoriteContract.ReviewEntry.MOVIE_ID + ") " +
                        "REFERENCES " + FavoriteEntry.TABLE_NAME + "(" + FavoriteEntry.MOVIE_ID +") " + " )";

        final String SQL_CREATE_TABLE_TRAILERS =
                "CREATE TABLE " + FavoriteContract.TrailerEntry.TABLE_NAME + " ( " +
                        FavoriteContract.TrailerEntry.MOVIE_ID      + " INTEGER, " +
                        FavoriteContract.TrailerEntry.TITLE         + " TEXT, " +
                        FavoriteContract.TrailerEntry.YOUTUBE_KEY   + " TEXT, " +
                        "FOREIGN KEY(" + FavoriteContract.TrailerEntry.MOVIE_ID + ") " +
                        "REFERENCES " + FavoriteEntry.TABLE_NAME + "(" + FavoriteEntry.MOVIE_ID +") " + " )";

        db.execSQL(SQL_CREATE_TABLE_MOVIES);
        db.execSQL(SQL_CREATE_TABLE_REVIEWS);
        db.execSQL(SQL_CREATE_TABLE_TRAILERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_UPGRADE_VERSION_QUERY = "DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME;
        db.execSQL(SQL_UPGRADE_VERSION_QUERY);

        onCreate(db);
    }
}
