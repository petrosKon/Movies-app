package com.example.android.applicationmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.android.applicationmovies.MainActivity.movieDatabase;
import static com.example.android.applicationmovies.MainActivity.showLatestMovies;
import static com.example.android.applicationmovies.MoviesAdapter.movieIDs;
import static com.example.android.applicationmovies.MainActivity.sortingByRating;


/**
 * Created by mac-lab on 08/11/2018.
 */

public class FavouriteList extends AppCompatActivity {

    public ArrayList<FavMovie> favouriteMovies = new ArrayList<>();

    private FavouriteListAdapter favAdapter;

    public String TAG = FavouriteList.class.getSimpleName();

    private ProgressDialog pDialog;

    private Toolbar mTopToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_list);

        mTopToolbar = findViewById(R.id.my_toolbar2);
        setSupportActionBar(mTopToolbar);


        new DisplayMovies().execute();

        Log.d(TAG,Integer.toString(movieIDs.size()));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.popular_list) {

            sortingByRating = false;

            Intent intent = new Intent(FavouriteList.this,MainActivity.class);

            startActivity(intent);

            return true;

        }else if(id == R.id.fav_list){

            Toast.makeText(getApplicationContext(),"You are already here",Toast.LENGTH_SHORT).show();

        }else if(id == R.id.top_rated_movies){

            sortingByRating = true;

            Intent intent = new Intent(FavouriteList.this,MainActivity.class);

            startActivity(intent);

            return true;

        } else{

            sortingByRating = true;

            showLatestMovies = true;

            Intent intent = new Intent(FavouriteList.this,MainActivity.class);

            startActivity(intent);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public class DisplayMovies extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute () {

        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(FavouriteList.this);
        pDialog.setMessage("Loading Favourite Movies...");
        pDialog.setCancelable(false);
        pDialog.show();

    }


        @Override
        protected Void doInBackground (Void...params){

        if (!movieIDs.isEmpty()) {

            for (int i = 0; i <= movieIDs.size(); i++) {

                try {

                    favouriteMovies.add(movieDatabase.daoAccess().fetchOneMoviebyMovieId(movieIDs.get(i)));

                } catch (IndexOutOfBoundsException e) {

                }

            }
        }

        return null;

    }

        @Override
        protected void onPostExecute (Void result){

        super.onPostExecute(result);
        RecyclerView recyclerView = findViewById(R.id.favorView);

        favAdapter = new FavouriteListAdapter(getApplicationContext(), favouriteMovies);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.setAdapter(favAdapter);

        favAdapter.notifyDataSetChanged();

        pDialog.dismiss();

        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FavouriteList.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


}
