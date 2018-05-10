package net.javango.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class JsonUtil {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String POSTER_PATH = "poster_path";
    public static final String SYNOPSIS = "overview";
    public static final String RATING = "vote_average";
    public static final String RELEASE_DATE = "release_date";
    public static final String RESULTS = "results";

    /**
     * Parses JSON object into a Movie
     * @param json contains a single {@code Movie}
     */
    public static Movie parseSandwichJson(String json) throws JSONException {
        Movie movie = new Movie();
        JSONObject obj = new JSONObject(json);
        movie.setId(obj.getString(ID));
        movie.setTitle((obj.getString(TITLE)));
        movie.setPosterPath(obj.getString(POSTER_PATH));
        movie.setSynopsis(obj.getString(SYNOPSIS));
        movie.setRating(obj.getString((RATING)));
        movie.setReleaseDate(obj.getString(RELEASE_DATE));
        return movie;
    }

    /**
     * Parses JSON object into a list of movie poster paths
     * @param json contains a page of movies
     */
    public static List<String> arrayToList(String json) throws JSONException {
        JSONObject page = new JSONObject(json);
        JSONArray movies = page.getJSONArray(RESULTS);
        List<String> list = new LinkedList<>();
        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            list.add(movie.getString(POSTER_PATH));
        }
        return list;
    }
}
