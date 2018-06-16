package net.javango.popularmovies.util;

import net.javango.popularmovies.model.Movie;
import net.javango.popularmovies.model.Review;
import net.javango.popularmovies.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class JsonUtil {

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String SYNOPSIS = "overview";
    private static final String RATING = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String RESULTS = "results";
    private static final String VOTE_COUNT = "vote_count";
    private static final String POPULARITY = "popularity";

    private static final String VIDEO_SITE = "site";
    private static final String VIDEO_KEY = "key";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";


    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Parses the full movie from JSON string
     * @param json
     * @return
     * @throws JSONException
     */
    private static Movie parseJsonMovie(String json) throws Exception {
        JSONObject jsMovie = new JSONObject(json);
        return parseJsonMovie(jsMovie, true);
    }

    /**
     * Parses JSON object into a Movie
     * @param jsMovie contains a single {@code Movie}
     * @param full if true, all/most movie fields will be parsed, else only minimal set (id, poster, etc.)
     */
    private static Movie parseJsonMovie(JSONObject jsMovie, boolean full) throws Exception {
        Movie movie = new Movie();
        movie.setId(jsMovie.getInt(ID));
        movie.setPosterPath(jsMovie.getString(POSTER_PATH));

        if (full) {
            movie.setTitle((jsMovie.getString(TITLE)));
            movie.setSynopsis(jsMovie.getString(SYNOPSIS));
            movie.setRating(jsMovie.getDouble((RATING)));
            Date date = dateFormat.parse(jsMovie.getString(RELEASE_DATE));
            movie.setReleaseDate(date);
            movie.setBackdropPath(jsMovie.getString(BACKDROP_PATH));
            movie.setVoteCount(jsMovie.getInt(VOTE_COUNT));
            movie.setPopularity(jsMovie.getDouble(POPULARITY));
        }
        return movie;
    }

    /**
     * Parses JSON object into a list of movie poster paths
     * @param json contains a page of movies
     */
    public static List<Movie> parseMovies(String json) throws Exception {
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

    /**
     * Parses JSON object into a list of movie videos
     * @param json contains a page of videos
     */
    public static List<Video> parseVideos(String json) throws Exception {
        JSONObject page = new JSONObject(json);
        JSONArray jsVideos = page.getJSONArray(RESULTS);
        List<Video> list = new LinkedList<>();
        for (int i = 0; i < jsVideos.length(); i++) {
            JSONObject jsVideo = jsVideos.getJSONObject(i);
            String site = jsVideo.getString(VIDEO_SITE);
            String key = jsVideo.getString(VIDEO_KEY);
            Video video = new Video(site, key);
            list.add(video);
        }
        return list;
    }

    /**
     * Parses JSON object into a list of movie reviews
     * @param json contains a page of reviews
     */
    public static List<Review> parseReviews(String json) throws Exception {
        JSONObject page = new JSONObject(json);
        JSONArray jsReviews = page.getJSONArray(RESULTS);
        List<Review> list = new LinkedList<>();
        for (int i = 0; i < jsReviews.length(); i++) {
            JSONObject jsReview = jsReviews.getJSONObject(i);
            String author = jsReview.getString(REVIEW_AUTHOR);
            String content = jsReview.getString(REVIEW_CONTENT);
            Review review = new Review(author, content);
            list.add(review);
        }
        return list;
    }

}


