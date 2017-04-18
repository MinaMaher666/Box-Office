package com.example.mina.boxoffice.Data;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mina on 14/04/17.
 */

public class FavoriteContract {
    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.example.mina.boxoffice";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    private FavoriteContract() {
    }

    public static class FavoriteEntry {
        public static final String TABLE_NAME = "movies";

        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_TITLE = "title";
        public static final String RELEASE_DATE = "release_date";
        public static final String PLOT = "plot";
        public static final String RATE = "rate";
        public static final String POSTER = "poster";

        public static final String PATH_MOVIES = TABLE_NAME;
        public static final Uri MOVIES_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIES)
                .build();
    }

    public static class ReviewEntry {
        public static final String TABLE_NAME = "reviews";

        public static final String MOVIE_ID = FavoriteEntry.MOVIE_ID;
        public static final String AUTHOR = "author";
        public static final String CONTENT = "content";

        public static final String PATH_REVIEWS = TABLE_NAME;
        public static final Uri REVIEWS_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_REVIEWS)
                .build();
    }

    public static class TrailerEntry {
        public static final String TABLE_NAME = "trailers";

        public static final String MOVIE_ID = FavoriteEntry.MOVIE_ID;
        public static final String TITLE = "title";
        public static final String YOUTUBE_KEY = "youtube_key";

        public static final String PATH_TRAILERS = TABLE_NAME;
        public static final Uri TRAILERS_URL = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_TRAILERS)
                .build();
    }
}
