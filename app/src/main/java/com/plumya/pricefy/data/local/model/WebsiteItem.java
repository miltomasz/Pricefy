package com.plumya.pricefy.data.local.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "website_items",
        foreignKeys = @ForeignKey(entity = Image.class,
                parentColumns = "id",
                childColumns = "imageId",
                onDelete = ForeignKey.CASCADE))
public class WebsiteItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long imageId;
    private String imageUri;
    private String detailsUri;
    private String mainTitle;
    private Float priceFrom;
    private Float priceTo;
    private Float stars;
    private String reviews;
    private String fit;
    private String size;
    private String color;
    private String features;
    private boolean detailsLoaded;

    @Ignore
    public WebsiteItem(long imageId, String imageUri, String detailsUri, String mainTitle,
                       Float priceFrom, Float priceTo, Float stars) {
        this.imageId = imageId;
        this.imageUri = imageUri;
        this.detailsUri = detailsUri;
        this.mainTitle = mainTitle;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.stars = stars;
    }

    @Ignore
    public WebsiteItem(long id, String reviews, String fit, String size, String color,
                       String features, boolean detailsLoaded) {
        this.id = id;
        this.reviews = reviews;
        this.fit = fit;
        this.size = size;
        this.color = color;
        this.features = features;
        this.detailsLoaded = detailsLoaded;
    }


    public WebsiteItem(long id, long imageId, String imageUri, String detailsUri, String mainTitle,
                       Float priceFrom, Float priceTo, Float stars, String reviews,
                       String fit, String size, String color, String features, boolean detailsLoaded) {
        this.id = id;
        this.imageId = imageId;
        this.imageUri = imageUri;
        this.detailsUri = detailsUri;
        this.mainTitle = mainTitle;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.stars = stars;
        this.reviews = reviews;
        this.fit = fit;
        this.size = size;
        this.color = color;
        this.features = features;
        this.detailsLoaded = detailsLoaded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public Float getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(Float priceFrom) {
        this.priceFrom = priceFrom;
    }

    public Float getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(Float priceTo) {
        this.priceTo = priceTo;
    }

    public Float getStars() {
        return stars;
    }

    public void setStars(Float stars) {
        this.stars = stars;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getFit() {
        return fit;
    }

    public void setFit(String fit) {
        this.fit = fit;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public boolean isDetailsLoaded() {
        return detailsLoaded;
    }

    public void setDetailsLoaded(boolean detailsLoaded) {
        this.detailsLoaded = detailsLoaded;
    }

    public String getDetailsUri() {
        return detailsUri;
    }

    public void setDetailsUri(String detailsUri) {
        this.detailsUri = detailsUri;
    }
}
