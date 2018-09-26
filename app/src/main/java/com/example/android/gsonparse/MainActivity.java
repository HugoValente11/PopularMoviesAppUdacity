package com.example.android.gsonparse;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.gsonparse.database.DatabaseContract;
import com.example.android.gsonparse.database.DatabaseOpenHelper;
import com.example.android.gsonparse.database.MovieDAO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.krishna.debug_tools.activity.ActivityDebugTools;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CursorAdapter.CursorAdapterOnClickHandler{

    public List<Result> resultList = null;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager gridLayoutManager;
    boolean fav = false;
    private Menu menu;
    private int menuChosen = 0;
    // if menuChosen = 0 then popularChosen
    // if menuChosen = 1 then topRated
    // if menuChosen = 2 then favoritesChosen
    private MenuItem chosenMenuItem;

    private String MENUCHOSENTAG = "MenuChosen";
    private CursorAdapter mAdapter;
    private Cursor cursor;

    //Strings
    private static final String API_KEY = BuildConfig.API_KEY;
    String popularURL = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
    String topRatedURL = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(MENUCHOSENTAG, menuChosen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Recycler View
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CursorAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        // Add popular movies
         fetchListData(popularURL);


        // To show favorites
        // https://stackoverflow.com/questions/28309321/display-sqlite-data-in-recyclerview


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        menuChosen = preferences.getInt("MENUCHOSEN", 0);

        getMovies(menuChosen);

    }

    public void fetchListData(String url) {

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson gson = new Gson();
                GsonParse gsonParse = gson.fromJson(response, GsonParse.class);

                resultList = gsonParse.getResults();

                // cursor = getApplicationContext().getContentResolver().query(DatabaseContract.CONTENT_URI, null, null, null, null);

                // Add popular movies
                 addMovieToDb(resultList, menuChosen);


                getMovies(menuChosen);

            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please check your Internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

        ConnectionManager.getInstance(this).add(request);

    }

    private void addMovieToDb(List<Result> resultList, int menuChosen) {

         Cursor mCursor = getAllMovies();

        for (Result result : resultList) {

            boolean isInDB = false;

            mCursor.moveToFirst();

            Log.d("First1", "Movie Id: " + mCursor.getColumnIndex(DatabaseContract.MovieTable.ID));

            Log.d("First2", "Result Id: " + result.getId());

            while (mCursor.moveToNext()) {
                Log.d("Main1", "Movie id: " + mCursor.getColumnIndex(DatabaseContract.MovieTable.ID));
                Log.d("Main2", "Result position: " + result.getId());

                if (result.getId().equals(mCursor.getInt(mCursor.getColumnIndex(DatabaseContract.MovieTable.ID)))) {
                    isInDB = true;
                }
            }

            if (!isInDB) {

                ContentValues cv = new ContentValues();

                cv.put(DatabaseContract.MovieTable.TITLE, result.getTitle());

                cv.put(DatabaseContract.MovieTable.ID, result.getId());

                String baseURLImagew185 = "http://image.tmdb.org/t/p/w185";
                String imageLink = baseURLImagew185 + result.getPosterPath();
                cv.put(DatabaseContract.MovieTable.IMAGELINK, imageLink);
                cv.put(DatabaseContract.MovieTable.PLOT, result.getOverview());
                cv.put(DatabaseContract.MovieTable.USERRATING, result.getVoteAverage());
                cv.put(DatabaseContract.MovieTable.RELEASEDATE, result.getReleaseDate());
                if (menuChosen == 1) {
                cv.put(DatabaseContract.MovieTable.TOPRATED, 1);
                } else {
                cv.put(DatabaseContract.MovieTable.MOSTPOPULAR, 1);
                }

                Uri uri = getContentResolver().insert(DatabaseContract.CONTENT_URI, cv);
                Log.d("Uri", "Uri: " + uri);
            }
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (menuChosen == 0) {
            chosenMenuItem = menu.findItem(R.id.menuItemChooseSortingPopular);
            onOptionsItemSelected(chosenMenuItem);
        }
        if (menuChosen == 1) {
            chosenMenuItem = menu.findItem(R.id.menuItemChooseSortingTopRated);
            onOptionsItemSelected(chosenMenuItem);
        }
        if (menuChosen == 2) {
            chosenMenuItem = menu.findItem(R.id.menuItemChooseFavorites);
            onOptionsItemSelected(chosenMenuItem);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private Cursor getAllMovies() {
        // fazer query de todos os filmes
        Cursor mCursor;
        mCursor = getContentResolver().query(DatabaseContract.CONTENT_URI, null, null, null, null);
        Log.d("Cursor", "Cursor: " + mCursor.getCount());

        return mCursor;
    }

    private void getMovies(int menuChosen) {
        // fazer query de todos os filmes de uma categoria
        Cursor mCursor;
        String selection;
        String[] selectionArgs = {"1"};

        if (menuChosen == 0){
            selection = "most_popular = ?";
            mCursor =  getContentResolver().query(DatabaseContract.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    null);


        }
        else if (menuChosen == 1) {
            selection = "top_rated = ?";
            mCursor =  getContentResolver().query(DatabaseContract.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    null);

        }
        else {
            selection = "favorite = ?";
            mCursor =  getContentResolver().query(DatabaseContract.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    null);
        }

        Log.d("TAG", "" + mCursor.getCount());

        String cursorInfo = DatabaseUtils.dumpCursorToString(mCursor);
        Log.d("TAG", "Cursor info" + cursorInfo );

        mAdapter.swapCursor(mCursor);

        }


    // Show menus
    // https://www.concretepage.com/android/android-options-menu-example-using-getmenuinflater-inflate-oncreateoptionsmenu-and-onoptionsitemselected
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // COMPLETED handle the checks in the two options to load either the most viewed or the most voted
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemChooseSortingPopular:
                menuChosen = 0;
                getMovies(menuChosen);
                return true;

            case R.id.menuItemChooseSortingTopRated:
                menuChosen = 1;
                fetchListData(topRatedURL);
                getMovies(menuChosen);
                return true;
            case R.id.menuItemChooseFavorites:
                menuChosen = 2;
                getMovies(menuChosen);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("MENUCHOSEN",menuChosen);
        editor.apply();
    }

    @Override
    public void onClick(long id) {
        Intent detailsActivity = new Intent(this, DetailsActivity.class);
        Uri movieUri = DatabaseContract.CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        cursor = getContentResolver().query(movieUri,  null, null, null, null);
        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.TITLE));
        Toast.makeText(this, "Movie title: " + title, Toast.LENGTH_SHORT).show();
        detailsActivity.setData(movieUri);
        startActivity(detailsActivity);

        }
}







