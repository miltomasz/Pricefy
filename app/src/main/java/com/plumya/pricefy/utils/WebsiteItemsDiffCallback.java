package com.plumya.pricefy.utils;

import android.support.v7.util.DiffUtil;

import com.plumya.pricefy.data.local.model.WebsiteItem;

import java.util.List;

/**
 * Created by miltomasz on 11/05/18.
 */

public class WebsiteItemsDiffCallback extends DiffUtil.Callback {

    private final List<WebsiteItem> websiteItemEntries;
    private final List<WebsiteItem> websiteItems;

    public WebsiteItemsDiffCallback(List<WebsiteItem> websiteItemEntries, List<WebsiteItem> websiteItems) {
        this.websiteItemEntries = websiteItemEntries;
        this.websiteItems = websiteItems;
    }

    @Override
    public int getOldListSize() {
        return websiteItemEntries.size();
    }

    @Override
    public int getNewListSize() {
        return websiteItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return websiteItemEntries.get(oldItemPosition).getId()
                == websiteItems.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        WebsiteItem websiteItemEntry = websiteItemEntries.get(oldItemPosition);
        WebsiteItem websiteItem = websiteItems.get(newItemPosition);
        return websiteItemEntry.getImageUri().equals(websiteItem.getImageUri());
    }
}
