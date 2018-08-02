package com.plumya.pricefy.di;

import android.content.Context;

import com.plumya.pricefy.data.PricefyRepository;
import com.plumya.pricefy.data.local.ImageDao;
import com.plumya.pricefy.data.local.PricefyDatabase;
import com.plumya.pricefy.data.local.WebsiteItemDao;
import com.plumya.pricefy.data.network.AmazonNetworkDaraSource;
import com.plumya.pricefy.data.network.AmazonWebsiteParser;
import com.plumya.pricefy.ui.detail.ResultDetailActivityViewModelFactory;
import com.plumya.pricefy.ui.main.MainActivityViewModelFactory;
import com.plumya.pricefy.ui.results.ResultsActivityViewModelFactory;
import com.plumya.pricefy.utils.AppExecutors;

/**
 * Created by miltomasz on 20/07/18.
 */

public class Injector {

    private Injector() {}

    public static PricefyRepository provideRepository(Context context) {
        PricefyDatabase database = PricefyDatabase.getInstance(context.getApplicationContext());
        ImageDao imageDao = database.imageDao();
        WebsiteItemDao websiteItemDao = database.websiteItemDao();
        AmazonNetworkDaraSource networkDaraSource = provideAmazonNetworkDataSource(context);
        AppExecutors appExecutors = AppExecutors.getInstance();
        return PricefyRepository.getInstance(imageDao, websiteItemDao, networkDaraSource, appExecutors);
    }

    public static MainActivityViewModelFactory provideMainActivityViewModelFactory(Context context) {
        PricefyRepository repository = provideRepository(context.getApplicationContext());
        return new MainActivityViewModelFactory(repository);
    }

    public static ResultsActivityViewModelFactory provideResultsActivityViewModelFactory(Context context) {
        PricefyRepository repository = provideRepository(context.getApplicationContext());
        return new ResultsActivityViewModelFactory(repository);
    }

    public static ResultDetailActivityViewModelFactory provideResultsDetailViewModelFactory(Context context) {
        PricefyRepository repository = provideRepository(context.getApplicationContext());
        return new ResultDetailActivityViewModelFactory(repository);
    }

    public static AmazonNetworkDaraSource provideAmazonNetworkDataSource(Context context) {
        AppExecutors appExecutors = AppExecutors.getInstance();
        AmazonWebsiteParser websiteParser = provideAmazonWebsiteParser();
        return AmazonNetworkDaraSource.getInstance(context.getApplicationContext(), appExecutors, websiteParser);
    }

    public static AmazonWebsiteParser provideAmazonWebsiteParser() {
        return new AmazonWebsiteParser();
    }
}
