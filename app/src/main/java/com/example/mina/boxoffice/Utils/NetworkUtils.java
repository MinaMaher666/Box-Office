package com.example.mina.boxoffice.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.mina.boxoffice.Movie;
import com.example.mina.boxoffice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mina on 27/03/17.
 */

public class NetworkUtils {
    private NetworkUtils(){
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    public static String buildPosterUrl(Context context, String posterPath) {
        String posterBaseUrl = context.getString(R.string.poster_base_url);
        String posterSize = context.getString(R.string.poster_size);
        Uri posterUri = Uri.parse(posterBaseUrl)
                .buildUpon()
                .appendPath(posterSize)
                .build();
        return posterUri.toString() + posterPath;
    }


    public static String buildUrl(String userSortChoice, Context context) {
        Uri uri = Uri.parse(context.getString(R.string.api_url))
                .buildUpon()
                .appendPath(userSortChoice)
                .appendQueryParameter(context.getString(R.string.api_key_url_key),
                        context.getString(R.string.api_key_url_value))
                .build();
        return uri.toString();
    }
    public static URL getUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static InputStream getInputStream(URL url, Context context) {
        HttpURLConnection urlConnection;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(context.getString(R.string.url_request_method));
            inputStream = urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public static String getJsonResponse(URL url, Context context) {
        InputStream stream = getInputStream(url, context);
        if (stream == null)
            return null;

        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();

        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                builder.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static List<Movie> extractMoviesFromJson(String jsonResponce, Context context) {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponce);
            JSONArray results = root.getJSONArray(context.getString(R.string.json_key_results_array));

            int movieId;
            String movieTitle;
            String releaseDate;
            String posterPath;
            String moviePlot;
            double rate;

            for (int i=0 ; i<results.length() ; i++) {
                JSONObject movie = results.getJSONObject(i);
                movieId = movie.getInt(context.getString(R.string.json_key_movie_id));
                movieTitle = movie.getString(context.getString(R.string.josn_key_movie_title));
                releaseDate = movie.getString(context.getString(R.string.json_key_release_date));
                posterPath = movie.getString(context.getString(R.string.json_key_poster_path));
                moviePlot = movie.getString(context.getString(R.string.json_key_movie_plot));
                rate = movie.getDouble(context.getString(R.string.json_key_movie_rate));

                movies.add(new Movie(movieId, movieTitle, releaseDate, posterPath, moviePlot, rate));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

}
