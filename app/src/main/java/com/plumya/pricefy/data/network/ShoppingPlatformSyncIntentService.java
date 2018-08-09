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
        try {
            Document document = networkDataSource.executeRequest(params);
            if (document == null) {
                Log.e(LOG_TAG, "Null document returned from request with params: " + params);
                networkDataSource.errorCallback(NetworkDataSource.ResultStatus.REQUEST_NETWORK_ERROR);
                return;
            }
            networkDataSource.parseResponse(imageId, document);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception occurred while executing request: " + e.getMessage());
            networkDataSource.errorCallback(NetworkDataSource.ResultStatus.REQUEST_PARSING_ERROR);
        }
    }
}
