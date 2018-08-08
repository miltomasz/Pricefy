package com.plumya.pricefy.utils;

import android.app.Application;

public class StethoUtil{

    public static void install(Application application){
        com.facebook.stetho.Stetho.initializeWithDefaults(application);
    }
}
