package com.example.mina.boxoffice.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.mina.boxoffice.Data.FavoriteContract;
import com.example.mina.boxoffice.Model.Movie;
import com.example.mina.boxoffice.Model.MovieReview;
import com.example.mina.boxoffice.Model.MovieTrailer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mina on 16/04/17.
 */

public class ProviderUtils {
    private ProviderUtils() {
    }

    public static List<Movie> extractMoviesFromCursor(Cursor cursor) {
        ArrayList<Movie> movies = new ArrayList<>();

        int movieId;
        String movieTitle;
        String releaseDate;
        String plot;
        double rate;
        byte[] posterByteArr;
        Bitmap poster;

        while (cursor.moveToNext()) {
            movieId = cursor.getInt(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.MOVIE_ID));
            movieTitle = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.MOVIE_TITLE));
            releaseDate = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.RELEASE_DATE));
            plot = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.PLOT));
            rate = cursor.getDouble(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.RATE));
            posterByteArr = cursor.getBlob(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.POSTER));
            poster = convertByteArrToBitmap(posterByteArr);

            movies.add(new Movie(movieId, movieTitle, releaseDate, null, plot, rate, poster));
        }
        cursor.close();
        return movies;
    }

    public static List<Movie> getAllFavoriteMovies(Context context) {
        Uri queryUri = FavoriteContract.FavoriteEntry.MOVIES_URI;
        Cursor allFavMoviesCursor = context.getContentResolver().query(queryUri, null, null, null, null);
        return extractMoviesFromCursor(allFavMoviesCursor);
    }

    public static byte[] convertBitmapToByteArr(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertByteArrToBitmap(byte[] bytes) {
        if (bytes == null)
            return null;

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Uri insertFavoriteMovie(Context context, Movie movie, Bitmap poster) {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteContract.FavoriteEntry.MOVIE_ID, movie.getmMovieId());
        cv.put(FavoriteContract.FavoriteEntry.MOVIE_TITLE, movie.getmTitle());
        cv.put(FavoriteContract.FavoriteEntry.PLOT, movie.getmPlot());
        cv.put(FavoriteContract.FavoriteEntry.RATE, movie.getmRate());
        cv.put(FavoriteContract.FavoriteEntry.RELEASE_DATE, movie.getmReleaseDate());
        if (poster != null) {
            byte[] posterByteArr = convertBitmapToByteArr(poster);
            cv.put(FavoriteContract.FavoriteEntry.POSTER, posterByteArr);
        }

        Uri insertUri = FavoriteContract.FavoriteEntry.MOVIES_URI;

        return context.getContentResolver().insert(insertUri, cv);
    }

    public static int deleteFavoriteMovie(Context context, int movieId) {
        Uri deleteUri = FavoriteContract.FavoriteEntry.MOVIES_URI
                .buildUpon()
                .appendPath(String.valueOf(movieId))
                .build();
        return context.getContentResolver().delete(deleteUri, null, null);
    }

    public static int insertFavoriteReviews(Context context, List<MovieReview> reviews, int movieId) {
        int reviewsSize = reviews.size();
        ContentValues[] values = new ContentValues[reviewsSize];
        for (int i=0 ; i<reviewsSize ; i++) {
            String author = reviews.get(i).getmAuthor();
            String content = reviews.get(i).getmContent();

            values[i] = new ContentValues();
            values[i].put(FavoriteContract.ReviewEntry.MOVIE_ID, movieId);
            values[i].put(FavoriteContract.ReviewEntry.AUTHOR, author);
            values[i].put(FavoriteContract.ReviewEntry.CONTENT, content);
        }
        Uri insertUri = FavoriteContract.ReviewEntry.REVIEWS_URI;
        return context.getContentResolver().bulkInsert(insertUri, values);
    }

    public static ArrayList<MovieReview> getMovieReviews(Context context, int movieId) {
        Uri queryUri = FavoriteContract.ReviewEntry.REVIEWS_URI;
        String selection = FavoriteContract.ReviewEntry.MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(movieId)};

        Cursor queryCursor = context.getContentResolver().query(queryUri, null, selection, selectionArgs, null);
        return extractReviewsFromCursor(queryCursor);
    }
    public static ArrayList<MovieReview> extractReviewsFromCursor(Cursor cursor) {
        ArrayList<MovieReview> reviews = new ArrayList<>();
        while (cursor.moveToNext()) {
            String author = cursor.getString(cursor.getColumnIndex(FavoriteContract.ReviewEntry.AUTHOR));
            String content = cursor.getString(cursor.getColumnIndex(FavoriteContract.ReviewEntry.CONTENT));

            reviews.add(new MovieReview(author, content));
        }
        cursor.close();
        return reviews;
    }

    public static int deleteMovieReviews(Context context, int movieId) {
        Uri deleteUri       = FavoriteContract.ReviewEntry.REVIEWS_URI;
        String where        = FavoriteContract.ReviewEntry.MOVIE_ID + "=?";
        String[] whereArgs  = new String[]{String.valueOf(movieId)};
        return context.getContentResolver().delete(deleteUri, where, whereArgs);
    }

    public static int insertFavoriteTrailers(Context context, List<MovieTrailer> trailers, int movieId) {
        int trailersSize = trailers.size();
        ContentValues[] values = new ContentValues[trailersSize];
        for(int i=0 ; i<trailersSize ; i++) {
            String title = trailers.get(i).getmName();
            String key = trailers.get(i).getmKey();

            values[i] = new ContentValues();
            values[i].put(FavoriteContract.TrailerEntry.MOVIE_ID, movieId);
            values[i].put(FavoriteContract.TrailerEntry.TITLE, title);
            values[i].put(FavoriteContract.TrailerEntry.YOUTUBE_KEY, key);
        }
        Uri insertUri = FavoriteContract.TrailerEntry.TRAILERS_URL;
        return context.getContentResolver().bulkInsert(insertUri, values);
    }

    public static ArrayList<MovieTrailer> getMovieTrailers(Context context, int movieId) {
        Uri queryUri = FavoriteContract.TrailerEntry.TRAILERS_URL;
        String selection = FavoriteContract.TrailerEntry.MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(movieId)};

        Cursor queryCursor = context.getContentResolver().query(queryUri, null, selection, selectionArgs, null);
        return extractTrailersFromCursor(queryCursor);
    }

    public static ArrayList<MovieTrailer> extractTrailersFromCursor(Cursor cursor) {
        ArrayList<MovieTrailer> trailers = new ArrayList<>();
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(FavoriteContract.TrailerEntry.TITLE));
            String key = cursor.getString(cursor.getColumnIndex(FavoriteContract.TrailerEntry.YOUTUBE_KEY));

            trailers.add(new MovieTrailer(title, key, null));
        }
        cursor.close();
        return trailers;
    }

    public static int deleteMovieTrailers(Context context, int movieId) {
        Uri deleteUri       = FavoriteContract.TrailerEntry.TRAILERS_URL;
        String where        = FavoriteContract.TrailerEntry.MOVIE_ID + "=?";
        String[] whereArgs  = new String[]{String.valueOf(movieId)};
        return context.getContentResolver().delete(deleteUri, where, whereArgs);
    }
}
