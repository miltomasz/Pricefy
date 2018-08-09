package com.plumya.pricefy.data.network;


import org.jsoup.nodes.Document;

/**
 * Created by miltomasz on 23/07/18.
 */

public interface NetworkDataSource {

    String PARAMS = "params";

    Document executeRequest(String params);

    void parseResponse(long itemId, Document document);

    String getUrl();

    String getItemDetailsUrl(String websiteItemUri);

    Document fetchItemDetails(String websiteItemUri);

    void parseDetailsResponse(long itemId, Document document);

    void errorCallback(int resultStatus);

    interface ResultStatus {
        int REQUEST_OK = 1;
        int REQUEST_NO_DATA_FOUND = 2;
        int REQUEST_PARSING_ERROR = 3;
        int REQUEST_NETWORK_ERROR = 4;
        int UNKNOWN_ERROR = 5;
    }
}
