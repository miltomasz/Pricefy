package com.plumya.pricefy.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.plumya.pricefy.data.local.model.WebsiteItem;
import com.plumya.pricefy.data.network.model.WebsiteItemModel;
import com.plumya.pricefy.ui.main.MainActivity;
import com.plumya.pricefy.ui.results.ResultsActivity;
import com.plumya.pricefy.utils.AppExecutors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class AmazonNetworkDaraSource implements NetworkDataSource {

    private static final String AMAZON_BASE_URL = "https://www.amazon.com/";
    private static final String SEARCH_DOMAIN = "s/";
    private static final String FIELD_KEYWORDS = "&field-keywords=";

    public static final String LOG_TAG = AmazonNetworkDaraSource.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AmazonNetworkDaraSource instance;
    // Collaborators
    private final Context context;
    private final AppExecutors executors;
    private final WebsiteParser websiteParser;
    private final MutableLiveData<WebsiteItemModel> downloadedWebsiteItemModel;
    private final MutableLiveData<WebsiteItem> downloadedWebsiteItemDetails;

    private AmazonNetworkDaraSource(Context context, AppExecutors executors, WebsiteParser websiteParser) {
        this.context = context;
        this.executors = executors;
        this.websiteParser = websiteParser;
        this.downloadedWebsiteItemModel = new MutableLiveData<>();
        this.downloadedWebsiteItemDetails = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static AmazonNetworkDaraSource getInstance(Context context, AppExecutors executors, WebsiteParser websiteParser) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (instance == null) {
            synchronized (LOCK) {
                instance = new AmazonNetworkDaraSource(context.getApplicationContext(), executors, websiteParser);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return instance;
    }

    public LiveData<WebsiteItemModel> getWebsiteItems() {
        return downloadedWebsiteItemModel;
    }

    public LiveData<WebsiteItem> getWebsiteItem() {
        return downloadedWebsiteItemDetails;
    }

    @Override
    public Document executeRequest(String params) {
        Log.d(LOG_TAG, "Executing request: " + params);
        String url = getUrl();
        if (url == null) {
            throw new UnsupportedOperationException("Url should be initialized");
        }
        String requestUrl = url + params;
        try {
            Document document = Jsoup.connect(requestUrl).get();
            return document;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception while getting website: " + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public void parseResponse(long imageId, final Document document) {
        Log.d(LOG_TAG, "Parsing item list document: " + document.title());
        executors.networkIO().execute(() -> {
            int resultCode = WebsiteItemModel.ResultStatus.REQUEST_OK;
            List<WebsiteItem> websiteItems = null;
            try {
                websiteItems = websiteParser.parseItems(imageId, document);
                if (websiteItems == null || websiteItems.size() == 0) {
                    resultCode = WebsiteItemModel.ResultStatus.REQUEST_NO_DATA_FOUND;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception while parsing items: " + e.getMessage());
                resultCode = WebsiteItemModel.ResultStatus.REQUEST_PARSING_ERROR;
            }
            WebsiteItemModel model = new WebsiteItemModel(websiteItems, resultCode);
            downloadedWebsiteItemModel.postValue(model);
        });
    }

    @Override
    public String getUrl() {
        return AMAZON_BASE_URL + SEARCH_DOMAIN + FIELD_KEYWORDS;
    }

    @Override
    public String getItemDetailsUrl(String websiteItemUri) {
        if (websiteItemUri != null && websiteItemUri.startsWith("/gp/")) {
            return AMAZON_BASE_URL + websiteItemUri;
        }
        return websiteItemUri;
    }

    @Override
    public Document fetchItemDetails(String websiteItemUri) {
        String url = getItemDetailsUrl(websiteItemUri);
        if (url == null) {
            throw new UnsupportedOperationException("Url for item details should be initialized");
        }
        try {
            Log.d(LOG_TAG, "Fetching item details from uri: " + url);
            Document document = Jsoup.connect(url).get();
            return document;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception while getting website: " + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public void parseDetailsResponse(long itemId, Document document) {
        Log.d(LOG_TAG, "Parsing item details document: " + document.title());
        executors.networkIO().execute(() -> {
            WebsiteItem temporaryWebsiteItem = websiteParser.parseItemDetails(itemId, document);
            downloadedWebsiteItemDetails.postValue(temporaryWebsiteItem);
        });
    }

    /**
     * Starts an intent service to fetch items from shopping platform.
     */
    public void startShoppingPlatformSyncIntentService(long imageId, String params) {
        Intent intentToFetch = new Intent(context, ShoppingPlatformSyncIntentService.class);
        intentToFetch.putExtra(NetworkDataSource.PARAMS, params);
        intentToFetch.putExtra(MainActivity.IMAGE_ID, imageId);
        context.startService(intentToFetch);
    }

    /**
     * Starts an intent service to fetch details of selected item.
     */
    public void startItemDetailsSyncIntentService(long websiteItemId, String itemDetailsUri) {
        Intent intentToFetch = new Intent(context, ItemDetailsSyncIntentService.class);
        intentToFetch.putExtra(ResultsActivity.ITEM_ID, websiteItemId);
        intentToFetch.putExtra(ResultsActivity.ITEM_DETAILS_URI, itemDetailsUri);
        context.startService(intentToFetch);
    }
}
