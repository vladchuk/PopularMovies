package net.javango.popularmovies.util;

import android.net.Uri;

import net.javango.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetUtil {
    // read content in chunks of this size
    private static final int READ_SIZE = 4096;

    private static final String API_KEY = "api_key";

    private static final String BASE_MOVIE_URL = "https://api.themoviedb.org/3/movie";
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p";

    private static final String POSTER_SIZE = "/w185";
    private static final String BACKDROP_SIZE = "/w342";

    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String PAGE = "page";

    /**
     * Reads the contents of URL as String
     */
    public static String getContent(URL url) throws IOException {
        HttpURLConnection conn = null;
        InputStreamReader reader = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            reader = new InputStreamReader(conn.getInputStream(), "UTF-8");
            StringBuilder buffer = new StringBuilder();
            char[] chars = new char[READ_SIZE];
            int readCount;
            while ((readCount = reader.read(chars)) != -1) {
                buffer.append(chars, 0, readCount);
            }
            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
            if (conn != null)
                conn.disconnect();
        }
    }

    public static String getPosterUri(String imagePath) {
        return BASE_IMAGE_URL + POSTER_SIZE + imagePath;
    }

    public static String getBackdropUri(String imagePath) {
        return BASE_IMAGE_URL + BACKDROP_SIZE + imagePath;
    }

    private static URL getMovieUrl(String path, int page) throws MalformedURLException {
        Uri uri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                .appendQueryParameter(PAGE, String.valueOf(page))
                .build();

        URL url = new URL(uri.toString());
        return url;
    }

    /**
     * Returns URL for popular movies
     */
    public static URL getPopularUrl(int page) throws MalformedURLException {
        return getMovieUrl(POPULAR_PATH, page);
    }

    /**
     * Returns URL for top-rated movies
     */
    public static URL getTopRatedUrl(int page) throws MalformedURLException {
        return getMovieUrl(TOP_RATED_PATH, page);
    }

}
