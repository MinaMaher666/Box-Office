package com.example.mina.boxoffice.Model;

/**
 * Created by mina on 05/04/17.
 */

public class MovieReview {
    private String mAuthor;
    private String mContent;

    public MovieReview(String mAuthor, String mContent) {
        this.mAuthor = mAuthor;
        this.mContent = mContent;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmContent() {
        return mContent;
    }
}
