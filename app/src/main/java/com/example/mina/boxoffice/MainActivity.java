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

import com.example.mina.boxoffice.Model.Movie;
import com.example.mina.boxoffice.Utils.JsonUtils;
import com.example.mina.boxoffice.Utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>, MovieAdapter.MovieOnClickListener,
        Spinner.OnItemSelectedListener{


    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int LOADER_ID = 666;
    public String userSortChoice;
    Toast mToast;
    private List<Movie> mMovies;
    private MovieAdapter movieAdapter;

    private View mEmptyListMessageView;
    private RecyclerView mRecyclerView;

    public static final String SELECTED_MOVIE = "selected_movie";
    public static final String SORT_USER_CHOICE_BUNDLE_KEY = "user_choice";

    private Spinner mSpinner;
    private int mSavedSpinnerSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(mMovies, this);

        movieAdapter.setOnReachLastPositionListener(new MovieAdapter.OnReachLastPosition() {
            @Override
            public void refreshPage(int page) {
                if(NetworkUtils.isConnected(MainActivity.this)) {
                    String newPageUrlString = NetworkUtils.buildUrl(userSortChoice, MainActivity.this, page);
                    initLoader(newPageUrlString);
                }
            }
        });

        GridLayoutManager layoutManager;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = getANumSpanCountGridLayout(3);
        } else {
            layoutManager = getANumSpanCountGridLayout(2);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        mEmptyListMessageView = findViewById(R.id.empty_list_message_text_view);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(movieAdapter);

        if(savedInstanceState != null && savedInstanceState.containsKey(SORT_USER_CHOICE_BUNDLE_KEY)) {
            mSavedSpinnerSelectedPosition = savedInstanceState.getInt(SORT_USER_CHOICE_BUNDLE_KEY);
        } else {
            mSavedSpinnerSelectedPosition = 0;
        }

        String urlString = NetworkUtils.buildUrl(userSortChoice, MainActivity.this, 1);
        initLoader(urlString);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT_USER_CHOICE_BUNDLE_KEY, mSpinner.getSelectedItemPosition());
    }

    private void initLoader(String urlString) {
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(getString(R.string.api_url_key), urlString);

        if(NetworkUtils.isConnected(MainActivity.this)) {
            getSupportLoaderManager().restartLoader(LOADER_ID, loaderBundle, this);
        } else {
            showNoNetworkToast();
        }
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

        showEmptyListMessage();
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
                String urlString = args.getString(getString(R.string.api_url_key));
                URL apiUrl = NetworkUtils.getUrl(urlString);
                String jsonResponse = NetworkUtils.getJsonResponse(apiUrl, context);
                return JsonUtils.extractMoviesFromJson(jsonResponse, context);
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
        }
        String urlString = NetworkUtils.buildUrl(userSortChoice, MainActivity.this, 1);
        mMovies.clear();
        initLoader(urlString);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

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
