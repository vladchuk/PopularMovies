package net.javango.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.javango.popularmovies.model.Movie;
import net.javango.popularmovies.util.MovieContext;

import java.util.List;
import java.util.UUID;

public class MoviePagerActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIE = "net.javango.popularmovies.movie";
    private static final String EXTRA_MOVIE_CONTEXT = "net.javango.popularmovies.movieContext";

    private ViewPager viewPager;
    private List<Movie> movies;

    public static Intent newIntent(Context context, Movie movie, MovieContext movieContext) {
        Intent intent = new Intent(context, MoviePagerActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        intent.putExtra(EXTRA_MOVIE_CONTEXT, movieContext);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_pager);

        MovieContext movieContext = getIntent().getParcelableExtra(EXTRA_MOVIE_CONTEXT);

        viewPager = (ViewPager) findViewById(R.id.movie_view_pager);
        movies = MovieAdapter.getmMovies();
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Movie movie = movies.get(position);
                return MovieFragment.newInstance(movie, movieContext);
            }

            @Override
            public int getCount() {
                return movies.size();
            }
        });
        viewPager.setCurrentItem(movieContext.getPosition());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                movieContext.setPosition(position);
                String title = MovieContext.getMovieName(getBaseContext(), movieContext);
                getSupportActionBar().setTitle(title);
            }
        });
    }

}
