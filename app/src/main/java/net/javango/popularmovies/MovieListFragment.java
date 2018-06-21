package net.javango.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.javango.popularmovies.model.AppDatabase;
import net.javango.popularmovies.model.Movie;
import net.javango.popularmovies.util.JsonUtil;
import net.javango.popularmovies.util.MovieContext;
import net.javango.popularmovies.util.NetUtil;

import java.net.URL;
import java.util.List;

public class MovieListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String ARG_CAN_LOAD = "canLoad";
    private static final String TAG = MovieListActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 13;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private GridLayoutManager layoutManager;
    private RecyclerView.OnScrollListener scrollListener;

    private static int movieContextId = MovieContext.MOST_POPULAR;

    // if page is currently loading
    private static volatile boolean isLoading;
    // total number of pages
    private static int TOTAL_PAGES = 5;
    // indicates the current page being loaded
    private static volatile int currentPage = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mRecyclerView = view.findViewById(R.id.movie_recycler_view);
        layoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(layoutManager);

        mMovieAdapter = new MovieAdapter(getActivity(), movieContextId);
//        mMovieAdapter.setData(null, movieContextId);
        mRecyclerView.setAdapter(mMovieAdapter);
        scrollListener = new MovieScrollListener(layoutManager);

        // enable menu
        setHasOptionsMenu(true);

        if (!isFavorites()) {
            mRecyclerView.addOnScrollListener(scrollListener);
            if (currentPage == 1)
                getLoaderManager().initLoader(MOVIE_LOADER_ID, savedInstanceState, this);
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(MOVIE_LOADER_ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isFavorites()) {
            new Thread(() -> {
                int movieCount = AppDatabase.getDatabase(getContext()).movieDao().getCount();
                if (mMovieAdapter.getItemCount() != movieCount) {
                    List<Movie> movies = AppDatabase.getDatabase(getContext()).movieDao().fetchAll();
                    getActivity().runOnUiThread(() -> {
                        mMovieAdapter.setData(movies, movieContextId);
                        mMovieAdapter.notifyDataSetChanged();
                    });
                }
            }).start();
        }
    }

    private void restartLoader(boolean canLoad) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_CAN_LOAD, canLoad);
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, bundle, MovieListFragment.this);
    }

    static class MovieLoader extends AsyncTaskLoader<List<Movie>> {

        private List<Movie> movies;
        private MovieListFragment mFragment;
        private boolean canLoad;

        public MovieLoader(MovieListFragment fragment, boolean canLoad) {
            super(fragment.getContext());
            mFragment = fragment;
            this.canLoad = canLoad;
        }

        @Override
        protected void onStartLoading() {
            if (!canLoad)
                return;

            if (movies != null) {
                deliverResult(movies);
            } else {
                forceLoad();
            }
        }

        @Override
        public List<Movie> loadInBackground() {
            try {
                int page = mFragment.currentPage;
                URL url = movieContextId == MovieContext.MOST_POPULAR ?
                        NetUtil.getPopularUrl(page) : NetUtil.getTopRatedUrl(page);
                String json = NetUtil.getContent(url);
                List<Movie> movies = JsonUtil.parseMovies(json);
                return movies;
            } catch (Exception e) {
                Log.e(TAG, "Failed to load movies:", e);
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
        boolean canLoad = args == null || args.getBoolean(ARG_CAN_LOAD);
        return new MovieLoader(this, canLoad);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        isLoading = false;
        if (data != null) {
            mMovieAdapter.appendData(data);
        } else
            Toast.makeText(getActivity(), "Failed to load movies!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        // not used
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movies, menu);
        int id;
        switch (movieContextId) {
            case MovieContext.MOST_POPULAR:
                id = R.id.sort_popularity;
                break;
            case MovieContext.TOP_RATED:
                id = R.id.sort_rating;
                break;
            case MovieContext.FAVORITE:
                id = R.id.favorite_movies;
                break;
            default:
                throw new IllegalArgumentException("No such context: " + movieContextId);
        }
        menu.findItem(id).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_popularity:
                movieContextId = MovieContext.MOST_POPULAR;
                break;
            case R.id.sort_rating:
                movieContextId = MovieContext.TOP_RATED;
                break;
            case R.id.favorite_movies:
                movieContextId = MovieContext.FAVORITE;
                break;
            default:
                throw new IllegalArgumentException("Invalid id: " + id);
        }
        String title = MovieContext.getName(getContext(), movieContextId);
        getActivity().setTitle(title);

        item.setChecked(true);
        handleContextSwitch();

        return super.onOptionsItemSelected(item);
    }

    private void handleContextSwitch() {
        if (isFavorites()) {
            mRecyclerView.removeOnScrollListener(scrollListener);
            new Thread(() -> {
                List<Movie> movies = AppDatabase.getDatabase(getContext()).movieDao().fetchAll();
                getActivity().runOnUiThread(() -> mMovieAdapter.setData(movies, movieContextId));
            }).start();
        } else {
            currentPage = 1;
            mMovieAdapter.setData(null, movieContextId);
            mRecyclerView.addOnScrollListener(scrollListener);
            restartLoader(true);
        }
    }

    private boolean isLastPage() {
        return currentPage >= TOTAL_PAGES;
    }

    private boolean isFavorites() {
        return movieContextId == MovieContext.FAVORITE;
    }

    private class MovieScrollListener extends PaginationScrollListener {

        public MovieScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        protected void loadMoreItems() {
            isLoading = true;
            currentPage += 1; //Increment page index to load the next one
            restartLoader(true);
        }

        @Override
        public boolean isLastPage() {
            Log.i(TAG, "Current page: " + currentPage);
            return MovieListFragment.this.isLastPage();
        }

        @Override
        public boolean isLoading() {
            if (isLoading)
                Log.i(TAG, "Loading.......................");
            return isLoading;
        }
    }
}