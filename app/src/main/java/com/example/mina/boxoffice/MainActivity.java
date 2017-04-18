package com.example.mina.boxoffice;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mina.boxoffice.Adapters.MovieAdapter;
import com.example.mina.boxoffice.Adapters.SpinnerAdapter;
import com.example.mina.boxoffice.Model.Movie;
import com.example.mina.boxoffice.Utils.JsonUtils;
import com.example.mina.boxoffice.Utils.NetworkUtils;
import com.example.mina.boxoffice.Utils.ProviderUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>, MovieAdapter.MovieOnClickListener,
        Spinner.OnItemSelectedListener{
    @BindView(R.id.empty_list_message_text_view) View mEmptyListMessageView;
    @BindView(R.id.movies_recycler_view) RecyclerView mRecyclerView;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int LOADER_ID = 666;
    public String userSortChoice;
    Toast mToast;
    private List<Movie> mMovies;
    private MovieAdapter movieAdapter;

    public static final int LANDSCAPE_SPAN_COUNT = 3;
    public static final int PORTRAIT_SPAN_COUNT = 2;

    public static final String SELECTED_MOVIE = "selected_movie";
    public static final String SORT_USER_CHOICE_BUNDLE_KEY = "user_choice";

    private Spinner mSpinner;
    private int mSavedSpinnerSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(mMovies, this);

        movieAdapter.setOnReachLastPositionListener(new MovieAdapter.OnReachLastPosition() {
            @Override
            public void refreshPage(int page) {
                // mSpinner position should not be favorite so doesn't make api calls on favorites
                if(!userSortChoice.equals(getString(R.string.sorted_by_favorites_lable)) && NetworkUtils.isConnected(MainActivity.this)) {
                    String newPageUrlString = NetworkUtils.buildUrl(userSortChoice, MainActivity.this, page);
                    initLoader(newPageUrlString);
                }
            }
        });

        GridLayoutManager layoutManager;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = getANumSpanCountGridLayout(LANDSCAPE_SPAN_COUNT);
        } else {
            layoutManager = getANumSpanCountGridLayout(PORTRAIT_SPAN_COUNT);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(movieAdapter);

        if(savedInstanceState != null && savedInstanceState.containsKey(SORT_USER_CHOICE_BUNDLE_KEY)) {
            mSavedSpinnerSelectedPosition = savedInstanceState.getInt(SORT_USER_CHOICE_BUNDLE_KEY);
        } else {
            mSavedSpinnerSelectedPosition = 0;
        }

        if(!NetworkUtils.isConnected(MainActivity.this)) {
            showNoNetworkToast();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userSortChoice != null && userSortChoice.equals(getString(R.string.sorted_by_favorites_lable))) {
            mMovies.clear();
            String urlString = NetworkUtils.buildUrl(userSortChoice, MainActivity.this, 1);
            initLoader(urlString);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT_USER_CHOICE_BUNDLE_KEY, mSpinner.getSelectedItemPosition());
    }

    private void initLoader(String urlString) {
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(getString(R.string.api_url_key), urlString);

        getSupportLoaderManager().restartLoader(LOADER_ID, loaderBundle, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem spinnerItem = menu.findItem(R.id.sort_spinner);
        mSpinner = (Spinner) MenuItemCompat.getActionView(spinnerItem);

        String[] options = getResources().getStringArray(R.array.sort_options);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(MainActivity.this, options);


        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setSelection(mSavedSpinnerSelectedPosition);
        mSpinner.setOnItemSelectedListener(this);
        if(!NetworkUtils.isConnected(MainActivity.this)) {
            mSpinner.setSelection(3);
        }

        return true;
    }


    private GridLayoutManager getANumSpanCountGridLayout(int spanCount) {
        return new GridLayoutManager(MainActivity.this, spanCount);
    }


    public void showNoNetworkToast() {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(MainActivity.this, getString(R.string.network_error_message), Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(MainActivity.this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public List<Movie> loadInBackground() {
                Context context = MainActivity.this;

                if(userSortChoice.equals(getString(R.string.sorted_by_favorites_lable))) {
                    return ProviderUtils.getAllFavoriteMovies(MainActivity.this);
                } else {
                    String urlString = args.getString(getString(R.string.api_url_key));
                    URL apiUrl = NetworkUtils.getUrl(urlString);
                    String jsonResponse = NetworkUtils.getJsonResponse(apiUrl, context);
                    return JsonUtils.extractMoviesFromJson(jsonResponse, context);
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if(mMovies.isEmpty()) {
            mMovies.addAll(data);
        } else {
            addNewMovies(data);
        }
        movieAdapter.notifyDataSetChanged();

        if (mMovies.size() > 0) {
            hideEmptyListMessage();
        } else {
            if(userSortChoice.equals(getString(R.string.sorted_by_favorites_lable))){
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(MainActivity.this, R.string.no_favorite_movies_error, Toast.LENGTH_SHORT);
                mToast.show();
            }
            showEmptyListMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }

    @Override
    public void onClick(int position) {
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Movie selectedMovie = mMovies.get(position);

        detailIntent.putExtra(SELECTED_MOVIE, selectedMovie);
        startActivity(detailIntent);
    }

    public void showEmptyListMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mEmptyListMessageView.setVisibility(View.VISIBLE);
    }

    public void hideEmptyListMessage() {
        mEmptyListMessageView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(!NetworkUtils.isConnected(MainActivity.this) && position < 3) {
            showNoNetworkToast();
        }

        switch (position) {
            case 0:
                userSortChoice = getString(R.string.api_url_sorted_by_now_playing);
                break;

            case 1:
                userSortChoice = getString(R.string.api_url_sorted_by_popular);
                break;

            case 2:
                userSortChoice = getString(R.string.api_url_sorted_by_top_rated);
                break;

            case 3:
                userSortChoice = getString(R.string.sorted_by_favorites_lable);
                break;

        }
        String urlString = NetworkUtils.buildUrl(userSortChoice, MainActivity.this, 1);
        mMovies.clear();
        initLoader(urlString);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // Remove Duplicates when other pages requested more then the first one
    private void addNewMovies(List<Movie> newMovies) {
        for (Movie movie: newMovies) {
            for (int i=0 ; i<mMovies.size() ; i++) {
                if(movie.getmMovieId() == mMovies.get(i).getmMovieId()) {
                    break;
                }
                if (i == mMovies.size()-1) {
                    mMovies.add(movie);
                }
            }
        }
    }
}
