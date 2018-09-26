package com.example.android.gsonparse;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.gsonparse.database.DatabaseContract;
import com.example.android.gsonparse.database.DatabaseOpenHelper;
import com.example.android.gsonparse.reviews.Review;
import com.example.android.gsonparse.reviews.ReviewsAdapter;
import com.example.android.gsonparse.reviews.ReviewsResult;
import com.example.android.gsonparse.trailers.Root;
import com.example.android.gsonparse.trailers.TrailersAdapter;
import com.example.android.gsonparse.trailers.VideoResults;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;


public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.ItemClickListener{

    private int defaultValue = 0;

    // Movie info
    String title;
    String releaseDate;
    String moviePoster;
    double voteAverage;
    String plot;
    int id;
    String movieID;
    int favorite;

    private static final String API_KEY = "?api_key=" + BuildConfig.API_KEY;
    private String baseURLImagew185="http://image.tmdb.org/t/p/w185";
    private String baseURLReviews = "http://api.themoviedb.org/3/movie/";
    private String baseURLVideos = "http://api.themoviedb.org/3/movie/";



    private TextView mTitleTextView;
    private TextView mPlotTextView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;

    Uri mUri;

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            DatabaseContract.MovieTable.ID,
            DatabaseContract.MovieTable.TITLE,
            DatabaseContract.MovieTable.RELEASEDATE,
            DatabaseContract.MovieTable.IMAGELINK,
            DatabaseContract.MovieTable.USERRATING,
            DatabaseContract.MovieTable.PLOT,
            DatabaseContract.MovieTable.FAVORITE
    };

    private RatingBar mRatingBar;

    private ImageView posterImageView;
    private ImageView favoriteButton;
    private boolean isFavorite = false;

    private List<ReviewsResult> reviewsResultList = null;
    private List<VideoResults> videosResultList = null;


    private RecyclerView reviewsRecyclerView;
    private RecyclerView trailersRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);



        getExtras();



        String europeanDate = switchOrderDate(releaseDate);

        posterImageView = findViewById(R.id.imageViewPoster);
        favoriteButton = findViewById(R.id.favoriteButton);


        mTitleTextView = findViewById(R.id.textViewTitle);
        mPlotTextView = findViewById(R.id.textViewPlot);
        mReleaseDateTextView = findViewById(R.id.textViewReleaseDate);
        mVoteAverageTextView = findViewById(R.id.textViewVoteAverage);


        mRatingBar = findViewById(R.id.ratingBar);

        // Set texts
        mTitleTextView.setText(title);
        mPlotTextView.setText(plot);
        mReleaseDateTextView.setText(europeanDate);
        mVoteAverageTextView.setText(String.valueOf(voteAverage));




        //Set image
       //  String imageLink = baseURLImagew185 + moviePoster;
        // Picasso load poster to image
        Picasso.get()
                .load(moviePoster)
