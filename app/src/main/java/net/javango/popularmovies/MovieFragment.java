package net.javango.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.javango.popularmovies.util.NetUtil;

import java.text.SimpleDateFormat;

public class MovieFragment extends Fragment {

    private static final String EXTRA_MOVIE = "net.javango.popularmovies.movie";

    public static Intent newIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        return intent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        TextView title = view.findViewById(R.id.movie_title);
        ImageView backdrop = view.findViewById(R.id.backdrop_image);
        ImageView poster = view.findViewById(R.id.poster_image);
        TextView rating = view.findViewById(R.id.movie_rating);
        TextView votes = view.findViewById(R.id.vote_count);
        TextView date = view.findViewById(R.id.movie_date);
        TextView synopsis = view.findViewById(R.id.movie_synopsis);
        TextView popularity = view.findViewById(R.id.popularity);

        Movie movie = (Movie) getActivity().getIntent().getParcelableExtra(EXTRA_MOVIE);
        title.setText(movie.getTitle());

        String backdropUri = NetUtil.getBackdropUri(movie.getBackdropPath());
        Picasso.with(getContext()).
                load(backdropUri).
                into(backdrop);
        String posterUri = NetUtil.getBackdropUri(movie.getPosterPath());
        Picasso.with(getContext()).
                load(posterUri).
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

        return view;
    }
}
