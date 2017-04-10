package com.example.mina.boxoffice.Model;

/**
 * Created by mina on 05/04/17.
 */

public class MovieTrailer {
    private String mName;
    private String mKey;
    private String mSite;

    public MovieTrailer(String mName, String mKey, String mSite) {
        this.mName = mName;
        this.mKey = mKey;
        this.mSite = mSite;
    }

    public String getmName() {
        return mName;
    }

    public String getmKey() {
        return mKey;
    }

    public String getmSite() {
        return mSite;
    }
}
