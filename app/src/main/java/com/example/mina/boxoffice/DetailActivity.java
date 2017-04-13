package com.example.mina.boxoffice;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mina.boxoffice.Adapters.ReviewAdapter;
import com.example.mina.boxoffice.Adapters.TrailersAdapter;
import com.example.mina.boxoffice.Model.Movie;
import com.example.mina.boxoffice.Model.MovieReview;
import com.example.mina.boxoffice.Model.MovieTrailer;
import com.example.mina.boxoffice.Utils.JsonUtils;
import com.example.mina.boxoffice.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerOnCLickListener{
    @BindView(R.id.movie_title) TextView titleTextView;
    @BindView(R.id.release_date) TextView releaseDateTextView;
    @BindView(R.id.rate_text_view) TextView rateTextView;
    @BindView(R.id.plot) TextView plotTextView;
    @BindView(R.id.detail_poster) ImageView moviePoster;
    @BindView(R.id.reviews_recycler_view) RecyclerView reviewsRecyclerView;
    @BindView(R.id.trailers_recycler_view) RecyclerView trailersRecyclerView;
    @BindView(R.id.no_reviews_view) TextView noReviewsView;
    @BindView(R.id.no_trailers_view) TextView noTrailersView;

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private ArrayList<MovieTrailer> mTrailers;
    private ArrayList<MovieReview> mReviews;

    public static final String REVIEWS_URL_KEY = "reviews_url";
    public static final String TRAILERS_URL_KEY = "trailers_url";

    public static final int REVIEWS_LOADER_ID = 777;
    public static final int TRAILERS_LOADER_ID = 888;

    private ReviewAdapter mReviewsAdapter;
    private TrailersAdapter mTrailersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie selectedMovie = getIntent().getParcelableExtra(MainActivity.SELECTED_MOVIE);

        mReviews = new ArrayList<>();
        mTrailers = new ArrayList<>();

        Bundle loaderBundle = new Bundle();
        Context context = DetailActivity.this;
        String movieIdString = String.valueOf(selectedMovie.getmMovieId());
        String reviewsUrl = NetworkUtils.buildReviewsUrl(context, movieIdString);
        String trailersUrl = NetworkUtils.buildTrailersUrl(context, movieIdString);
        loaderBundle.putString(REVIEWS_URL_KEY, reviewsUrl);
        loaderBundle.putString(TRAILERS_URL_KEY, trailersUrl);

        ButterKnife.bind(this);

        Picasso.with(context)
                .load(NetworkUtils.buildPosterUrl(context, selectedMovie.getmPosterPath()))
                .error(ContextCompat.getDrawable(context, R.drawable.noposter))
                .into(moviePoster);

        titleTextView.setText(selectedMovie.getmTitle());
        releaseDateTextView.setText(dateFormatter(selectedMovie.getmReleaseDate().substring(0, 4)));
        rateTextView.setText(String.valueOf(selectedMovie.getmRate()));
        plotTextView.setText(selectedMovie.getmPlot());


        if(NetworkUtils.isConnected(context)) {
            getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, loaderBundle, new ReviewsLoader());
            getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, loaderBundle, new TrailersLoader());
        } else {
            showEmptyReviewsView();
        }

        mTrailersAdapter = new TrailersAdapter(mTrailers, this);
        LinearLayoutManager horizontalLinearLayout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        trailersRecyclerView.setLayoutManager(horizontalLinearLayout);
        trailersRecyclerView.setAdapter(mTrailersAdapter);

        mReviewsAdapter = new ReviewAdapter(mReviews);
        LinearLayoutManager verticalLinearLayout = new LinearLayoutManager(context);
        reviewsRecyclerView.setLayoutManager(verticalLinearLayout);
        reviewsRecyclerView.setAdapter(mReviewsAdapter);

    }

    public   String dateFormatter(String date) {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return  ("(" + date + ")");
        } else {
            return date;
        }
    }

    private class ReviewsLoader implements LoaderManager.LoaderCallbacks<List<MovieReview>> {
        @Override
        public Loader<List<MovieReview>> onCreateLoader(int id, Bundle args) {
            final String reviewsUrlString = args.getString(REVIEWS_URL_KEY);
            return new AsyncTaskLoader<List<MovieReview>>(DetailActivity.this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public List<MovieReview> loadInBackground() {
                    URL reviewsUrl = NetworkUtils.getUrl(reviewsUrlString);
                    String jsonResponse = NetworkUtils.getJsonResponse(reviewsUrl, DetailActivity.this);
                    return JsonUtils.extractReviewsFromJson(jsonResponse, DetailActivity.this);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> data) {
            mReviews.clear();
            mReviews.addAll(data);
            if(data == null || data.size() < 1) {
                showEmptyReviewsView();
            }

            mReviewsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<MovieReview>> loader) {

        }
    }

    private class TrailersLoader implements LoaderManager.LoaderCallbacks<List<MovieTrailer>> {

        @Override
        public Loader<List<MovieTrailer>> onCreateLoader(int id, Bundle args) {
            final String trailersUrlString = args.getString(TRAILERS_URL_KEY);
            return new AsyncTaskLoader<List<MovieTrailer>>(DetailActivity.this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public List<MovieTrailer> loadInBackground() {
                    URL trailersUrl = NetworkUtils.getUrl(trailersUrlString);
                    String jsonResponse = NetworkUtils.getJsonResponse(trailersUrl, DetailActivity.this);
                    return JsonUtils.extractTrailersFromJson(jsonResponse, DetailActivity.this);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<MovieTrailer>> loader, List<MovieTrailer> data) {
            mTrailers.clear();
            mTrailers.addAll(data);
            if(data == null || data.size() < 1) {
                showEmptyTrailersView();
            }

            mTrailersAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<MovieTrailer>> loader) {

        }
    }

    public void showEmptyReviewsView() {
        reviewsRecyclerView.setVisibility(View.INVISIBLE);
        noReviewsView.setVisibility(View.VISIBLE);
    }

    private void showEmptyTrailersView() {
        trailersRecyclerView.setVisibility(View.INVISIBLE);
        noTrailersView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int position) {
        Context context = DetailActivity.this;
        MovieTrailer currentMovieTrailer = mTrailers.get(position);
        String trailerKey = currentMovieTrailer.getmKey();
        Uri trailerUri = NetworkUtils.buildTrailerUri(context, trailerKey);

        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
        if (trailerIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(trailerIntent);
        } else {
            Toast.makeText(context, "There is no app can grant your request try installing Youtube App of any browser", Toast.LENGTH_SHORT).show();
        }

    }
}
