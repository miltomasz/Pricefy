package com.plumya.pricefy.utils;

public class LinkUtil {

    private static final String AMAZON_BASE_URI = "https://www.amazon.com";

    private LinkUtil() {}

    public static String prepareItemDetailsUrl(String websiteItemUri) {
        if (websiteItemUri != null && websiteItemUri.startsWith("/gp/")) {
            return AMAZON_BASE_URI + websiteItemUri;
        } else if (websiteItemUri != null && websiteItemUri.startsWith(AMAZON_BASE_URI)){
            return websiteItemUri;
        } else {
            return AMAZON_BASE_URI;
        }
    }

    public static String secureParams(String params) {
        if (params == null) return "";
        return params.replace(" ", "+");
    }
}
