package net.javango.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.javango.popularmovies.util.JsonUtil;
import net.javango.popularmovies.util.NetUtil;

import java.net.URL;
import java.util.List;

public class MovieListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = MovieListActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 13;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private static int mSortState = R.id.sort_popularity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mRecyclerView = view.findViewById(R.id.movie_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mMovieAdapter = new MovieAdapter(getActivity());
        mRecyclerView.setAdapter(mMovieAdapter);
        getActivity().setTitle(R.string.app_name);

        // enable menu
        setHasOptionsMenu(true);

        // start
        getLoaderManager().initLoader(MOVIE_LOADER_ID, savedInstanceState, this);
        return view;
    }

    static class MovieLoader extends AsyncTaskLoader<List<Movie>> {

        private List<Movie> movies;
        private MovieListFragment mFragment;

        public MovieLoader(MovieListFragment fragment) {
            super(fragment.getContext());
            mFragment = fragment;
        }

        @Override
        protected void onStartLoading() {
            if (movies != null) {
                deliverResult(movies);
            } else {
                forceLoad();
            }
        }

        @Override
        public List<Movie> loadInBackground() {
            try {
                URL url = mSortState == R.id.sort_popularity ? NetUtil.getPopularUrl() : NetUtil.getTopRatedUrl();
                String json = NetUtil.getContent(url);
                List<Movie> movies = JsonUtil.parseMovies(json);
                return movies;
            } catch (Exception e) {
                Log.e(TAG, "Failed to load movies!", e);
                return null;
            }
        }

        @Override
        public void deliverResult(@Nullable List<Movie> data) {
            movies = data;
            super.deliverResult(data);
        }
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MovieLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        mMovieAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        // not implemented
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movies, menu);
        menu.findItem(mSortState).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mSortState = id;
        item.setChecked(true);

        mMovieAdapter.setData(null);
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

        return super.onOptionsItemSelected(item);
    }
}