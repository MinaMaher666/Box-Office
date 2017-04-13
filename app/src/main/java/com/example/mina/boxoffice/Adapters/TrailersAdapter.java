package com.example.mina.boxoffice.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mina.boxoffice.Model.MovieTrailer;
import com.example.mina.boxoffice.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mina on 13/04/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {
    private  List<MovieTrailer> mTrailers;
    private TrailerOnCLickListener onCLickListener;

    public interface TrailerOnCLickListener {
        public void onClick(int position);
    }

    public TrailersAdapter(List<MovieTrailer> trailers, TrailerOnCLickListener listener) {
        this.mTrailers = trailers;
        this.onCLickListener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.trailer_title) TextView trailerTitleTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onCLickListener.onClick(position);
                }
            });
        }

        private void bind(int itemIndex) {
            MovieTrailer currentTrailer = mTrailers.get(itemIndex);
            trailerTitleTextView.setText(currentTrailer.getmName());
        }
    }
}
