package net.javango.popularmovies.model;

public class Video {

    private static final String YOUTUBE = "YouTube";

    private String key;
    private String site;

    public Video(String site, String key) {
        this.site = site;
        this.key = key;
    }

    public String getPath() {
        if (site.equals(YOUTUBE))
            return "http://www.youtube.com/watch?v=" + key;
        else
            throw new RuntimeException("Unknown site: " + site);

    }
}
