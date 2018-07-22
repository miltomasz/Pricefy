package com.plumya.pricefy.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.plumya.pricefy.data.local.ImageDao;
import com.plumya.pricefy.data.local.model.Image;
import com.plumya.pricefy.utils.AppExecutors;

import java.util.List;

/**
 * Created by miltomasz on 20/07/18.
 */

public class PricefyRepository {

    private static final String LOG_TAG = PricefyRepository.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static PricefyRepository instance;

    private final ImageDao imageDao;
    private final AppExecutors executors;

    private boolean initialized = false;

    private PricefyRepository(ImageDao imageDao, AppExecutors executors) {
        this.imageDao = imageDao;
        this.executors = executors;
    }

    public synchronized static PricefyRepository getInstance(ImageDao imageDao, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (instance == null) {
            synchronized (LOCK) {
                instance = new PricefyRepository(imageDao, executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return instance;
    }

    public Image insertImage(final Image image) {
        executors.diskIO().execute(() -> {
            long imageId = imageDao.insertImage(image);
            image.setId(imageId);
        });
        return image;
    }

    public LiveData<List<Image>> getImages() {
        return imageDao.getAllImages();
    }

    public interface InsertCallback {
        long returningId(long id);
    }
}
