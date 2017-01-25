package com.mitrai.scanner;

/**
 * Created by nirojans on 1/25/17.
 */
public class ReceiptResult {

    // id = fileName
    private String id;
    private int[] descriptionHistogram;
    private int[] valueHistogram;
    private int superMarketAccuracy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int[] getDescriptionHistogram() {
        return descriptionHistogram;
    }

    public void setDescriptionHistogram(int[] descriptionHistogram) {
        this.descriptionHistogram = descriptionHistogram;
    }

    public int[] getValueHistogram() {
        return valueHistogram;
    }

    public void setValueHistogram(int[] valueHistogram) {
        this.valueHistogram = valueHistogram;
    }

    public int getSuperMarketAccuracy() {
        return superMarketAccuracy;
    }

    public void setSuperMarketAccuracy(int superMarketAccuracy) {
        this.superMarketAccuracy = superMarketAccuracy;
    }
}
