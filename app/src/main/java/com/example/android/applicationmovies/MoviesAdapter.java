package com.example.android.applicationmovies;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.applicationmovies.model.Genre;
import com.example.android.applicationmovies.model.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static com.example.android.applicationmovies.MainActivity.movieDatabase;
import static com.example.android.applicationmovies.MainActivity.sortingByRating;

/**
 * Created by mac-lab on 03/11/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> movieList ;

    private List<Genre> genreList;

    private Context context;

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

    private Movie movie;

    public static ArrayList<Integer> movieIDs = new ArrayList<>();



    public MoviesAdapter(Context context, List<Movie> movieList, List<Genre> genreList) {
        this.context = context;
        this.movieList = movieList;
        this.genreList = genreList;

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        view = mInflater.inflate(R.layout.item_movie,parent,false);
        Log.d("ids",movieIDs.toString());
        return new MovieViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        movie = movieList.get(position);
        holder.bind(movie);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }


    class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView releaseDate;
        TextView title;
        TextView rating;
        ImageView poster;
        ToggleButton favButton;
        TextView genres;


        public MovieViewHolder(View itemView) {

            super(itemView);
            releaseDate = itemView.findViewById(R.id.item_movie_release_date);
            title = itemView.findViewById(R.id.item_movie_title);
            rating = itemView.findViewById(R.id.item_movie_rating);
            poster = itemView.findViewById(R.id.item_movie_poster);
            favButton = itemView.findViewById(R.id.btnAddFavourites);
            genres = itemView.findViewById(R.id.item_movie_genre);
            this.setIsRecyclable(false);



            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();

                    Context context = view.getContext();

                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra("Title", movieList.get(position).getTitle());
                    intent.putExtra("Details", movieList.get(position).getOverview());
                    intent.putExtra("Release_date", movieList.get(position).getReleaseDate());
                    intent.putExtra("Path", movieList.get(position).getBackdropPath());
                    intent.putExtra("id", movieList.get(position).getId());

                    context.startActivity(intent);

                }
            });


            favButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(isFavourite()){
                        RemoveFromFavourites();
                    }
                    else {
                        AddToFavourites();
                    }
                }

            });
        }


        private void bind(Movie movie) {

            releaseDate.setText(movie.getReleaseDate().split("-")[0]);
            title.setText(movie.getTitle());
            rating.setText(String.valueOf(movie.getVoteAverage()));
            Glide.with(itemView)
                    .load(IMAGE_BASE_URL + movie.getPosterPath())
                    .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                    .into(poster);
            genres.setText(getGenres(movie.getGenreIds()));
            favButton.setChecked(isFavourite());


        }

        private void AddToFavourites() {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    int pos = getAdapterPosition();
                    FavMovie favorite = new FavMovie();

                    if (movieIDs != null && !movieIDs.contains(Integer.valueOf(movieList.get(pos).getId()))) {

                        movieIDs.add(movieList.get(pos).getId());

                        Log.d(TAG, Integer.toString(movieIDs.size()));
                        Log.d("ids", movieIDs.toString());

                        favorite.setMovieId(movieList.get(pos).getId());
                        favorite.setPosterPath(IMAGE_BASE_URL + movieList.get(pos).getPosterPath());
                        favorite.setOverview(movieList.get(pos).getOverview());
                        favorite.setTitle(movieList.get(pos).getTitle());
                        favorite.setReleaseDate(movieList.get(pos).getReleaseDate());
                        favorite.setVoteAverage(movieList.get(pos).getVoteAverage());
                        favorite.setBackdropPath(movieList.get(pos).getBackdropPath());

                        //inserts the movie into the database

                        movieDatabase.daoAccess().insertOnlySingleMovie(favorite);


                    }
                }
            }).start();

            Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();

        }


        private void RemoveFromFavourites() {

            new Thread(new Runnable() {

                @Override
                public void run() {

                    int pos = getAdapterPosition();

                    FavMovie favorite = new FavMovie();

                    if (movieList != null) {

                        if (movieIDs.contains(movieList.get(pos).getId())) {

                            movieIDs.remove(Integer.valueOf(movieList.get(pos).getId()));
                            Log.d("movie id size",Integer.toString(movieIDs.size()));

                            favorite.setMovieId(movieList.get(pos).getId());
                            favorite.setPosterPath(IMAGE_BASE_URL + movieList.get(pos).getBackdropPath());
                            favorite.setOverview(movieList.get(pos).getOverview());
                            favorite.setTitle(movieList.get(pos).getTitle());
                            favorite.setReleaseDate(movieList.get(pos).getReleaseDate());
                            favorite.setVoteAverage(movieList.get(pos).getVoteAverage());
                            favorite.setBackdropPath(movieList.get(pos).getBackdropPath());

                            //deletes the movie from the database
                            movieDatabase.daoAccess().deleteMovie(favorite);
                        }

                    }
                }
            }).start();

            Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show();
        }


        private String getGenres(List<Integer> genreIds) {

            List<String> movieGenres = new ArrayList<>();

            for (Integer genreId : genreIds) {

                if (genreList != null) {

                    for (Genre genre : genreList) {

                        if (genre != null) {

                            if (genre.getId() == genreId) {

                                movieGenres.add(genre.getName());

                                break;

                            }

                        }

                    }

                }

            }
            return TextUtils.join(", ", movieGenres);

        }


        private boolean isFavourite() {

            boolean isFavourite = false;
            int pos = getAdapterPosition();

            if (movieList != null) {

                    if (movieIDs != null) {

                        if (movieIDs.contains(movieList.get(pos).getId())) {

                            isFavourite = true;

                        }

                    }

            }

            return isFavourite;
        }

    }

        public void appendMovies(List<Movie> moviesToAppend) {

        if(sortingByRating) {
            movieList.addAll(moviesToAppend);
            removeDuplicates(movieList);
            sortMovies(movieList);
            notifyDataSetChanged();


        }
        else {
            movieList.addAll(moviesToAppend);
            notifyDataSetChanged();
        }

        }


    public void sortMovies(List<Movie> popularList){

        if(popularList != null) {

            Collections.sort(popularList, new Comparator<Movie>() {

                @Override
                public int compare(Movie movie, Movie movie2) {

                    return movie.getVoteAverage().compareTo(movie2.getVoteAverage());

                }

            });

            Collections.reverse(popularList);

        }

    }

    public void removeDuplicates(List<Movie> movieList) {

        HashSet<Movie> movies = new HashSet<>(movieList);

        movieList.clear();

        movieList.addAll(movies);

    }


}