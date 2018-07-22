package com.plumya.pricefy.di;

import android.content.Context;

import com.plumya.pricefy.data.PricefyRepository;
import com.plumya.pricefy.data.local.ImageDao;
import com.plumya.pricefy.data.local.PricefyDatabase;
import com.plumya.pricefy.ui.main.MainActivityViewModelFactory;
import com.plumya.pricefy.utils.AppExecutors;

/**
 * Created by miltomasz on 20/07/18.
 */

public class Injector {
    private Injector() {}

    public static PricefyRepository provideRepository(Context context) {
        PricefyDatabase database = PricefyDatabase.getInstance(context.getApplicationContext());
        ImageDao imageDao = database.imageDao();
        AppExecutors appExecutors = AppExecutors.getInstance();
        return PricefyRepository.getInstance(imageDao, appExecutors);
    }

    public static MainActivityViewModelFactory provideMainActivityViewModelFactory(Context context) {
        PricefyRepository repository = provideRepository(context.getApplicationContext());
        return new MainActivityViewModelFactory(repository);
    }
}
