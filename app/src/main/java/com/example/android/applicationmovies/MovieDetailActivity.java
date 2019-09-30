package com.example.android.applicationmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.applicationmovies.model.Movie;
import com.example.android.applicationmovies.model.Review;
import com.example.android.applicationmovies.model.ReviewResponse;
import com.example.android.applicationmovies.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.applicationmovies.MainActivity.API_KEY;

/**
 * Created by mac-lab on 05/11/2018.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";


    private ImageView movieBackdrop;
    private TextView movieTitle;
    private TextView movieOverview;
    private TextView movieOverviewLabel;
    private TextView movieReleaseDate;
    private LinearLayout movieReviews;
    private String review;
    private TextView reviewsLabel;


    List<Review> reviewList;

    private Api api;

    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);

        api = RetrofitClientInstance.getRetrofitInstance().create(Api.class);
        int movieId = getIntent().getIntExtra("id", 0);
        getReview(movieId);



        String title = getIntent().getStringExtra("Title");
        String details = getIntent().getStringExtra("Details");
        String releaseDate = getIntent().getStringExtra("Release_date");
        String path = getIntent().getStringExtra("Path");


        movieBackdrop = findViewById(R.id.movieDetailsBackdrop);
        movieTitle = findViewById(R.id.movieDetailsTitle);
        movieOverview = findViewById(R.id.movieDetailsOverview);
        movieReleaseDate = findViewById(R.id.movieDetailsReleaseDate);
        movieReviews = findViewById(R.id.movieReviews);
        reviewsLabel = findViewById(R.id.reviewsLabel);


        movieTitle.setText(title);
        movieOverview.setText(details);
        movieReleaseDate.setText(releaseDate);
        Glide.with(MovieDetailActivity.this)
                .load(IMAGE_BASE_URL + path)
                .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                .into(movieBackdrop);
    }

    private void getReview(int id) {


        api.getReviews(id, API_KEY).enqueue(new Callback<ReviewResponse>() {

            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {

                if (response.isSuccessful()) {

                    ReviewResponse reviewResponse = response.body();

                    if (reviewResponse != null) {
                        reviewList = reviewResponse.getReviews();
                        reviewsLabel.setVisibility(View.VISIBLE);

                        for(Review review : reviewList){

                            View parent = getLayoutInflater().inflate(R.layout.review, movieReviews,false);
                            TextView author = parent.findViewById(R.id.reviewAuthor);
                            TextView content = parent.findViewById(R.id.reviewContent);
                            author.setText(review.getAuthor());
                            content.setText(review.getContent());
                            movieReviews.addView(parent);

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {

            }
        });
    }
}

