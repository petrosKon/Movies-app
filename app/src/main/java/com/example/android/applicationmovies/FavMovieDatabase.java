package com.example.android.applicationmovies;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;


/**
 * Created by mac-lab on 10/11/2018.
 */

@Database(entities = {FavMovie.class}, version = 1, exportSchema = false)
public abstract class FavMovieDatabase extends RoomDatabase {


    public abstract DaoAccess daoAccess() ;

}

