package net.javango.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class MovieActivity extends SingleFragmentActivity {

    private static final String EXTRA_MOVIE = "net.javango.popularmovies.movie";

    @Override
    protected Fragment createFragment() {
        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        return MovieFragment.newInstance(movie);
    }

    public static Intent newIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        return intent;
    }

}