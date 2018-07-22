package com.plumya.pricefy.data.local;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by miltomasz on 19/07/18.
 */

public class Converters {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
