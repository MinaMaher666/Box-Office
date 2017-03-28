package com.example.mina.boxoffice;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mina.boxoffice.Utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>, MovieAdapter.MovieOnClickListener{


    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int LOADER_ID = 666;
    public String userSortChoice;
    Toast mToast;
    private List<Movie> mMovies;
    private MovieAdapter movieAdapter;

    private View mEmptyListMessageView;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(mMovies, this);
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

        userSortChoice = getString(R.string.api_url_sorted_by_now_playing);

        initLoader();
    }

    private void initLoader() {
        Bundle loaderBundle = new Bundle();
        String urlString = NetworkUtils.buildUrl(userSortChoice, MainActivity.this);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.sorted_by_now_playing:
                userSortChoice = getString(R.string.api_url_sorted_by_now_playing);
                break;

            case R.id.sorted_by_popular:
                userSortChoice = getString(R.string.api_url_sorted_by_popular);
                break;

            case R.id.sorted_by_top_rated:
                userSortChoice = getString(R.string.api_url_sorted_by_top_rated);
                break;

            default:
                return false;
        }
        initLoader();
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
                return NetworkUtils.extractMoviesFromJson(jsonResponse, context);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mMovies.clear();
        mMovies.addAll(data);
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
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(MainActivity.this, mMovies.get(position).getmTitle(), Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showEmptyListMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mEmptyListMessageView.setVisibility(View.VISIBLE);
    }

    public void hideEmptyListMessage() {
        mEmptyListMessageView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
