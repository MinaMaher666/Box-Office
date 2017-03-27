package com.example.mina.boxoffice;

/**
 * Created by mina on 27/03/17.
 */

public class Movie {
    private int mMovieId;
    private String mTitle;
    private String mReleaseDate;
    private String mPosterPath;
    private String mPlot;
    private double mRate;

    public Movie(int mId, String mTitle, String mReleaseDate, String mPosterPath, String mPlot, double mRate) {
        this.mMovieId = mId;
        this.mTitle = mTitle;
        this.mReleaseDate = mReleaseDate;
        this.mPosterPath = mPosterPath;
        this.mPlot = mPlot;
        this.mRate = mRate;
    }

    public int getmMovieId() {
        return mMovieId;
    }
    public String getmTitle() {
        return mTitle;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public String getmPlot() {
        return mPlot;
    }

    public double getmRate() {
        return mRate;
    }
}
