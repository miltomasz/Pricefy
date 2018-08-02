package com.plumya.pricefy.data.network;

import android.text.TextUtils;
import android.util.Log;

import com.plumya.pricefy.data.local.model.WebsiteItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AmazonWebsiteParser implements WebsiteParser {

    private static final String LOG_TAG = AmazonWebsiteParser.class.getSimpleName();
    private static final int FROM = 0;
    private static final int TO = 1;

    @Override
    public String getImageUri() {
        return null;
    }

    @Override
    public String getMainTitle() {
        return null;
    }

    @Override
    public Double getPrice() {
        return null;
    }

    @Override
    public Double getStars() {
        return null;
    }

    @Override
    public List<WebsiteItem> parseItems(long imageId, Document doc) {
        List<WebsiteItem> websiteItems = new ArrayList<>();

        boolean oneColumn = isOneColumn(doc);

        Elements itemBoxes = doc.select("div.s-item-container");
        Log.d(LOG_TAG, "Found items: " + itemBoxes.size());

        for (int i = 0; i < itemBoxes.size(); i++) {
            Element item = itemBoxes.get(i);
            if (item.html() == null || item.html().isEmpty()) continue;

            String imageUri = extractImageUri(item);
            if (TextUtils.isEmpty(imageUri)) continue;

            String detailsUri = extractDetailsUri(item);
            if (TextUtils.isEmpty(detailsUri)) continue;

            String mainTitle = extractMainTitle(oneColumn, item);

            Float[] price = extractPrice(item);
            if (price[0] == 0 && price[1] == 0) continue;

            Float stars = extractStars(oneColumn, item);

            WebsiteItem websiteItem = new WebsiteItem(imageId, imageUri, detailsUri, mainTitle, price[FROM], price[TO], stars);
            websiteItems.add(websiteItem);
        }
        return websiteItems;
    }

    private String extractDetailsUri(Element item) {
        String detailsUri = item
                .select("a[class$=a-link-normal s-access-detail-page  s-color-twister-title-link a-text-normal]")
                .attr("href");
        return detailsUri;
    }

    @Override
    public WebsiteItem parseItemDetails(long itemId, Document doc) {
        String reviewsItem = doc.getElementById("acrCustomerReviewText") != null
                ? doc.getElementById("acrCustomerReviewText").text()
                : "";
        String fit = doc.getElementById("fitRecommendationsLinkRatingText") != null
                ? doc.getElementById("fitRecommendationsLinkRatingText").text()
                : "";

        Element colorDiv = doc.getElementById("variation_color_name");
        String color = "";
        if (colorDiv != null) {
            Elements spanSelection = colorDiv.select("span[class$=selection]");
            color = spanSelection.size() > 0 ? spanSelection.get(0).text() : "";
        }

        Elements spanSize = doc.select("span[class$=a-dropdown-prompt]");
        String size = spanSize.size() > 0 ? spanSize.get(0).text() : "";

        Element featuresDiv = doc.getElementById("feature-bullets");
        String featuresUl = "";
        final String UL_START = "<ul>";
        final String UL_END = "</ul>";
        if (featuresDiv != null) {
            Elements spanUl = featuresDiv.select("li").not("[id]");
            featuresUl =  UL_START + spanUl.outerHtml() + UL_END;
        }

        WebsiteItem temporaryWebsiteItem = new WebsiteItem(
                itemId, reviewsItem, fit, size, color, featuresUl, true);
        return temporaryWebsiteItem;
    }

    private Float extractStars(boolean oneColumn, Element item) {
        String starsDivSelect = oneColumn
                ? "div[class$=a-row a-spacing-mini]"
                : "div[class$=a-row a-spacing-top-mini a-spacing-none]";
        Elements starsDiv = item.select(starsDivSelect);
        Elements spanStars = starsDiv.select("span[class$=a-icon-alt]");
        String starsString = spanStars.size() > 0 ? spanStars.text().split(" ")[0] : "0.0";
        Float stars = Float.parseFloat(starsString);
        Log.d(LOG_TAG, "Stars: " + stars);
        return stars;
    }

    private Float[] extractPrice(Element item) {
        Elements spanPriceWhole = item.select("span[class$=sx-price-whole]");
        Elements spanPriceFractional = item.select("sup[class$=sx-price-fractional]");
        Elements spanDashFormatting = item.select("span[class$=sx-dash-formatting]");
        Float priceFrom = 0f;
        Float priceTo = 0f;
        if (spanDashFormatting.size() > 0) {
            // price range
            if (spanPriceWhole.size() > 1 && spanPriceFractional.size() > 1) {
                String priceWholes1 = spanPriceWhole.get(0).text().replace(",", "");
                String priceWholes2 = spanPriceWhole.get(1).text().replace(",", "");
                String priceFractionals1 = spanPriceFractional.get(0).text().replace(",", "");
                String priceFractionals2 = spanPriceFractional.get(1).text().replace(",", "");
                priceFrom = Float.parseFloat(priceWholes1 + "." + priceFractionals1);
                priceTo = Float.parseFloat(priceWholes2+ "." + priceFractionals2);
                Log.d(LOG_TAG, "Price range: " + priceFrom + " - " + priceTo);
            }
        } else {
            // price
            if (spanPriceWhole.size() > 0 && spanPriceFractional.size() > 0){
                String priceWhole = spanPriceWhole.get(0).text().replace(",", "");
                String priceFractional = spanPriceFractional.get(0).text().replace(",", "");
                priceFrom = Float.parseFloat(priceWhole + "." + priceFractional);
            }
            Log.d(LOG_TAG, "Price: " + priceFrom);
        }
        return new Float[] {priceFrom, priceTo};
    }

    private String extractMainTitle(boolean oneColumn, Element item) {
        String mainTitleSelect = oneColumn
                ? "h2[class$=a-size-medium s-inline  s-access-title  a-text-normal]"
                : "h2[class$=a-size-base s-inline  s-access-title  a-text-normal]";
        String mainTitle = item.select(mainTitleSelect).text();
        Log.d(LOG_TAG, "Main title: " + mainTitle);
        return mainTitle;
    }

    private String extractImageUri(Element item) {
        String imageUri = item.select("img[class$=s-access-image cfMarker]").attr("src");
        Log.d(LOG_TAG, "Image uri: " + imageUri);
        return imageUri;
    }

    private boolean isOneColumn(Document doc) {
        return doc.getElementById("s-results-list-atf")
                    .attr("class")
                    .contains("s-col-1 s-col-ws-1");
    }
}
