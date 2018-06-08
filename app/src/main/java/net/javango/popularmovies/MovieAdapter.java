package net.javango.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.javango.popularmovies.util.MovieContext;
import net.javango.popularmovies.util.NetUtil;

import java.util.LinkedList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private Context mContext;
    private List<Movie> mMovies;
    private int movieContext;

    public MovieAdapter(Context context) {
        mContext = context;
    }

    /**
     * Set the data and notify observers
     */
    public void setData(List<Movie> data, int movieContext) {
        mMovies = data;
        this.movieContext = movieContext;
        notifyDataSetChanged();
    }

    public void appendData(List<Movie> data) {
        if (mMovies == null)
            mMovies = new LinkedList<>();
        mMovies.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_movie, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Movie movie = mMovies.get(position);
        holder.mMovie = movie;
        holder.position = position + 1;
        String posterUri = NetUtil.getPosterUri(movie.getPosterPath());
        Picasso.with(mContext).
                load(posterUri).
                placeholder(R.drawable.poster_placeholder).
                into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMovies != null ? mMovies.size() : 0;
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;
        private Movie mMovie;
        private int position;

        public MovieHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MovieContext cxt = new MovieContext(movieContext, position);
            Intent intent = MovieActivity.newIntent(mContext, mMovie, cxt);
            mContext.startActivity(intent);
        }
    }
}
