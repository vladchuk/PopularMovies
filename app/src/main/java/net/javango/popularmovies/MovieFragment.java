package net.javango.popularmovies;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import net.javango.popularmovies.model.AppDatabase;
import net.javango.popularmovies.model.Movie;
import net.javango.popularmovies.model.MovieDao;
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
    private ViewGroup videoLayout;
    private ViewGroup reviewLayout;
    private ToggleButton favButton;

    public static MovieFragment newInstance(Movie movie, MovieContext movieContext) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        args.putParcelable(ARG_MOVIE_CONTEXT, movieContext);
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private MovieDao getMovieDao() {
        return AppDatabase.getDatabase(getContext()).movieDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        TextView title = view.findViewById(R.id.movie_title);
        ImageView poster = view.findViewById(R.id.poster_image);
        TextView rating = view.findViewById(R.id.movie_rating);
        TextView votes = view.findViewById(R.id.vote_count);
        TextView date = view.findViewById(R.id.movie_date);
        TextView synopsis = view.findViewById(R.id.movie_synopsis);
        TextView popularity = view.findViewById(R.id.popularity);
        videoLayout = view.findViewById(R.id.videos_list);
        reviewLayout = view.findViewById(R.id.reviews_list);

        movie = getArguments().getParcelable(ARG_MOVIE);
        title.setText(movie.getTitle());

        favButton = view.findViewById(R.id.favorite_button);
        Integer movieId = getMovieDao().getId(movie.getId());
        favButton.setChecked(movieId != null);
        favButton.setOnClickListener(v -> handleFavorite());

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
        if (movie.hasMetadata()) {
            populateVideos();
            populateReviews();
        } else {
            getLoaderManager().initLoader(METADATA_LOADER_ID, savedInstanceState, this);
        }
        return view;
    }

    private void populateVideos() {
        int ind = 0;
        for (Video video : movie.getVideos()) {
            ind++;
            View videoView = LayoutInflater.from(getContext()).inflate(R.layout.video_item, null);
            Button play = videoView.findViewById(R.id.play_button);
            play.setText("#" + ind);
            play.setOnClickListener(v -> {
                Uri uri = Uri.parse(video.getPath());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
            videoLayout.addView(videoView);
        }
    }

    private void populateReviews() {
        int ind = 0;
        for (Review review : movie.getReviews()) {
            ind++;
            View reviewView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, null);
            TextView headerView = reviewView.findViewById(R.id.review_header);
            String header = "#" + ind + " by " + review.getAuthor();
            headerView.setText(header);
            TextView contentView = reviewView.findViewById(R.id.review_content);
            contentView.setText(review.getContent());
            reviewLayout.addView(reviewView);
        }
    }

    void setTitle() {
        MovieContext movieContext = getArguments().getParcelable(ARG_MOVIE_CONTEXT);
        String movieName = MovieContext.getMovieName(getContext(), movieContext);
        getActivity().setTitle(movieName);
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
            if (!movie.hasMetadata()) {
                updateMovie(metadata);
                populateVideos();
                populateReviews();
            }
        } else {
            Toast.makeText(getActivity(), "Failed to fetch metadata!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie> loader) {
        // not implemented
    }

    private void handleFavorite() {
        new Thread(() -> {
            if (favButton.isChecked())
                getMovieDao().addMovie(movie);
            else
                getMovieDao().deleteMovie(movie);
        }).start();
    }
}
