package com.example.android.gsonparse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hugo Valente on 08/03/2018.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper{
    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DEFAULT_0 = " DEFAULT 0";

    private static final String COMMA = ", ";

    // Constructor
    public DatabaseOpenHelper(Context context) {
        super(context, DatabaseContract.DB_NAME, null, DATABASE_VERSION);
    }

    // Create TABLE SENTENCE
    private static final String CREATE_MOVIE_TABLE = "CREATE TABLE " +
            DatabaseContract.MovieTable.TABLE_NAME + " (" +
            DatabaseContract.MovieTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
            DatabaseContract.MovieTable.ID + INTEGER_TYPE + COMMA +
            DatabaseContract.MovieTable.TITLE + TEXT_TYPE + COMMA +
            DatabaseContract.MovieTable.IMAGELINK + TEXT_TYPE + COMMA +
            DatabaseContract.MovieTable.PLOT + TEXT_TYPE + COMMA +
            DatabaseContract.MovieTable.USERRATING + INTEGER_TYPE + COMMA +
            DatabaseContract.MovieTable.RELEASEDATE + TEXT_TYPE + COMMA +
//            Integer to boolean 1 is true is favorite 0 is false
//            https://stackoverflow.com/questions/843780/store-boolean-value-in-sqlite
            DatabaseContract.MovieTable.FAVORITE + INTEGER_TYPE + DEFAULT_0 + COMMA +
            DatabaseContract.MovieTable.TOPRATED + INTEGER_TYPE + DEFAULT_0 + COMMA +
            DatabaseContract.MovieTable.MOSTPOPULAR + INTEGER_TYPE + DEFAULT_0 +
            " )";

    // Delete TABLE SENTENCE
    public static final String DROP_MOVIE_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.MovieTable.TABLE_NAME;

    static final String DATABASE_ALTER_FAVORITE_COLUMN = "ALTER TABLE " + DatabaseContract.MovieTable.TABLE_NAME +
            "ADD COLUMN " + DatabaseContract.MovieTable.FAVORITE + " string;";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(DROP_MOVIE_TABLE);
        }
        if (oldVersion < 3) {
            db.execSQL(DROP_MOVIE_TABLE);
        }
       onCreate(db);
    }



}
