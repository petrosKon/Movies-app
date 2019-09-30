package com.example.android.applicationmovies;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import static com.example.android.applicationmovies.MainActivity.movieDatabase;
import static com.example.android.applicationmovies.MoviesAdapter.movieIDs;


/**
 * Created by mac-lab on 08/11/2018.
 */

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.FavouriteViewHolder> {

    private ArrayList<FavMovie> movieList;

    private Context context;

    private static final String TAG = FavouriteList.class.getSimpleName();

    private FavMovie movie;

    private static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";


    public FavouriteListAdapter(Context context, ArrayList<FavMovie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        view = mInflater.inflate(R.layout.fav_movie,parent,false);
        return new FavouriteViewHolder(view);

    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, int position) {
        movie = movieList.get(position);
        holder.bind(movie);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }


    class FavouriteViewHolder extends RecyclerView.ViewHolder {

        TextView releaseDate;
        TextView title;
        TextView rating;
        ImageView poster;
        ToggleButton favButton;



        public  FavouriteViewHolder(View itemView) {

            super(itemView);
            releaseDate = itemView.findViewById(R.id.item_movie_release_date);
            title = itemView.findViewById(R.id.item_movie_title);
            rating = itemView.findViewById(R.id.item_movie_rating);
            poster = itemView.findViewById(R.id.item_movie_poster);
            favButton = itemView.findViewById(R.id.btnAddFavourites);
            favButton.setChecked(true);



            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();

                    Context context = view.getContext();

                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra("Title",movieList.get(position).getTitle());
                    intent.putExtra("Details",movieList.get(position).getOverview());
                    intent.putExtra("Release_date",movieList.get(position).getReleaseDate());
                    intent.putExtra("Path",movieList.get(position).getBackdropPath());
                    intent.putExtra("id", movieList.get(position).getMovieId());

                    context.startActivity(intent);

                }
            });

            favButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    RemoveFromFavourites();

                }
            });


        }

        public void bind(FavMovie movie) {

                releaseDate.setText(movie.getReleaseDate().split("-")[0]);
                title.setText(movie.getTitle());
                rating.setText(String.valueOf(movie.getVoteAverage()));
                Glide.with(itemView)
                        .load(movie.getPosterPath())
                        .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                        .into(poster);


        }

        public void RemoveFromFavourites(){

            new Thread(new Runnable() {

                @Override
                public void run() {

                    int pos = getAdapterPosition();

                        FavMovie favorite = new FavMovie();

                        if(movieList != null) {

                            if (movieIDs.contains(movieList.get(pos).getMovieId())) {

                                movieIDs.remove(Integer.valueOf(movieList.get(pos).getMovieId()));

                                favorite.setMovieId(movieList.get(pos).getMovieId());
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


            Toast.makeText(context,"Removed from Favourites",Toast.LENGTH_SHORT).show();

        }
    }
}