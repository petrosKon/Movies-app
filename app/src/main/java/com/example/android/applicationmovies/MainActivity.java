package com.example.android.applicationmovies;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.android.applicationmovies.model.ExtraInfo;
import com.example.android.applicationmovies.model.Genre;
import com.example.android.applicationmovies.model.GenreResponse;
import com.example.android.applicationmovies.model.Movie;
import com.example.android.applicationmovies.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.applicationmovies.MoviesAdapter.movieIDs;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "3b65a4f0f5b041dc0cd23d8ac50c9bcc";

    public List<Movie> mPopularList;

    private List<Genre> mGenres;

    private List<Movie> sortedList;

    public static final String LANGUAGE = "en-US";

    private ProgressDialog pDialog;

    private int currentPage = 1;

    private SwipeRefreshLayout swipeRefreshLayout;

    private MoviesAdapter adapter;

    private Api api;

    private Call<ExtraInfo> callMovies;

    private Call<GenreResponse> callGenres;

    private RecyclerView recyclerView;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private Toolbar mTopToolbar;

    public static FavMovieDatabase movieDatabase;

    private static final String DATABASE_NAME = "FavMovies_db";

    private List<FavMovie> favMoviesList = new ArrayList<>();

    public static boolean sortingByRating = false;

    public static boolean showLatestMovies;

    public static boolean filterByReleaseDate;

    final String[] years = {"2018", "2017", "2016", "2015", "2014", "2010-1990", "1989-1930"};

    ArrayList<Integer> slist = new ArrayList();

    boolean icount[] = new boolean[years.length];

    String msg = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.movies_list);
        //swipeRefreshLayout = findViewById(R.id.swipeContainer);

        api = RetrofitClientInstance.getRetrofitInstance().create(Api.class);

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        if (movieDatabase == null) {

            movieDatabase = Room.databaseBuilder(getApplicationContext(), FavMovieDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();

        }

        mTopToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

        populateOurDatabase();

        GetInfo(currentPage);

        setupOnScrollListener();





        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Clear();
            }
        });*/
    }


    public void Clear() {


        mPopularList.clear();

        currentPage = 1;
        GetInfo(currentPage);
        generateMovieList(mPopularList, mGenres);


        adapter.notifyDataSetChanged();

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

            filterByReleaseDate = false;

            sortingByRating = false;

            showLatestMovies = false;

            mPopularList.clear();

            sortedList.clear();

            adapter = null;

            currentPage = 1;

            GetInfo(currentPage);

            generateMovieList(mPopularList, mGenres);

            Toast.makeText(getApplicationContext(), "Popular", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.fav_list) {

            filterByReleaseDate = false;

            showLatestMovies = false;

            sortingByRating = false;

            sortedList.clear();

            Intent intent = new Intent(MainActivity.this, FavouriteList.class);

            startActivity(intent);

        } else if (id == R.id.top_rated_movies) {
            filterByReleaseDate = false;

            sortingByRating = true;

            showLatestMovies = false;

            mPopularList.clear();

            sortedList.clear();

            adapter = null;

            currentPage = 1;

            GetInfo(currentPage);

            generateMovieList(mPopularList, mGenres);

            Toast.makeText(getApplicationContext(), "Top Rated", Toast.LENGTH_SHORT).show();


        } else if (id == R.id.release_date) {

            filterByReleaseDate = false;

            showLatestMovies = true;

            sortingByRating = true;

            mPopularList.clear();

            sortedList.clear();

            adapter = null;

            currentPage = 1;

            GetInfo(currentPage);

            filterMovies(mPopularList);

            generateMovieList(mPopularList, mGenres);

            Toast.makeText(getApplicationContext(), "This Years Top Movies", Toast.LENGTH_SHORT).show();


        } else if (id == R.id.sort_movies) {

            adapter = null;

            removeDuplicates(sortedList);

            sortMovies(sortedList);

            generateMovieList(sortedList, mGenres);

            Toast.makeText(getApplicationContext(), "Sorting", Toast.LENGTH_SHORT).show();

        } else {

            if ((sortingByRating && !showLatestMovies) || (!sortingByRating && !showLatestMovies))  {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Choose Years")
                        .setMultiChoiceItems(years, icount, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1, boolean arg2) {

                                if (arg2) {

                                    // If user select a item then add it in selected items
                                    slist.add(arg1);

                                } else if (slist.contains(arg1)) {

                                    // if the item is already selected then remove it
                                    slist.remove(Integer.valueOf(arg1));

                                    adapter = null;

                                    filterByReleaseDate = true;

                                    removeDuplicates(sortedList);

                                    filterMoviesByYear(sortedList, years, slist);

                                    generateMovieList(sortedList, mGenres);

                                }

                            }
                        }).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        msg = "";
                        for (int i = 0; i < slist.size(); i++) {

                            msg = msg + "\n" + (i + 1) + " : " + years[slist.get(i)];
                            Log.d("msg",msg);

                        }

                        adapter = null;

                        filterByReleaseDate = true;

                        removeDuplicates(sortedList);

                        filterMoviesByYear(sortedList, years, slist);

                        generateMovieList(sortedList, mGenres);

                        Toast.makeText(getApplicationContext(), "Total " + slist.size() + " Years Selected.\n" + msg, Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "No Option Selected", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }

        return true;

    }


    public void GetInfo(int page) {

        callMovies = api.getPopularMovies(API_KEY, LANGUAGE, page);
        callGenres = api.getGenres(API_KEY, LANGUAGE);

        callGenres.enqueue(new Callback<GenreResponse>() {

            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {

                if (response.isSuccessful()) {

                    GenreResponse genreResponse = response.body();

                    if (genreResponse != null) {

                        mGenres = genreResponse.getGenres();

                    }
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Something Happened Please Check Your Connection", Toast.LENGTH_SHORT).show();

            }
        });

        callMovies.enqueue(new Callback<ExtraInfo>() {

            @Override
            public void onResponse(Call<ExtraInfo> call, Response<ExtraInfo> response) {

                if (response.isSuccessful()) {

                    ExtraInfo extraInfo = response.body();

                    if (extraInfo != null) {

                        mPopularList = extraInfo.getMovies();
                        pDialog.dismiss();

                        if (sortedList == null) {

                            sortedList = extraInfo.getMovies();

                        } else {
                            sortedList.addAll(extraInfo.getMovies());
                            Log.d("sorted list size", Integer.toString(sortedList.size()));
                        }

                        if (filterByReleaseDate) {

                            removeDuplicates(sortedList);
                            filterMoviesByYear(sortedList, years, slist);
                            generateMovieList(sortedList, mGenres);

                        } else {

                            if (showLatestMovies) {

                                removeDuplicates(mPopularList);
                                filterMovies(mPopularList);
                                generateMovieList(mPopularList, mGenres);


                            } else {

                                removeDuplicates(mPopularList);
                                generateMovieList(mPopularList, mGenres);

                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ExtraInfo> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Something Happened Please Check Your Connection", Toast.LENGTH_SHORT).show();

            }

        });

        currentPage = page;
    }


    private void generateMovieList(List<Movie> movieList, List<Genre> genreList) {

        if (adapter == null) {

            if (!sortingByRating) {
                adapter = new MoviesAdapter(MainActivity.this, movieList, genreList);
                recyclerView.setAdapter(adapter);
            } else {

                sortMovies(movieList);
                adapter = new MoviesAdapter(MainActivity.this, movieList, genreList);
                recyclerView.setAdapter(adapter);
            }

        } else {

            if (!sortingByRating) {

                adapter.appendMovies(movieList);
            } else {
                sortMovies(movieList);
                adapter.appendMovies(movieList);

            }

        }

    }

    private void setupOnScrollListener() {

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                visibleItemCount = manager.getChildCount();
                totalItemCount = manager.getItemCount();
                pastVisiblesItems = manager.findFirstVisibleItemPosition();

                if (!sortingByRating) {

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount / 2) {

                        GetInfo(currentPage + 1);

                    }

                } else {

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                        GetInfo(currentPage + 1);

                    }


                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);

    }

    public void populateOurDatabase() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                if (movieDatabase.daoAccess().fetchAllMovies() != null) {

                    favMoviesList.addAll(movieDatabase.daoAccess().fetchAllMovies());

                    for (FavMovie movie : favMoviesList) {

                        if (movieIDs != null && !movieIDs.contains(movie.getMovieId()))

                            movieIDs.add(movie.getMovieId());

                        Log.d("idsss", movieIDs.toString());
                    }

                    Log.d("show", "ended");

                }
            }

        }).start();
    }

    public void sortMovies(List<Movie> popularList) {

        if (popularList != null) {

            Collections.sort(popularList, new Comparator<Movie>() {

                @Override
                public int compare(Movie movie, Movie movie2) {

                    return movie.getVoteAverage().compareTo(movie2.getVoteAverage());

                }

            });

            Collections.reverse(popularList);

        }

    }

    public void filterMovies(List<Movie> popularList) {

        List<Movie> oldMovies = new ArrayList<>();

        if (popularList != null) {

            for (Movie movie : popularList) {

                if (!movie.getReleaseDate().contains("2018") || movie.getVoteAverage() < 7) {
                    oldMovies.add(movie);
                }

            }

            popularList.removeAll(oldMovies);
        }
    }


    public void filterMoviesByYear(List<Movie> movieList, String[] totalYears, ArrayList<Integer> list) {

        List<Movie> newMovies = new ArrayList<>();
        //Log.d("years", Arrays.toString(years));

        if (movieList != null) {

            for (Movie movie : movieList) {

                for (int i = 0; i < list.size(); i++) {

                    Log.d("years", totalYears[list.get(i)]);

                    if (totalYears[list.get(i)] != "2010-1990" && totalYears[list.get(i)] != "1989-1930") {
                        if (movie.getReleaseDate().contains(totalYears[list.get(i)])) {

                            newMovies.add(movie);

                        }
                    } else if (totalYears[list.get(i)] == "2010-1990") {

                        for (int k = 1990; k <= 2010; k++) {

                            if (movie.getReleaseDate().contains(Integer.toString(k))) {

                                newMovies.add(movie);

                            }
                        }

                    } else if (totalYears[list.get(i)] == "1989-1930") {

                        for (int k = 1930; k <= 1989; k++) {

                            if (movie.getReleaseDate().contains(Integer.toString(k))) {

                                newMovies.add(movie);

                            }
                        }
                    }
                }
            }
        }

        movieList.clear();
        movieList.addAll(newMovies);
    }


    public void removeDuplicates(List<Movie> movieList) {

        HashSet<Movie> movies = new HashSet<>(movieList);

        movieList.clear();

        movieList.addAll(movies);

    }
}



