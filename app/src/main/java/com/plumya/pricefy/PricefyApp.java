package com.plumya.pricefy;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by miltomasz on 19/07/18.
 */

public class PricefyApp extends Application {

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
