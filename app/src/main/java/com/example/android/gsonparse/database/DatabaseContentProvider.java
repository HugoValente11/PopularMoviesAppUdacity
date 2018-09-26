package com.example.android.gsonparse.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.gsonparse.Result;

import java.util.List;

/**
 * Created by Hugo Valente on 12/03/2018.
 */

public class DatabaseContentProvider extends ContentProvider {

    public static final int MOVIE = 100;
    public static final int MOVIE_WITH_TITLE = 101;

    private static Cursor cursor;

    private DatabaseOpenHelper mDatabaseOpenHelper;

    public static final UriMatcher sURI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURI_MATCHER.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_MOVIES, MOVIE);
        sURI_MATCHER.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_MOVIES + "/#", MOVIE_WITH_TITLE);

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
        cursor = null;

        int match = sURI_MATCHER.match(uri);

        Log.d("uri", "uri" + uri);

        switch (match) {
            case MOVIE:
                cursor = db.query(DatabaseContract.MovieTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIE_WITH_TITLE:
                String id = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{id};

                cursor = db.query(DatabaseContract.MovieTable.TABLE_NAME,
                        projection,
                        DatabaseContract.MovieTable._ID + " = ? ",
                        selectionArguments,
                        null, null, sortOrder
                        );

                break;
                default:
                    Log.e("CONTENTPROVIDERTAG", "Error, something went wrong.");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

        long _id=0;
        Uri returnUri;

        switch (sURI_MATCHER.match(uri)){
            case MOVIE:
                _id = db.insert(DatabaseContract.MovieTable.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ContentUris.withAppendedId(DatabaseContract.CONTENT_URI, _id);
                } else {
                    throw new SQLException("Couldn't insert data into " + uri);
                }
                break;
                default:
                    throw new UnsupportedOperationException("Error no uri" + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable access
        SQLiteDatabase db = new DatabaseOpenHelper(getContext()).getWritableDatabase();

        String[] columns = {DatabaseContract.MovieTable._ID, DatabaseContract.MovieTable.FAVORITE};




        cursor = db.query(DatabaseContract.MovieTable.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToPosition(cursor.getColumnIndex(DatabaseContract.MovieTable._ID));

        //Convert to boolean
        String favorite = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.FAVORITE));
        int fav = Integer.parseInt(favorite);

        db.update(DatabaseContract.MovieTable.TABLE_NAME, values, selection, selectionArgs );


        return fav;
    }


}
