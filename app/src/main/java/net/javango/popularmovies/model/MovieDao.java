package net.javango.popularmovies.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Insert
    void addMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("select * from movie order by id")
    List<Movie> fetchAll();

    @Query("select id from movie where id = :id")
    Integer getId(int id);

    @Query("select count(*) from movie")
    int getCount();
}
