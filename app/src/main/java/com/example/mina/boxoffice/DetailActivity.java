package com.example.mina.boxoffice;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mina.boxoffice.Adapters.ReviewAdapter;
import com.example.mina.boxoffice.Adapters.TrailersAdapter;
import com.example.mina.boxoffice.Data.FavoriteContract;
import com.example.mina.boxoffice.Model.Movie;
import com.example.mina.boxoffice.Model.MovieReview;
import com.example.mina.boxoffice.Model.MovieTrailer;
import com.example.mina.boxoffice.Utils.JsonUtils;
import com.example.mina.boxoffice.Utils.NetworkUtils;
import com.example.mina.boxoffice.Utils.ProviderUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerOnCLickListener {
    @BindView(R.id.movie_title)
    TextView titleTextView;
    @BindView(R.id.release_date)
    TextView releaseDateTextView;
    @BindView(R.id.rate_text_view)
    TextView rateTextView;
    @BindView(R.id.plot)
    TextView plotTextView;
    @BindView(R.id.detail_poster)
    ImageView moviePoster;
    @BindView(R.id.reviews_recycler_view)
    RecyclerView reviewsRecyclerView;
    @BindView(R.id.trailers_recycler_view)
    RecyclerView trailersRecyclerView;
    @BindView(R.id.no_reviews_view)
    TextView noReviewsView;
    @BindView(R.id.no_trailers_view)
    TextView noTrailersView;
    @BindView(R.id.favorit_button)
    Button favoriteButon;

    Movie selectedMovie;
    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private ArrayList<MovieTrailer> mTrailers;
    private ArrayList<MovieReview> mReviews;

    public static final String REVIEWS_URL_KEY = "reviews_url";
    public static final String TRAILERS_URL_KEY = "trailers_url";

    public static final int REVIEWS_LOADER_ID = 777;
    public static final int TRAILERS_LOADER_ID = 888;

    private ReviewAdapter mReviewsAdapter;
    private TrailersAdapter mTrailersAdapter;

    private Toast mToast;

    boolean isFavoriteFlag;

    private Bitmap mPosterBitmap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setTitle(R.string.detail_activity_bar_title);

        selectedMovie = getIntent().getParcelableExtra(MainActivity.SELECTED_MOVIE);

        mReviews = new ArrayList<>();
        mTrailers = new ArrayList<>();

        Bundle loaderBundle = new Bundle();
        final Context context = DetailActivity.this;
        String movieIdString = String.valueOf(selectedMovie.getmMovieId());
        String reviewsUrl = NetworkUtils.buildReviewsUrl(context, movieIdString);
        String trailersUrl = NetworkUtils.buildTrailersUrl(context, movieIdString);
        loaderBundle.putString(REVIEWS_URL_KEY, reviewsUrl);
        loaderBundle.putString(TRAILERS_URL_KEY, trailersUrl);

        ButterKnife.bind(this);


        Uri movieUri = FavoriteContract.FavoriteEntry.MOVIES_URI;
        final String selection = FavoriteContract.FavoriteEntry.MOVIE_ID + "=?";
        final String[] selectionArgs = new String[]{String.valueOf(selectedMovie.getmMovieId())};
        final Cursor isFavoriteCursor = getContentResolver().query(movieUri, null, selection, selectionArgs, null);

        if (isFavoriteCursor.getCount() > 0) {
            isFavoriteFlag = true;
        } else {
            isFavoriteFlag = false;
        }
        isFavoriteCursor.close();

        setFavoriteButtonBackground();

        favoriteButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavoriteFlag) {
                    isFavoriteFlag = false;
                    ProviderUtils.deleteFavoriteMovie(context, selectedMovie.getmMovieId());
                    ProviderUtils.deleteMovieReviews(context, selectedMovie.getmMovieId());
                    ProviderUtils.deleteMovieTrailers(context, selectedMovie.getmMovieId());
                    setFavoriteButtonBackground();
                } else {
                    isFavoriteFlag = true;
                    ProviderUtils.insertFavoriteMovie(context, selectedMovie, mPosterBitmap);
                    ProviderUtils.insertFavoriteReviews(context, mReviews, selectedMovie.getmMovieId());
                    ProviderUtils.insertFavoriteTrailers(context, mTrailers, selectedMovie.getmMovieId());
                    setFavoriteButtonBackground();
                }
            }
        });


        titleTextView.setText(selectedMovie.getmTitle());
        releaseDateTextView.setText(dateFormatter(selectedMovie.getmReleaseDate().substring(0, 4)));
        rateTextView.setText(String.valueOf(selectedMovie.getmRate()));
        plotTextView.setText(selectedMovie.getmPlot());

        getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, loaderBundle, new ReviewsLoader());
        getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, loaderBundle, new TrailersLoader());

        if (NetworkUtils.isConnected(context) && selectedMovie.getmPosterPath()!= null) {
            String posterUri = NetworkUtils.buildPosterUrl(context, selectedMovie.getmPosterPath());
            attachPosterImageView(Uri.parse(posterUri));
        } else {
            if(selectedMovie.getmPoster()!=null) {
                moviePoster.setImageBitmap(selectedMovie.getmPoster());
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_share:
                shareFirstTrailer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String dateFormatter(String date) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return ("(" + date + ")");
        } else {
            return date;
        }
    }

    private void setFavoriteButtonBackground() {
        Context context = DetailActivity.this;
        if (isFavoriteFlag) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                favoriteButon.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.favorit_1));
            } else {
                favoriteButon.setBackground(ContextCompat.getDrawable(context, R.drawable.favorit_1));
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                favoriteButon.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.favorit));
            } else {
                favoriteButon.setBackground(ContextCompat.getDrawable(context, R.drawable.favorit));
            }
        }
    }

    ;

    private Target mTarget;

    private void attachPosterImageView(Uri posterUri) {
        if (mTarget == null) {
            mTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    moviePoster.setImageBitmap(bitmap);
                    mPosterBitmap = bitmap;
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    moviePoster.setImageDrawable(errorDrawable);
                    mPosterBitmap = null;
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
        }
        Context context = DetailActivity.this;
        Picasso.with(context)
                .load(posterUri)
                .error(ContextCompat.getDrawable(context, R.drawable.noposter))
                .into(mTarget);
    }

    public void shareFirstTrailer() {
        if (mTrailers != null && mTrailers.size() > 0) {
            Uri trailerUri = NetworkUtils.buildTrailerUri(DetailActivity.this, mTrailers.get(0).getmKey());
            String chooserTitle = "Share Trailer With";
            String mimeType = "text/plain";
            String sharing = trailerUri.toString();

            Intent shareIntent = ShareCompat.IntentBuilder.from(DetailActivity.this)
                    .setChooserTitle(chooserTitle)
                    .setType(mimeType)
                    .setText(sharing)
                    .getIntent();

            startActivity(shareIntent);
        } else {
            if (mToast != null)
                mToast.cancel();

            mToast = Toast.makeText(DetailActivity.this, getString(R.string.no_trailers_view) + " to share", Toast.LENGTH_SHORT);
            mToast.show();
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

    /* Trailers On Click Listener */
    @Override
    public void onClick(int position) {
        Context context = DetailActivity.this;
        if (!NetworkUtils.isConnected(context)) {
            if(mToast != null)
                mToast.cancel();
            mToast = Toast.makeText(context, "You Have To be Online to See Trailers", Toast.LENGTH_SHORT);
            mToast.show();
            return;
        }

        MovieTrailer currentMovieTrailer = mTrailers.get(position);
        String trailerKey = currentMovieTrailer.getmKey();
        Uri trailerUri = NetworkUtils.buildTrailerUri(context, trailerKey);

        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
        if (trailerIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(trailerIntent);
        } else {
            if (mToast != null)
                mToast.cancel();

            mToast = Toast.makeText(context, "There is no app can grant your request try installing Youtube App of any browser", Toast.LENGTH_SHORT);
            mToast.show();
        }

    }

    /* Movie Extra Information Loaders */
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
                    Context context = DetailActivity.this;
                    // Get Reviews from Content Provider if The Movie is Favorite
                    if(!NetworkUtils.isConnected(context) || isFavoriteFlag) {
                        return ProviderUtils.getMovieReviews(context, selectedMovie.getmMovieId());
                    } else {
                        URL reviewsUrl = NetworkUtils.getUrl(reviewsUrlString);
                        String jsonResponse = NetworkUtils.getJsonResponse(reviewsUrl, DetailActivity.this);
                        return JsonUtils.extractReviewsFromJson(jsonResponse, DetailActivity.this);
                    }

                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> data) {
            mReviews.clear();
            mReviews.addAll(data);
            if (data == null || data.size() < 1) {
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
                    Context context = DetailActivity.this;
                    if (!NetworkUtils.isConnected(context)) {
                        return ProviderUtils.getMovieTrailers(context, selectedMovie.getmMovieId());
                    } else {
                        URL trailersUrl = NetworkUtils.getUrl(trailersUrlString);
                        String jsonResponse = NetworkUtils.getJsonResponse(trailersUrl, DetailActivity.this);
                        return JsonUtils.extractTrailersFromJson(jsonResponse, DetailActivity.this);
                    }

                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<MovieTrailer>> loader, List<MovieTrailer> data) {
            mTrailers.clear();
            mTrailers.addAll(data);
            if (data == null || data.size() < 1) {
                showEmptyTrailersView();
            }

            mTrailersAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<MovieTrailer>> loader) {

        }
    }
    /*------------------------------------------------------------------*/
}
