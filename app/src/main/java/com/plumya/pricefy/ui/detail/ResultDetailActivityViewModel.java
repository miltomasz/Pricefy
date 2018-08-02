package com.plumya.pricefy.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.plumya.pricefy.data.PricefyRepository;
import com.plumya.pricefy.data.local.model.WebsiteItem;

/**
 * Created by miltomasz on 20/07/18.
 */

class ResultDetailActivityViewModel extends ViewModel {

    private final PricefyRepository repository;
    private LiveData<WebsiteItem> websiteItem;

    public ResultDetailActivityViewModel(PricefyRepository repository) {
        this.repository = repository;
    }

    public void setWebsiteItemId(long itemId, String websiteItemUri) {
        this.repository.setWebsiteItemId(itemId);
        this.websiteItem = repository.getWebsiteItem(itemId, websiteItemUri);
    }

    public LiveData<WebsiteItem> getWebsiteItem() {
        return websiteItem;
    }
}