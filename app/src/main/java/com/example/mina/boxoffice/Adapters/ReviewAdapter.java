package com.example.mina.boxoffice.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mina.boxoffice.Model.MovieReview;
import com.example.mina.boxoffice.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mina on 10/04/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<MovieReview> mReviews;

    public ReviewAdapter(List<MovieReview> reviews) {
        this.mReviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_item, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_author) TextView reviewAuthorTextView;
        @BindView(R.id.review) TextView reviewTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int itemIndex) {
            MovieReview currentReview = mReviews.get(itemIndex);
            reviewAuthorTextView.setText(currentReview.getmAuthor());
            reviewTextView.setText(currentReview.getmContent());
        }
    }
}
