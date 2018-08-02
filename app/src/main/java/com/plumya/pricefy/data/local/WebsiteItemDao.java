package com.plumya.pricefy.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.plumya.pricefy.data.local.model.WebsiteItem;

import java.util.List;

/**
 * Dao interface for website items db operations
 */

@Dao
public interface WebsiteItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(WebsiteItem... websiteItem);

    @Query("SELECT * FROM website_items WHERE imageId=:imageId ORDER BY priceFrom ASC")
    LiveData<List<WebsiteItem>> getWebsiteItems(long imageId);

    @Query("SELECT COUNT(id) FROM website_items WHERE imageId=:imageId")
    int countAllWebsiteItems(long imageId);

    @Query("SELECT detailsLoaded FROM website_items WHERE id=:id")
    boolean websiteItemDetailsLoaded(long id);

    @Query("UPDATE website_items SET reviews=:reviews, fit=:fit, size=:size, color=:color, " +
            "features=:features, detailsLoaded=:detailsLoaded WHERE id=:id")
    void updateWebsiteIem(long id, String reviews, String fit, String size, String color,
                          String features, boolean detailsLoaded);

    @Query("SELECT * FROM website_items WHERE id=:itemId")
    LiveData<WebsiteItem> getWebsiteItem(long itemId);
}
