package com.example.android.applicationmovies;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by mac-lab on 10/11/2018.
 */
@Dao
public interface DaoAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleMovie (FavMovie movies);
    @Query("SELECT * FROM FavMovie WHERE movieId = :movieId")
    FavMovie fetchOneMoviebyMovieId(int movieId);
    @Query("SELECT * FROM favmovie")
    List<FavMovie> fetchAllMovies();
    @Delete
    void deleteMovie (FavMovie movies);
}
