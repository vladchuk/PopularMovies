package net.javango.popularmovies.model;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public Date fromLong(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long toLong(Date date) {
        return date == null ? null : date.getTime();
    }
}