package com.example.mina.boxoffice.Utils;

import android.content.Context;
import android.text.LoginFilter;
import android.util.Log;

import com.example.mina.boxoffice.Model.Movie;
import com.example.mina.boxoffice.Model.MovieReview;
import com.example.mina.boxoffice.Model.MovieTrailer;
import com.example.mina.boxoffice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mina on 05/04/17.
 */

public class JsonUtils {
    private JsonUtils() {
    }


    public static List<Movie> extractMoviesFromJson(String jsonResponse, Context context) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (jsonResponse == null || jsonResponse.length()==0) {
            return movies;
        }

        try {
            JSONObject root = new JSONObject(jsonResponse);
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
                movieTitle = movie.getString(context.getString(R.string.json_key_movie_title));
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

    public static List<MovieReview> extractReviewsFromJson(String jsonResponse, Context context) {
        ArrayList<MovieReview> reviews = new ArrayList<>();
        if (jsonResponse == null || jsonResponse.length()==0) {
            return reviews;
        }

        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray results = root.getJSONArray(context.getString(R.string.json_key_results_array));

            String author;
            String content;

            for (int i=0 ; i<results.length() ; i++) {
                JSONObject review = results.getJSONObject(i);
                author  = review.getString(context.getString(R.string.json_key_review_author));
                content = review.getString(context.getString(R.string.json_key_review_content));

                reviews.add(new MovieReview(author, content));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public static List<MovieTrailer> extractTrailersFromJson(String jsonResponse, Context context) {
        ArrayList<MovieTrailer> trailers = new ArrayList<>();
        if (jsonResponse == null || jsonResponse.length() == 0) {
            return trailers;
        }

        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray results = root.getJSONArray(context.getString(R.string.json_key_results_array));

            String trailerKey;
            String trailerSite;
            String trailerTitle;
            for(int i=0 ; i<results.length() ; i++) {
                JSONObject trailer = results.getJSONObject(i);
                trailerTitle = trailer.getString(context.getString(R.string.json_key_trailer_title));
                trailerKey = trailer.getString(context.getString(R.string.json_key_trailer_key));
                trailerSite = trailer.getString(context.getString(R.string.json_key_trailer_site));
                trailers.add(new MovieTrailer(trailerTitle, trailerKey, trailerSite));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }
}
