package com.plumya.pricefy.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.plumya.pricefy.data.local.model.Image;
import com.plumya.pricefy.data.local.model.WebsiteItem;

/**
 * Created by miltomasz on 19/07/18.
 */

@Database(entities = {Image.class, WebsiteItem.class}, version = 5)
@TypeConverters(Converters.class)
public abstract class PricefyDatabase extends RoomDatabase {

    private static final String LOG_TAG = PricefyDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "pricefy";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static PricefyDatabase instance;

    public static PricefyDatabase getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the database");
        if (instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        PricefyDatabase.class, PricefyDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
                Log.d(LOG_TAG, "Made new database");
            }
        }
        return instance;
    }

    // The associated DAOs for the database
    public abstract ImageDao imageDao();

    public abstract WebsiteItemDao websiteItemDao();
}
