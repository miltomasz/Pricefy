package com.plumya.pricefy.data.network.model;

import com.plumya.pricefy.data.local.model.WebsiteItem;

import java.util.List;

public class WebsiteItemModel {

    private List<WebsiteItem> websiteItems;
    private int resultStatus;

    public WebsiteItemModel(List<WebsiteItem> websiteItems, int resultStatus) {
        this.websiteItems = websiteItems;
        this.resultStatus = resultStatus;
    }

    public interface ResultStatus {
        int REQUEST_OK = 1;
        int REQUEST_NO_DATA_FOUND = 2;
        int REQUEST_PARSING_ERROR = 3;
    }

    public List<WebsiteItem> getWebsiteItems() {
        return websiteItems;
    }

    public int getResultStatus() {
        return resultStatus;
    }
}
