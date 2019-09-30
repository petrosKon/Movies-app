package com.example.android.applicationmovies;

import com.example.android.applicationmovies.model.ExtraInfo;
import com.example.android.applicationmovies.model.GenreResponse;
import com.example.android.applicationmovies.model.Movie;
import com.example.android.applicationmovies.model.ReviewResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("movie/popular")
    Call<ExtraInfo> getPopularMovies(@Query("api_key") String api_key,
                                           @Query("language") String language,
                                           @Query("page") int page);

    @GET("genre/movie/list")
    Call<GenreResponse> getGenres(@Query("api_key") String api_key,
                                  @Query("language") String language);


    @GET("movie/{id}/reviews")
    Call<ReviewResponse> getReviews(@Path("id") int id,
                                    @Query("api_key") String api_key);

}
