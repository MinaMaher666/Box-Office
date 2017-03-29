package com.example.mina.boxoffice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mina.boxoffice.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.util.List;

/**
 * Created by mina on 28/03/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> mMovies;
    private MovieOnClickListener clickListener;

    public MovieAdapter(List<Movie> movies, MovieOnClickListener listener) {
        this.mMovies = movies;
        this.clickListener = listener;
    }

    public interface MovieOnClickListener {
        void onClick(int position);
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
        private ImageView mMoviePoster;
        private ProgressBar mLoadingIndicator;
        private TextView rateTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            mLoadingIndicator = (ProgressBar) itemView.findViewById(R.id.poster_pb);
            rateTextView = (TextView) itemView.findViewById(R.id.main_rate);
            itemView.setOnClickListener(this);
        }

        private Target mTarget;

        void bind(int itemIndex) {
            Context context = itemView.getContext();
            double rate = mMovies.get(itemIndex).getmRate();
            String posterPath = mMovies.get(itemIndex).getmPosterPath();
            String posterUrlString = NetworkUtils.buildPosterUrl(context, posterPath);
            attachPosterImageView(Uri.parse(posterUrlString), rate);
        }


        private void attachPosterImageView(Uri posterUri, double rate) {
            if(mTarget == null) {
                mTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mMoviePoster.setImageBitmap(bitmap);
                        hideLoadingIndicator();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        showLoadingIndicator();
                    }
                };
            }

            rateTextView.setText(String.valueOf(rate));
            Picasso.with(itemView.getContext()).load(posterUri).into(mTarget);
        }

        private void showLoadingIndicator() {
            mMoviePoster.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        private void hideLoadingIndicator() {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mMoviePoster.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            clickListener.onClick(position);
        }
    }
}
