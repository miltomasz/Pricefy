package com.plumya.pricefy.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.plumya.pricefy.di.Injector;
import com.plumya.pricefy.ui.main.MainActivity;

import org.jsoup.nodes.Document;


public class ShoppingPlatformSyncIntentService extends IntentService {

    private static final String LOG_TAG = ShoppingPlatformSyncIntentService.class.getSimpleName();

    public ShoppingPlatformSyncIntentService() {
        super("ShoppingPlatformSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "ShoppingPlatformSyncIntentService started");
        long imageId = intent.getLongExtra(MainActivity.IMAGE_ID, -1);
        String params = intent.getStringExtra(NetworkDataSource.PARAMS);
        NetworkDataSource networkDataSource =
                Injector.provideAmazonNetworkDataSource(this.getApplicationContext());
        Document document = networkDataSource.executeRequest(params);
        networkDataSource.parseResponse(imageId, document);
    }
}
