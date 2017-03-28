package com.example.mina.boxoffice;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mina.boxoffice.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by mina on 28/03/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> mMovies;
    MovieOnClickListener clickListener;

    public MovieAdapter(List<Movie> movies, MovieOnClickListener listener) {
        this.mMovies = movies;
        this.clickListener = listener;
    }

    public interface MovieOnClickListener {
        public void onClick(int position);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView moviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        void bind(int itemIndex) {
            Context context = itemView.getContext();
            String posterPath = mMovies.get(itemIndex).getmPosterPath();
            String posterUrlString = NetworkUtils.buildPosterUrl(context, posterPath);
            Picasso.with(context).load(Uri.parse(posterUrlString)).into(moviePoster);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            clickListener.onClick(position);
        }
    }
}
