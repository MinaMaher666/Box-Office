package com.example.mina.boxoffice.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Created by mina on 14/04/17.
 */

public class FavoriteContentProvider extends ContentProvider {
    private FavoriteDBHelper mMovieDbHelper;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    public static final int MOVIES_CODE         = 100;
    public static final int MOVIES_WITH_ID_CODE = 101;
    public static final int REVIEWS_CODE        = 200;
    public static final int TRAILERS_CODE       = 300;

    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.FavoriteEntry.PATH_MOVIES, MOVIES_CODE);
        matcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.FavoriteEntry.PATH_MOVIES + "/#", MOVIES_WITH_ID_CODE);
        matcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.ReviewEntry.PATH_REVIEWS, REVIEWS_CODE);
        matcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.TrailerEntry.PATH_TRAILERS, TRAILERS_CODE);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mMovieDbHelper = new FavoriteDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = mMovieDbHelper.getReadableDatabase();
        Cursor returnedCursor;

        int code = sUriMatcher.match(uri);
        switch (code) {
            case MOVIES_CODE:
                returnedCursor = sqLiteDatabase.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIES_WITH_ID_CODE:
                String id = uri.getPathSegments().get(1);
                String mSelection = FavoriteContract.FavoriteEntry.MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};
                returnedCursor = sqLiteDatabase.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            case REVIEWS_CODE:
                returnedCursor = sqLiteDatabase.query(FavoriteContract.ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case TRAILERS_CODE:
                returnedCursor = sqLiteDatabase.query(FavoriteContract.TrailerEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new android.database.SQLException("Unknown uri " + uri);
        }


        returnedCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnedCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int code = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mMovieDbHelper.getWritableDatabase();
        Uri insertedUri = null;
        long insertedId;

        switch (code) {
            case MOVIES_CODE:
                insertedId = sqLiteDatabase.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
                if (insertedId > 0) {
                    insertedUri = FavoriteContract.FavoriteEntry.MOVIES_URI.buildUpon().appendPath(String.valueOf(insertedId)).build();
                } else {
                    throw new android.database.SQLException("Failed to insert row " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return insertedUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase sqLiteDatabase = mMovieDbHelper.getWritableDatabase();

        int code = sUriMatcher.match(uri);
        int rowsInserted = 0;
        switch (code) {
            case REVIEWS_CODE:
                sqLiteDatabase.beginTransaction();
                try {
                    for (ContentValues cv : values) {
                        long id = sqLiteDatabase.insert(FavoriteContract.ReviewEntry.TABLE_NAME, null, cv);
                        if(id != -1) {
                            rowsInserted++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }
                if(rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            case TRAILERS_CODE:
                sqLiteDatabase.beginTransaction();
                try {
                    for (ContentValues cv : values) {
                        long id = sqLiteDatabase.insert(FavoriteContract.TrailerEntry.TABLE_NAME, null, cv);
                        if(id != -1) {
                            rowsInserted++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }
                if(rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mMovieDbHelper.getWritableDatabase();

        int deleterRows = 0;
        switch (code) {
            case MOVIES_CODE:
                deleterRows = sqLiteDatabase.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_WITH_ID_CODE:
                String id = uri.getPathSegments().get(1);
                String mSelection = FavoriteContract.FavoriteEntry.MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};
                deleterRows = sqLiteDatabase.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, mSelection, mSelectionArgs);
                break;

            case REVIEWS_CODE:
                deleterRows = sqLiteDatabase.delete(FavoriteContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case TRAILERS_CODE:
                deleterRows = sqLiteDatabase.delete(FavoriteContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if (deleterRows < 0) {
            throw new android.database.SQLException("Faild to delete row " + uri);
        }

        return deleterRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
