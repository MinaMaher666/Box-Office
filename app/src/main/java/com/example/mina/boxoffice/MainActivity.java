package com.example.mina.boxoffice;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mina.boxoffice.Utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>{


    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int LOADER_ID = 666;
    public String userSortChoice;
    Toast mToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        userSortChoice = getString(R.string.api_url_sorted_by_now_playing);

        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(getString(R.string.api_url_key), NetworkUtils.buildUrl(userSortChoice, MainActivity.this));

        if(NetworkUtils.isConnected(MainActivity.this)) {
            getSupportLoaderManager().initLoader(LOADER_ID, loaderBundle, this);
        } else {
            showNoNetworkToast();
        }
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
                String urlString = args.getString(getString(R.string.api_url_key));
                URL apiUrl = NetworkUtils.getUrl(urlString);
                String jsonResponse = NetworkUtils.getJsonResponse(apiUrl, context);
                return NetworkUtils.extractMoviesFromJson(jsonResponse, context);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }
}
