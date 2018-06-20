package net.javango.popularmovies.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Movie.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String NAME = "MOVIE_DB";
    private static AppDatabase DB;

    public abstract MovieDao movieModel();

    public static AppDatabase getDatabase(Context context) {
        if (DB == null) {
            DB = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return DB;
    }

}
