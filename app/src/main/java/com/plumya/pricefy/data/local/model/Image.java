package com.plumya.pricefy.data.local.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by miltomasz on 19/07/18.
 */

@Entity(tableName = "images")
public class Image {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String uri;
    private Date captureDate;
    private String labels;
    private String texts;
    private String colors;
    private String bestWebGuess;

    @Ignore
    public Image(String uri, Date captureDate, String labels, String texts, String colors, String bestWebGuess) {
        this.uri = uri;
        this.captureDate = captureDate;
        this.labels = labels;
        this.texts = texts;
        this.colors = colors;
        this.bestWebGuess = bestWebGuess;
    }

    // Constructor used by Room
    public Image(long id, String uri, Date captureDate, String labels, String texts, String colors, String bestWebGuess) {
        this.id = id;
        this.uri = uri;
        this.captureDate = captureDate;
        this.labels = labels;
        this.texts = texts;
        this.colors = colors;
        this.bestWebGuess = bestWebGuess;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public Date getCaptureDate() {
        return captureDate;
    }

    public String getLabels() {
        return labels;
    }

    public String getTexts() {
        return texts;
    }

    public String getColors() {
        return colors;
    }

    public String getBestWebGuess() {
        return bestWebGuess;
    }

    public String labelsToReadableString() {
        return labels != null ? labels.replace("+", ", ") : "";
    }

    public String toDateString() {
        String dateFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(captureDate);
    }
}
