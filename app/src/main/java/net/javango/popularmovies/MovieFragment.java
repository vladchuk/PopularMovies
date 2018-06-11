package net.javango.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.javango.popularmovies.util.MovieContext;
import net.javango.popularmovies.util.NetUtil;

import java.text.SimpleDateFormat;

public class MovieFragment extends Fragment {

    private static final String ARG_MOVIE = "movie";
    private static final String ARG_MOVIE_CONTEXT = "movieContext";

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

        Movie movie = getArguments().getParcelable(ARG_MOVIE);
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
        return view;
    }

    private void setTitle() {
        MovieContext movieContext = getArguments().getParcelable(ARG_MOVIE_CONTEXT);
        String base = getString(movieContext.getContext() == MovieContext.MOST_POPULAR ? R.string.sort_popularity : R.string.sort_rating);
        getActivity().setTitle(base + " #" + movieContext.getPosition());
    }
}
