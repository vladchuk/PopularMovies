package net.javango.popularmovies.util;

import net.javango.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class JsonUtil {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String POSTER_PATH = "poster_path";
    public static final String BACKDROP_PATH = "backdrop_path";
    public static final String SYNOPSIS = "overview";
    public static final String RATING = "vote_average";
    public static final String RELEASE_DATE = "release_date";
    public static final String RESULTS = "results";
    public static final String VOTE_COUNT = "vote_count";

    /**
     * Parses the full movie from JSON string
     * @param json
     * @return
     * @throws JSONException
     */
    public static Movie parseJsonMovie(String json) throws JSONException {
        JSONObject jsMovie = new JSONObject(json);
        return parseJsonMovie(jsMovie, true);
    }

    /**
     * Parses JSON object into a Movie
     * @param jsMovie contains a single {@code Movie}
     * @param full if true, all/most movie fields will be parsed, else only minimal set (id, poster, etc.)
     */
    public static Movie parseJsonMovie(JSONObject jsMovie, boolean full) throws JSONException {
        Movie movie = new Movie();
        movie.setId(jsMovie.getString(ID));
        movie.setPosterPath(jsMovie.getString(POSTER_PATH));

        if (full) {
            movie.setTitle((jsMovie.getString(TITLE)));
            movie.setSynopsis(jsMovie.getString(SYNOPSIS));
            movie.setRating(jsMovie.getString((RATING)));
            movie.setReleaseDate(jsMovie.getString(RELEASE_DATE));
            movie.setBackdropPath(jsMovie.getString(BACKDROP_PATH));
            movie.setVoteCount(jsMovie.getString(VOTE_COUNT));
        }
        return movie;
    }

    /**
     * Parses JSON object into a list of movie poster paths
     * @param json contains a page of movies
     */
    public static List<Movie> parseMovies(String json) throws JSONException {
        JSONObject page = new JSONObject(json);
        JSONArray jsMovies = page.getJSONArray(RESULTS);
        List<Movie> list = new LinkedList<>();
        for (int i = 0; i < jsMovies.length(); i++) {
            JSONObject jsMovie = jsMovies.getJSONObject(i);
            Movie movie = parseJsonMovie(jsMovie, true);
            list.add(movie);
        }
        return list;
    }
}
