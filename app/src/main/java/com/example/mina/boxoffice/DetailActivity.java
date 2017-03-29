package com.example.mina.boxoffice;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mina.boxoffice.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie selectedMovie = getIntent().getParcelableExtra(MainActivity.SELECTED_MOVIE);

        TextView titleTextView = (TextView) findViewById(R.id.movie_title);
        ImageView moviePoster = (ImageView) findViewById(R.id.detail_poster);
        TextView releaseDateTextView = (TextView) findViewById(R.id.release_date);
        TextView rateTextView = (TextView) findViewById(R.id.rate_text_view);
        TextView plotTextView = (TextView) findViewById(R.id.plot);


        titleTextView.setText(selectedMovie.getmTitle());
        releaseDateTextView.setText(dateFormatter(selectedMovie.getmReleaseDate().substring(0, 4)));
        rateTextView.setText(String.valueOf(selectedMovie.getmRate()));
        plotTextView.setText(selectedMovie.getmPlot());

        Context context = DetailActivity.this;
        Picasso.with(context).load(NetworkUtils.buildPosterUrl(context, selectedMovie.getmPosterPath())).into(moviePoster);
    }

    public   String dateFormatter(String date) {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return  ("(" + date + ")");
        } else {
            return date;
        }
    }
}
