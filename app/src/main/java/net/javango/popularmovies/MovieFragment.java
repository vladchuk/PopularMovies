package net.javango.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.javango.popularmovies.model.Movie;
import net.javango.popularmovies.model.Review;
import net.javango.popularmovies.model.Video;
import net.javango.popularmovies.util.JsonUtil;
import net.javango.popularmovies.util.MovieContext;
import net.javango.popularmovies.util.NetUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Movie> {

    private static final String TAG = MovieListActivity.class.getSimpleName();

    private static final String ARG_MOVIE = "movie";
    private static final String ARG_MOVIE_CONTEXT = "movieContext";
    private static final int METADATA_LOADER_ID = 14;

    private Movie movie;

    public static MovieFragment newInstance(Movie movie, MovieContext movieContext) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        args.putParcelable(ARG_MOVIE_CONTEXT, movieContext);
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        TextView title = view.findViewById(R.id.movie_title);
//        ImageView backdrop = view.findViewById(R.id.backdrop_image);
        ImageView poster = view.findViewById(R.id.poster_image);
        TextView rating = view.findViewById(R.id.movie_rating);
        TextView votes = view.findViewById(R.id.vote_count);
        TextView date = view.findViewById(R.id.movie_date);
        TextView synopsis = view.findViewById(R.id.movie_synopsis);
        TextView popularity = view.findViewById(R.id.popularity);

        movie = getArguments().getParcelable(ARG_MOVIE);
        title.setText(movie.getTitle());

        String backdropUri = NetUtil.getBackdropUri(movie.getBackdropPath());
//        Picasso.with(getContext()).
//                load(backdropUri).
//                into(backdrop);
        String posterUri = NetUtil.getBackdropUri(movie.getPosterPath());
        Picasso.with(getContext()).
                load(posterUri).
                placeholder(R.drawable.poster_placeholder).
                into(poster);

        String ratingTxt = Double.toString(movie.getRating());
        rating.setText(ratingTxt);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        String dateTxt = dateFormat.format(movie.getReleaseDate());
        date.setText(dateTxt);
        String votesTxt = Integer.toString(movie.getVoteCount());
        votes.setText(votesTxt);
        synopsis.setText(movie.getSynopsis());
        String popularityTtx = String.format("%.0f", movie.getPopularity());
        popularity.setText(popularityTtx);

        setTitle();
        getLoaderManager().initLoader(METADATA_LOADER_ID, savedInstanceState, this);
        return view;
    }

    private void setTitle() {
        MovieContext movieContext = getArguments().getParcelable(ARG_MOVIE_CONTEXT);
        String base = getString(movieContext.getContext() == MovieContext.MOST_POPULAR ? R.string.sort_popularity : R.string.sort_rating);
        getActivity().setTitle(base + " #" + movieContext.getPosition());
    }

    private void updateMovie(Movie metadata) {
        // update master with metadata
        movie.setVideos(metadata.getVideos());
        movie.setReviews(metadata.getReviews());
    }

    static class MovieLoader extends AsyncTaskLoader<Movie> {

        private Movie movie;
        private MovieFragment mFragment;

        public MovieLoader(MovieFragment fragment) {
            super(fragment.getContext());
            mFragment = fragment;
        }

        @Override
        protected void onStartLoading() {
            if (movie != null) {
                deliverResult(movie);
            } else {
                forceLoad();
            }
        }

        @Override
        public Movie loadInBackground() {
            try {
                int movieId = mFragment.movie.getId();
                Movie metadata = new Movie();

                // load videos
                URL url = NetUtil.getVideosUrl(movieId);
                String json = NetUtil.getContent(url);
                List<Video> videos = JsonUtil.parseVideos(json);
                metadata.setVideos(videos);

                // load reviews
                url = NetUtil.getReviewsUrl(movieId);
                json = NetUtil.getContent(url);
                List<Review> reviews = JsonUtil.parseReviews(json);
                metadata.setReviews(reviews);

                return metadata;
            } catch (Exception e) {
                Log.e(TAG, "Failed to load movie metadata:", e);
                return null;
            }
        }

        @Override
        public void deliverResult(@Nullable Movie metadata) {
            movie = metadata;
            super.deliverResult(metadata);
        }
    }

    @NonNull
    @Override
    public Loader<Movie> onCreateLoader(int id, @Nullable Bundle args) {
        return new MovieFragment.MovieLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie> loader, Movie metadata) {
        if (metadata != null) {
            updateMovie(metadata);
        }
        else {
            Toast.makeText(getActivity(), "Failed to update movie!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie> loader) {
        // not implemented
    }
}
