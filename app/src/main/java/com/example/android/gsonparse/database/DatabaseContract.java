package com.example.android.gsonparse.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by Hugo Valente on 08/03/2018.
 */

final public class DatabaseContract {

    // The name of the database
    public static final String DB_NAME = "movies_app_db";

    public static final String CONTENT_AUTHORITY = "com.example.android.gsonparse";
    public static final String PATH_MOVIES = "movie";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();



    public abstract class MovieTable implements BaseColumns {

        public static final String TABLE_NAME = "movie_table";

        public static final String TITLE = "title";
        public static final String IMAGELINK = "imagelink";
        public static final String PLOT = "plot";
        public static final String USERRATING = "user_rating_average";
        public static final String RELEASEDATE = "release_date";
        public static final String FAVORITE = "favorite";
        public static final String TOPRATED = "top_rated";
        public static final String MOSTPOPULAR = "most_popular";
        public static final String ID = "id";


    }
}
