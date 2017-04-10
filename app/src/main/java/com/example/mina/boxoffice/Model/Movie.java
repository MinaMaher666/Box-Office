package com.example.mina.boxoffice.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mina on 27/03/17.
 */

public class Movie implements Parcelable{
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

    protected Movie(Parcel in) {
        mMovieId = in.readInt();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mPlot = in.readString();
        mRate = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mMovieId);
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mPosterPath);
        dest.writeString(mPlot);
        dest.writeDouble(mRate);
    }
}
