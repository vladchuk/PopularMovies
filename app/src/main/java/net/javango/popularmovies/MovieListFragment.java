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
import android.widget.ProgressBar;

import net.javango.popularmovies.util.JsonUtil;
import net.javango.popularmovies.util.MovieContext;
import net.javango.popularmovies.util.NetUtil;

import java.net.URL;
import java.util.List;

public class MovieListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = MovieListActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 13;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private GridLayoutManager layoutManager;

    private static int mSortState = R.id.sort_popularity;


    //PaginationAdapter adapter;
    private ProgressBar progressBar;
    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;
    // If current page the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;
    // total no. of pages to load. Initial load is page 0, after which 2 more pages will load.
    private int TOTAL_PAGES = 100;
    // indicates the current page which Pagination is fetching.
    private int currentPage = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mRecyclerView = view.findViewById(R.id.movie_recycler_view);
        layoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(layoutManager);

        mMovieAdapter = new MovieAdapter(getActivity());
        mRecyclerView.setAdapter(mMovieAdapter);
        RecyclerView.OnScrollListener scrollListener = new MovieScrollListener(layoutManager);
        mRecyclerView.addOnScrollListener(scrollListener);
        getActivity().setTitle(R.string.app_name);

        // enable menu
        setHasOptionsMenu(true);

        // start
        getLoaderManager().initLoader(MOVIE_LOADER_ID, savedInstanceState, this);
        return view;
    }

    private void setTitle() {
        String title = mSortState == R.id.sort_popularity ? getString(R.string.sort_popularity) :
                getString(R.string.sort_rating);
        title += "page " + currentPage + " of " + TOTAL_PAGES;
        getActivity().setTitle(title);
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
                URL url = mSortState == R.id.sort_popularity ? NetUtil.getPopularUrl(mFragment.currentPage) : NetUtil
                        .getTopRatedUrl(mFragment.currentPage);
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
        mMovieAdapter.appendData(data);
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
        int movieContext = 0;
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_popularity:
                movieContext = MovieContext.MOST_POPULAR;
                break;
            case R.id.sort_rating:
                movieContext = MovieContext.TOP_RATED;
                break;
        }
        item.setChecked(true);

        currentPage = 1;
        mMovieAdapter.setData(null, movieContext);
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

        return super.onOptionsItemSelected(item);
    }

    private class MovieScrollListener extends PaginationScrollListener {

        public MovieScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        protected void loadMoreItems() {
            isLoading = true;
            currentPage += 1; //Increment page index to load the next one
            loadNextPage();
            setTitle();
        }

        @Override
        public int getTotalPageCount() {
            return TOTAL_PAGES;
        }

        @Override
        public boolean isLastPage() {
            return isLastPage;
        }

        @Override
        public boolean isLoading() {
            return isLoading;
        }
    }

    private void loadNextPage() {
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
//        adapter.removeLoadingFooter();  // 2
        isLoading = false;   // 3

//        adapter.addAll(movies);   // 4

        if (currentPage != TOTAL_PAGES) ;// adapter.addLoadingFooter();  // 5
        else isLastPage = true;
    }
}