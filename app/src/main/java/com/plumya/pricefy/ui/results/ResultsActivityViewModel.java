package com.plumya.pricefy.ui.results;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.plumya.pricefy.data.PricefyRepository;
import com.plumya.pricefy.data.local.model.WebsiteItem;

import java.util.List;

/**
 * Created by miltomasz on 20/07/18.
 */

class ResultsActivityViewModel extends ViewModel {

    private final PricefyRepository repository;
    private MutableLiveData<List<WebsiteItem>> websiteItemsLiveData = new MutableLiveData<>();

    public ResultsActivityViewModel(PricefyRepository repository) {
        this.repository = repository;
    }

    public void setImageParameters(long imageId, String params) {
        repository.setImageId(imageId);
        repository.getWebsiteItems(imageId, params).observeForever(new Observer<List<WebsiteItem>>() {
            @Override
            public void onChanged(@Nullable List<WebsiteItem> websiteItems) {
                if (websiteItems != null && websiteItems.size() > 0) {
                    websiteItemsLiveData.postValue(websiteItems);
                }
            }
        });
    }

    public LiveData<List<WebsiteItem>> getWebsiteItems() {
        return websiteItemsLiveData;
    }
}