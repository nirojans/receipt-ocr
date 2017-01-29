package com.mitrai.scanner;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by nirojans on 1/26/17.
 */
public class OCRStats implements Serializable{

    private String id;
    private String status;
    private String[] identifiedDescriptionAndValue;
    private String[] unclassifiedManualLineItem;
    private String[] unclassifiedOCRLineItem;
    private Map<Integer, Integer> descriptionAccuracyHistogram;
    private Map<Integer, Integer> valueAccuracyHistogram;
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

    public String[] getIdentifiedDescriptionAndValue() {
        return identifiedDescriptionAndValue;
    }

    public void setIdentifiedDescriptionAndValue(String[] identifiedDescriptionAndValue) {
        this.identifiedDescriptionAndValue = identifiedDescriptionAndValue;
    }

    public Map<Integer, Integer> getDescriptionAccuracyHistogram() {
        return descriptionAccuracyHistogram;
    }

    public void setDescriptionAccuracyHistogram(Map<Integer, Integer> descriptionAccuracyHistogram) {
        this.descriptionAccuracyHistogram = descriptionAccuracyHistogram;
    }

    public Map<Integer, Integer> getValueAccuracyHistogram() {
        return valueAccuracyHistogram;
    }

    public void setValueAccuracyHistogram(Map<Integer, Integer> valueAccuracyHistogram) {
        this.valueAccuracyHistogram = valueAccuracyHistogram;
    }

    public int getBrandAccuracy() {
        return brandAccuracy;
    }

    public void setBrandAccuracy(int brandAccuracy) {
        this.brandAccuracy = brandAccuracy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getUnclassifiedManualLineItem() {
        return unclassifiedManualLineItem;
    }

    public void setUnclassifiedManualLineItem(String[] unclassifiedManualLineItem) {
        this.unclassifiedManualLineItem = unclassifiedManualLineItem;
    }

    public String[] getUnclassifiedOCRLineItem() {
        return unclassifiedOCRLineItem;
    }

    public void setUnclassifiedOCRLineItem(String[] unclassifiedOCRLineItem) {
        this.unclassifiedOCRLineItem = unclassifiedOCRLineItem;
    }
}
