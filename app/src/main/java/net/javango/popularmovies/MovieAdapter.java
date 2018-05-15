package net.javango.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.javango.popularmovies.util.NetUtil;

import java.net.MalformedURLException;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private Context mContext;
    private List<Movie> mMovies;

    public MovieAdapter(Context context) {
        mContext = context;
    }

    /**
     * Set the data and notify observers
     */
    public void setData(List<Movie> data) {
        mMovies = data;
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
        String posterUri = NetUtil.getPosterUri(movie.getPosterPath());
        Picasso.with(mContext).
                load(posterUri).
                into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMovies != null ? mMovies.size() : 0;
    }

    class MovieHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private Movie mMovie;

        public MovieHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.movie_poster);
        }
    }
}
