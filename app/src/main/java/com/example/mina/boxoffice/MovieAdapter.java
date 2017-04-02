package com.example.mina.boxoffice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mina on 28/03/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String LOG_TAG =MovieAdapter.class.getSimpleName();
    private List<Movie> mMovies;
    private MovieOnClickListener clickListener;
    private OnReachLastPosition mRefreshListener;
    private int mPageNo;


    public MovieAdapter(List<Movie> movies, MovieOnClickListener listener) {
        this.mMovies = movies;
        this.clickListener = listener;
        mPageNo = 1;
    }

    public interface MovieOnClickListener {
        void onClick(int position);
    }

    public interface OnReachLastPosition {
        void refreshPage(int page);
    }

    public void setOnReachLastPositionListener(OnReachLastPosition refreshListener) {
        this.mRefreshListener = refreshListener;
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
        if (position == mMovies.size() -1) {
            addNextPageMovies();
        }

        holder.bind(position);
    }

    private void addNextPageMovies() {
        if(mRefreshListener != null) {
            mRefreshListener.refreshPage(++mPageNo);
        }
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.movie_poster) ImageView mMoviePoster;
        @BindView(R.id.poster_pb) ProgressBar mLoadingIndicator;
        @BindView(R.id.main_rate) TextView mRateTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
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
                        mMoviePoster.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        showLoadingIndicator();
                    }


                };
            }

            mRateTextView.setText(String.valueOf(rate));
            Context context = itemView.getContext();
            Picasso.with(context)
                    .load(posterUri)
                    .error(ContextCompat.getDrawable(context, R.drawable.noposter))
                    .into(mTarget);
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
