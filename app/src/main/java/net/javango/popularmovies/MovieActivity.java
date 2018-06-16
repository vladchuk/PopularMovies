package net.javango.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import net.javango.popularmovies.model.Movie;
import net.javango.popularmovies.util.MovieContext;

public class MovieActivity extends SingleFragmentActivity {

    private static final String EXTRA_MOVIE = "net.javango.popularmovies.movie";
    private static final String EXTRA_MOVIE_CONTEXT = "net.javango.popularmovies.movieContext";

    @Override
    protected Fragment createFragment() {
        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        MovieContext movieContext = getIntent().getParcelableExtra(EXTRA_MOVIE_CONTEXT);
        return MovieFragment.newInstance(movie, movieContext);
    }

    public static Intent newIntent(Context context, Movie movie, MovieContext movieContext) {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        intent.putExtra(EXTRA_MOVIE_CONTEXT, movieContext);
        return intent;
    }

}