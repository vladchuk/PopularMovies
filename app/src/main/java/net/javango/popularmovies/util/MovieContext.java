package net.javango.popularmovies.util;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import net.javango.popularmovies.R;

/**
 * Encapsulates external information about a movie, such as context this movie is used in, its position in
 * the list, etc.
 */
public class MovieContext implements Parcelable {

    public static final int MOST_POPULAR = 1;
    public static final int TOP_RATED = 2;
    public static final int FAVORITE = 3;

    // 0 - undefined
    private int contextId;
    private int position;

    public MovieContext(int contextId, int position) {
        this.contextId = contextId;
        this.position = position;
    }

    public int getId() {
        return contextId;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(contextId);
        dest.writeInt(position);
    }

    public static final Parcelable.Creator<MovieContext> CREATOR = new Parcelable.Creator<MovieContext>() {
        public MovieContext createFromParcel(Parcel in) {
            return new MovieContext(in);
        }

        public MovieContext[] newArray(int size) {
            return new MovieContext[size];
        }
    };

    private MovieContext(Parcel in) {
        contextId = in.readInt();
        position = in.readInt();
    }

    public static String getName(Context context, int moivieCotextId) {
            switch (moivieCotextId) {
                case MOST_POPULAR:
                    return context.getString(R.string.sort_popularity);
                case TOP_RATED:
                    return context.getString(R.string.sort_rating);
                case FAVORITE:
                    return context.getString(R.string.favorite_movies);
                default:
                    throw new IllegalArgumentException("Invalid movie context: " + moivieCotextId);
            }
    }
}
