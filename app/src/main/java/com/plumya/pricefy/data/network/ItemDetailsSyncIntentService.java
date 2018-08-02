package com.plumya.pricefy.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.plumya.pricefy.di.Injector;
import com.plumya.pricefy.ui.results.ResultsActivity;

import org.jsoup.nodes.Document;


public class ItemDetailsSyncIntentService extends IntentService {

    private static final String LOG_TAG = ItemDetailsSyncIntentService.class.getSimpleName();

    public ItemDetailsSyncIntentService() {
        super("ItemDetailsSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "ItemDetailsSyncIntentService started");
        long itemId = intent.getLongExtra(ResultsActivity.ITEM_ID, -1);
        String websiteItemUri = intent.getStringExtra(ResultsActivity.ITEM_DETAILS_URI);
        NetworkDataSource networkDataSource =
                Injector.provideAmazonNetworkDataSource(this.getApplicationContext());
        Document document = networkDataSource.fetchItemDetails(websiteItemUri);
        networkDataSource.parseDetailsResponse(itemId, document);
    }
}
