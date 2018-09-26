package com.example.android.gsonparse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.gsonparse.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hugo Valente on 08/03/2018.
 */

public class MovieDAO {

    private static MovieDAO sInstance = null;

    public static MovieDAO getInstance() {
        if (sInstance == null) {
            sInstance = new MovieDAO();
        }
        return sInstance;
    }

    //This stores the movies in the Database
    public boolean storeMovies (Context context, List<Result> resultList) {

        List<Result> storedMovies = MovieDAO.getInstance().getMoviesFromDB(context);

        try {
            SQLiteDatabase db = new DatabaseOpenHelper(context).getWritableDatabase();

            db.beginTransaction();

            for (Result result : resultList) {

                boolean isInDB = false;

                for (Result movieStored : storedMovies) {
                    if (result.getTitle().equals(movieStored.getTitle())) {
                        isInDB = true;
                    }
                }

                if (!isInDB) {
                    ContentValues cv = new ContentValues();

                    cv.put(DatabaseContract.MovieTable.TITLE, result.getTitle());

                    String baseURLImagew185="http://image.tmdb.org/t/p/w185";
                    String imageLink = baseURLImagew185 + result.getPosterPath();
                    cv.put(DatabaseContract.MovieTable.IMAGELINK, imageLink);
                    cv.put(DatabaseContract.MovieTable.PLOT, result.getOverview());
                    cv.put(DatabaseContract.MovieTable.USERRATING, result.getVoteCount());
                    cv.put(DatabaseContract.MovieTable.RELEASEDATE, result.getReleaseDate());
                    cv.put(DatabaseContract.MovieTable.FAVORITE, 0);

                    db.insert(DatabaseContract.MovieTable.TABLE_NAME, null, cv);

                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            db.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    // This retrieves the posts from the database
    public List<Result> getMoviesFromDB (Context context) {

        SQLiteDatabase db = new DatabaseOpenHelper(context).getWritableDatabase();

        Cursor cursor = db.query(DatabaseContract.MovieTable.TABLE_NAME, null, null, null, null, null, null, null);

        cursor.moveToFirst();

        List<Result> movieList = new ArrayList<>();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(DatabaseContract.MovieTable.ID));
            String title = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.TITLE));
            String posterLink = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.IMAGELINK));
            String plot = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.PLOT));
            String userRatingString = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.USERRATING));
            double userRating = Double.parseDouble(userRatingString);
            String releaseDate = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.RELEASEDATE));
            String favorite = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.FAVORITE));

            Result result = new Result(title, posterLink, plot, userRating, releaseDate, favorite, id);

            movieList.add(result);

        }

        cursor.close();
        db.close();

        return movieList;
    }

    public void addToFavorite(String movie_title, Context context) {
        SQLiteDatabase db = new DatabaseOpenHelper(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.MovieTable.FAVORITE, "1");
        db.update(DatabaseContract.MovieTable.TABLE_NAME, cv, "title = ?", new String [] {movie_title});
    }

    public void removeFromFavorite(String movie_title, Context context) {
        SQLiteDatabase db = new DatabaseOpenHelper(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.MovieTable.FAVORITE, "0");
        db.update(DatabaseContract.MovieTable.TABLE_NAME, cv, "title = ?", new String [] {movie_title});
    }


    // Check if Favorite is breaking when opening a movie for the first time
    public boolean checkIfFavorite(String movie_id, Context context) {

        String[] columns = {DatabaseContract.MovieTable.ID, DatabaseContract.MovieTable.FAVORITE};
        String selection = "id = ?" ;

        SQLiteDatabase db = new DatabaseOpenHelper(context).getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.MovieTable.TABLE_NAME,
                columns,
                selection,
                new String[] {movie_id},
                null,
                null,
                null
                );


        cursor.moveToPosition(cursor.getColumnIndex(DatabaseContract.MovieTable.TITLE));

        //Convert to boolean
        String favorite = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.FAVORITE));
        int fav = Integer.parseInt(favorite);

        cursor.close();
        db.close();

        return fav > 0 ? true : false ;
    }

    // This retrieves the favorite movies from the database
    public List<Result> getFavoriteMoviesFromDB (Context context) {

        SQLiteDatabase db = new DatabaseOpenHelper(context).getWritableDatabase();

        String selection = "favorite = ?" ;
        String[] selectionArgs = {"1"};
        Cursor cursor = db.query(DatabaseContract.MovieTable.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);

        cursor.moveToFirst();

        List<Result> movieList = new ArrayList<>();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(DatabaseContract.MovieTable.IMAGELINK));
            String title = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.TITLE));
            String posterLink = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.IMAGELINK));
            String plot = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.PLOT));
            String userRatingString = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.USERRATING));
            double userRating = Double.parseDouble(userRatingString);
            String releaseDate = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.RELEASEDATE));
            String favorite = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.FAVORITE));

            Result result = new Result(title, posterLink, plot, userRating, releaseDate, favorite, id);

            movieList.add(result);

            DatabaseUtils.dumpCursor(cursor);


        }


        cursor.close();
        db.close();

        return movieList;
    }
}
