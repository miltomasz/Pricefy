package com.plumya.pricefy.ui.results;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.plumya.pricefy.data.PricefyRepository;
import com.plumya.pricefy.data.local.model.WebsiteItem;
import com.plumya.pricefy.data.network.model.WebsiteItemModel;

import java.util.List;

/**
 * Created by miltomasz on 20/07/18.
 */

class ResultsActivityViewModel extends ViewModel {

    private final PricefyRepository repository;
    private MutableLiveData<List<WebsiteItem>> websiteItemsLiveData = new MutableLiveData<>();
    private SingleLiveEvent<WebsiteItemModel> websiteItemModelErrors;

    public ResultsActivityViewModel(PricefyRepository repository) {
        this.repository = repository;
        this.websiteItemModelErrors = repository.getWebsiteItemsErrors();
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

    public SingleLiveEvent<WebsiteItemModel> getWebsiteItemModelErrors() {
        return websiteItemModelErrors;
    }
}