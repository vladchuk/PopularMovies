package net.javango.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
import java.util.zip.Inflater;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private Context mContext;
    private List<Movie> mMovies;

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_movie_list, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public MovieHolder(View itemView) {
            super(itemView);
        }
    }
}
