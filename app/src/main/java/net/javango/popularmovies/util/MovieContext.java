package net.javango.popularmovies.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Encapsulates external information about a movie, such as context this movie is used in, its position in
 * the list, etc.
 */
public class MovieContext implements Parcelable {

    public static final int MOST_POPULAR = 1;
    public static final int TOP_RATED = 2;
    public static final int FAVORITE = 3;

    // 0 - undefined
    private int context;
    private int position;

    public MovieContext(int context, int position) {
        this.context = context;
        this.position = position;
    }

    public int getContext() {
        return context;
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
        dest.writeInt(context);
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
        context = in.readInt();
        position = in.readInt();
    }
}
