package com.mitrai.scanner;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by nirojans on 1/26/17.
 */
public class OCRStats implements Serializable{

    private String id;
    private List<String[]> descriptionValueStats;
    private Map<Integer, Integer> descriptionHistogram;
    private Map<Integer, Integer> valueHistogram;
    private int brandAccuracy;

    public OCRStats(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String[]> getDescriptionValueStats() {
        return descriptionValueStats;
    }

    public void setDescriptionValueStats(List<String[]> descriptionValueStats) {
        this.descriptionValueStats = descriptionValueStats;
    }

    public Map<Integer, Integer> getDescriptionHistogram() {
        return descriptionHistogram;
    }

    public void setDescriptionHistogram(Map<Integer, Integer> descriptionHistogram) {
        this.descriptionHistogram = descriptionHistogram;
    }

    public Map<Integer, Integer> getValueHistogram() {
        return valueHistogram;
    }

    public void setValueHistogram(Map<Integer, Integer> valueHistogram) {
        this.valueHistogram = valueHistogram;
    }

    public int getBrandAccuracy() {
        return brandAccuracy;
    }

    public void setBrandAccuracy(int brandAccuracy) {
        this.brandAccuracy = brandAccuracy;
    }
}
