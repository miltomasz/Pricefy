package com.plumya.pricefy.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.util.Log;

import com.plumya.pricefy.data.local.ImageDao;
import com.plumya.pricefy.data.local.WebsiteItemDao;
import com.plumya.pricefy.data.local.model.Image;
import com.plumya.pricefy.data.local.model.WebsiteItem;
import com.plumya.pricefy.data.network.AmazonNetworkDaraSource;
import com.plumya.pricefy.data.network.model.WebsiteItemModel;
import com.plumya.pricefy.ui.results.SingleLiveEvent;
import com.plumya.pricefy.utils.AppExecutors;
import com.plumya.pricefy.utils.WebsiteItemsDiffCallback;

import java.util.List;

/**
 * Created by miltomasz on 20/07/18.
 */

public class PricefyRepository {

    private static final String LOG_TAG = PricefyRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static PricefyRepository instance;

    // Collaborators
    private final ImageDao imageDao;
    private final WebsiteItemDao websiteItemDao;
    private final AmazonNetworkDaraSource networkDataSource;
    private final AppExecutors executors;
    private boolean initialized;

    // Live data
    private LiveData<List<WebsiteItem>> websiteItems;
    private LiveData<WebsiteItem> websiteItem;
    private SingleLiveEvent<WebsiteItemModel> websiteItemsErrors = new SingleLiveEvent<>();

    private PricefyRepository(ImageDao imageDao, WebsiteItemDao websiteItemDao,
                              AmazonNetworkDaraSource networkDataSource, AppExecutors executors) {
        this.imageDao = imageDao;
        this.websiteItemDao = websiteItemDao;
        this.networkDataSource = networkDataSource;
        this.executors = executors;

        LiveData<WebsiteItemModel> networkItemList = networkDataSource.getWebsiteItems();
        networkItemList.observeForever(new ItemListObserver());
        LiveData<WebsiteItem> networkItemDetails = networkDataSource.getWebsiteItem();
        networkItemDetails.observeForever(new Observer<WebsiteItem>() {
            @Override
            public void onChanged(@Nullable WebsiteItem websiteItem) {
                executors.diskIO().execute(() -> {
                    websiteItemDao.updateWebsiteIem(
                            websiteItem.getId(),
                            websiteItem.getReviews(),
                            websiteItem.getFit(),
                            websiteItem.getSize(),
                            websiteItem.getColor(),
                            websiteItem.getFeatures(),
                            websiteItem.isDetailsLoaded()
                    );
                });
            }
        });
    }

    public synchronized static PricefyRepository getInstance(ImageDao imageDao,
                                                             WebsiteItemDao websiteItemDao,
                                                             AmazonNetworkDaraSource networkDaraSource,
                                                             AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (instance == null) {
            synchronized (LOCK) {
                instance = new PricefyRepository(imageDao, websiteItemDao, networkDaraSource, executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return instance;
    }

    private synchronized void initializeData(long imageId, String params) {
        executors.diskIO().execute(() -> {
            if (isFetchOfItemsNeeded(imageId)) {
                startShoppingPlatformSync(imageId, params);
            }
        });
    }

    private boolean isFetchOfItemsNeeded(long imageId) {
        int count = websiteItemDao.countAllWebsiteItems(imageId);
        return count == 0;
    }

    private void startShoppingPlatformSync(long imageId, String params) {
        networkDataSource.startShoppingPlatformSyncIntentService(imageId, params);
    }

    private void startItemDetailSync(long id, String websiteItemUri) {
        networkDataSource.startItemDetailsSyncIntentService(id, websiteItemUri);
    }

    public void bulkInsert(List<WebsiteItem> websiteItems) {
        executors.diskIO().execute(() -> {
            websiteItemDao.bulkInsert(websiteItems.toArray(new WebsiteItem[websiteItems.size()]));
        });
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

    public List<Image> getImagesForWidget() {
        return imageDao.getImagesForWidget();
    }

    public void setImageId(long imageId) {
        this.websiteItems = websiteItemDao.getWebsiteItems(imageId);
    }

    public LiveData<List<WebsiteItem>> getWebsiteItems(long imageId, String params) {
        initializeData(imageId, params);
        return websiteItems;
    }

    public void setWebsiteItemId(long itemId) {
        this.websiteItem = websiteItemDao.getWebsiteItem(itemId);
    }

    public LiveData<WebsiteItem> getWebsiteItem(long id, String websiteItemUri) {
        initializedDetailResultData(id, websiteItemUri);
        return websiteItem;
    }

    private void initializedDetailResultData(long id, String websiteItemUri) {
        executors.diskIO().execute(() -> {
            if (isFetchOfSelectedItemNeeded(id)) {
                startItemDetailSync(id, websiteItemUri);
            }
        });
    }

    private boolean isFetchOfSelectedItemNeeded(long id) {
        boolean detailsLoaded = websiteItemDao.websiteItemDetailsLoaded(id);
        return !detailsLoaded;
    }

    /**
     * Calculates difference between old and new 'website items'
     */
    private static class DiffWebsiteItemsUpdateCallback implements ListUpdateCallback {

        private WebsiteItemDao websiteItemDao;
        private List<WebsiteItem> newWebsiteItems;

        public DiffWebsiteItemsUpdateCallback(WebsiteItemDao websiteItemDao, List<WebsiteItem> newWebsiteItems) {
            this.websiteItemDao = websiteItemDao;
            this.newWebsiteItems = newWebsiteItems;
        }

        @Override
        public void onInserted(int position, int count) {
            Log.d(LOG_TAG, "Values inserted. Add new website items");
            websiteItemDao.bulkInsert(newWebsiteItems.toArray(new WebsiteItem[newWebsiteItems.size()]));
        }

        @Override
        public void onRemoved(int position, int count) {
            Log.d(LOG_TAG, "Values removed");
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            Log.d(LOG_TAG, "Values moved");
        }

        @Override
        public void onChanged(int position, int count, Object payload) {
            Log.d(LOG_TAG, "Values changed. Add/replace website items if there are new ones");
            websiteItemDao.bulkInsert(newWebsiteItems.toArray(new WebsiteItem[newWebsiteItems.size()]));
        }
    }

    public SingleLiveEvent<WebsiteItemModel> getWebsiteItemsErrors() {
        return websiteItemsErrors;
    }

    private class ItemListObserver implements Observer<WebsiteItemModel> {
        @Override
        public void onChanged(@Nullable WebsiteItemModel newWebsiteItemModel) {
            executors.diskIO().execute(() -> {
                if (newWebsiteItemModel.getResultStatus() == WebsiteItemModel.ResultStatus.REQUEST_OK) {
                    final WebsiteItemsDiffCallback diffCallback =
                            new WebsiteItemsDiffCallback(websiteItems.getValue(), newWebsiteItemModel.getWebsiteItems());

                    final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
                    diffResult.dispatchUpdatesTo(
                            new DiffWebsiteItemsUpdateCallback(websiteItemDao, newWebsiteItemModel.getWebsiteItems())
                    );
                } else {
                    websiteItemsErrors.postValue(newWebsiteItemModel);
                }
            });
        }
    }
}
