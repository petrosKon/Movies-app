package com.example.android.applicationmovies;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by mac-lab on 10/11/2018.
 */

@Entity
public class FavMovie {

    @NonNull
    @PrimaryKey
    private int movieId;
    private String movieName;
    private String overview;
    private String posterPath;
    private Double voteAverage;
    private String releaseDate;
    private String title;
    private String BackdropPath;

    public String getBackdropPath() {
        return BackdropPath;
    }

    public void setBackdropPath(String getBackdropPath) {
        this.BackdropPath = getBackdropPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public FavMovie() {
    }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public String getMovieName() { return movieName; }
    public void setMovieName (String movieName) { this.movieName = movieName; }
}
