package com.plumya.pricefy.utils;

public class UIUtil {

    private static final String DEFAULT_PRICE_VALUE = "0.0";

    private UIUtil() {}

    public static String formatPrice(String priceFrom, String priceTo) {
        return "$" + priceFrom + (priceTo.equals(DEFAULT_PRICE_VALUE) ? "" : " - " + priceTo);
    }
}
