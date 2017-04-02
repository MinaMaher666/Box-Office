package com.example.mina.boxoffice;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mina.boxoffice.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.movie_title) TextView titleTextView;
    @BindView(R.id.release_date) TextView releaseDateTextView;
    @BindView(R.id.rate_text_view) TextView rateTextView;
    @BindView(R.id.plot) TextView plotTextView;
    @BindView(R.id.detail_poster) ImageView moviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie selectedMovie = getIntent().getParcelableExtra(MainActivity.SELECTED_MOVIE);

        ButterKnife.bind(this);

        titleTextView.setText(selectedMovie.getmTitle());
        releaseDateTextView.setText(dateFormatter(selectedMovie.getmReleaseDate().substring(0, 4)));
        rateTextView.setText(String.valueOf(selectedMovie.getmRate()));
        plotTextView.setText(selectedMovie.getmPlot());

        Context context = DetailActivity.this;
        Picasso.with(context)
                .load(NetworkUtils.buildPosterUrl(context, selectedMovie.getmPosterPath()))
                .error(ContextCompat.getDrawable(context, R.drawable.noposter))
                .into(moviePoster);
    }

    public   String dateFormatter(String date) {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return  ("(" + date + ")");
        } else {
            return date;
        }
    }
}
