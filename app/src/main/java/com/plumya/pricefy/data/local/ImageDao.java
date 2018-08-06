package com.plumya.pricefy.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.plumya.pricefy.data.local.model.Image;

import java.util.List;

/**
 * Created by miltomasz on 20/07/18.
 */

@Dao
public interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertImage(Image image);

    @Query("SELECT * FROM images ORDER BY captureDate DESC")
    LiveData<List<Image>> getAllImages();

    @Query("SELECT * FROM images ORDER BY captureDate DESC")
    List<Image> getImagesForWidget();
}