//                .error(R.mipmap.)
//                .resize(200,200)
//                .centerCrop()
                .into(posterImageView);

        float rating = (float) voteAverage;
        mRatingBar.setRating(rating);

        // Check if it is favorite
        isFavorite = checkFavorite();

        setPicture(isFavorite);
        onClickListener();

        //Recycler View for reviews
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        fetchReviewData(baseURLReviews);

        //Recycler View for trailers
        trailersRecyclerView = findViewById(R.id.trailersRecyclerView);
        trailersRecyclerView.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        trailersRecyclerView.setLayoutManager(horizontalLayoutManager);
        trailersRecyclerView.setItemAnimator(new DefaultItemAnimator());

        fetchTrailerData(baseURLVideos);

    }

    // Get intent extras
    private void getExtras() {
        Intent intent = getIntent();
        mUri = intent.getData();

      Cursor cursor =  getContentResolver().query(mUri, MOVIE_DETAIL_PROJECTION, null, null, null);

      cursor.moveToFirst();
      title = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.TITLE));
      releaseDate = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.RELEASEDATE));
      moviePoster = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.IMAGELINK));

      String voteAverageString = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.USERRATING));
      voteAverage = Double.valueOf(voteAverageString);

       plot = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.PLOT));

       movieID = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.ID));

       String favoriteString  = cursor.getString(cursor.getColumnIndex(DatabaseContract.MovieTable.FAVORITE));
       favorite = Integer.parseInt(favoriteString);

       baseURLVideos = "http://api.themoviedb.org/3/movie/" + movieID + "/videos" + API_KEY;
       Log.d("VIDEOSYOUTUBETRAILER", baseURLVideos);
       baseURLReviews = "http://api.themoviedb.org/3/movie/" + movieID + "/reviews" + API_KEY;




    }

    private String switchOrderDate(String releaseDate) {
        String year = releaseDate.substring(0,4);
        String month = releaseDate.substring(5,7);
        String day = releaseDate.substring(8,10);
        String separator = "/";

        String europeanFormat = new StringBuilder(day).append(separator).append(month).append(separator).append(year).toString();

        return europeanFormat;
    }

    private void onClickListener() {
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // COMPLETED get Content from DB, if favorite is 0 then on click change image and set to 1
                // if content is 1 the onClick change image and set to 0
                isFavorite = changePicture(isFavorite);

            }
        });
    }

    public void updateFavorite(boolean isFavorite) {
        //try to update DB favorite
        ContentValues cv = new ContentValues();
        if (isFavorite) {
            cv.put(DatabaseContract.MovieTable.FAVORITE, "1");
        } else {
            cv.put(DatabaseContract.MovieTable.FAVORITE, "0");
        }

        String selection = "_id = ?";
        String movID = mUri.getLastPathSegment();
        String[] selectionArguments = new String[]{movID};



        favorite = getContentResolver().update(mUri, cv, selection, selectionArguments);
    }

    public boolean checkFavorite() {

        return favorite > 0 ? true : false ;
    }


    private void setPicture(boolean isFavorite) {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.btn_star_on);
        } else {
            favoriteButton.setImageResource(R.drawable.btn_star_off);
        }

    }

    private boolean changePicture(boolean isFavorite) {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.btn_star_off);
            isFavorite = false;
            updateFavorite(isFavorite);
        } else {
            favoriteButton.setImageResource(R.drawable.btn_star_on);
            isFavorite = true;
            updateFavorite(isFavorite);
        }
        return isFavorite;
    }



    public void fetchReviewData(String url) {

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson gson = new Gson();
                Review gsonParse = gson.fromJson(response, Review.class);

                reviewsResultList = gsonParse.getResults();

                ReviewsAdapter adapter = new ReviewsAdapter(DetailsActivity.this, reviewsResultList);

                reviewsRecyclerView.setAdapter(adapter);

              reviewsResultList.size();

            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailsActivity.this, "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
            }
        });

        ConnectionManager.getInstance(this).add(request);

    }

    public void fetchTrailerData(String url) {

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson gson = new Gson();
                Root gsonParse = gson.fromJson(response, Root.class);

                videosResultList = gsonParse.getResults();

                if (videosResultList.size() != 0) {

                    TrailersAdapter adapter = new TrailersAdapter(DetailsActivity.this, videosResultList);

                    trailersRecyclerView.setAdapter(adapter);

                    adapter.setClickListener(DetailsActivity.this);
                } else {
                    trailersRecyclerView.setVisibility(View.GONE);
                }

            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                The toast is already being shown in fetchTrailerData
//                Toast.makeText(DetailsActivity.this, "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
            }
        });

        ConnectionManager.getInstance(this).add(request);

    }

    @Override
    public void onItemClick(View view, int position) {
        String key = videosResultList.get(position).getKey();
        watchYoutubeVideo(this, key);
    }

    // Intent to youtube
    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

}
