package com.plumya.pricefy.data.network;

import com.plumya.pricefy.data.local.model.WebsiteItem;

import org.jsoup.nodes.Document;

import java.util.List;

/**
 * General interface for parsers that will
 * parse particular shopping platform website.
 */
public interface WebsiteParser {

    String getImageUri();
    String getMainTitle();
    Double getPrice();
    Double getStars();
    List<WebsiteItem> parseItems(long imageId, Document document) throws Exception;
    WebsiteItem parseItemDetails(long itemId, Document document);
}
