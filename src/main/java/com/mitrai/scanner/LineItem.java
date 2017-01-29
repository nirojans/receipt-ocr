package com.mitrai.scanner;

import java.io.Serializable;

/**
 * Created by niro273 on 1/11/17.
 */
public class LineItem implements Serializable {

    private int descriptionAccuracyLevel;
    private int descriptionAccuracyPercentage;
    private int valueAccuracyLevel;
    private int valueAccuracyPercentage;
    private int lineNumber;
    private int manualDatalineNumber;
    private String description;
    private String units;
    private String value;
    private String currencySymbol;

    private String manualDataDescription;
    private String manualDataValue;

    // full text score
    private double score;

    public LineItem() {

    }

    public void setManualDataForLineItem(int manualDatalineNumber, int descriptionAccuracyLevel,ManualReceiptLineItem manualReceiptLineItem) {
        this.manualDatalineNumber = manualDatalineNumber;
        this.descriptionAccuracyLevel = descriptionAccuracyLevel;
        this.manualDataDescription = manualReceiptLineItem.getTILLROLL_LINE_DESC();
        this.manualDataValue = manualReceiptLineItem.getLINE_PRICE();
    }

    public LineItem(int descriptionAccuracyLevel, int lineNumber, int manualDatalineNumber, String description, String units, String value, String currencySymbol,
                    String manualDataDescription, String manualDataValue) {
        this.descriptionAccuracyLevel = descriptionAccuracyLevel;
        this.lineNumber = lineNumber;
        this.manualDatalineNumber = manualDatalineNumber;
        this.description = description;
        this.units = units;
        this.value = value;
        this.currencySymbol = currencySymbol;
        this.manualDataDescription = manualDataDescription;
        this.manualDataValue = manualDataValue;
    }

    public int getDescriptionAccuracyLevel() {
        return descriptionAccuracyLevel;
    }

    public void setDescriptionAccuracyLevel(int descriptionAccuracyLevel) {
        this.descriptionAccuracyLevel = descriptionAccuracyLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {

        this.lineNumber = lineNumber;
    }

    public int getManualDatalineNumber() {
        return manualDatalineNumber;
    }

    public void setManualDatalineNumber(int manualDatalineNumber) {
        this.manualDatalineNumber = manualDatalineNumber;
    }

    public String getManualDataDescription() {
        return manualDataDescription;
    }

    public void setManualDataDescription(String manualDataDescription) {
        this.manualDataDescription = manualDataDescription;
    }

    public String getManualDataValue() {
        return manualDataValue;
    }

    public void setManualDataValue(String manualDataValue) {
        this.manualDataValue = manualDataValue;
    }

    public int getValueAccuracyLevel() {
        return valueAccuracyLevel;
    }

    public void setValueAccuracyLevel(int valueAccuracyLevel) {
        this.valueAccuracyLevel = valueAccuracyLevel;
    }

    public int getDescriptionAccuracyPercentage() {
        return descriptionAccuracyPercentage;
    }

    public void setDescriptionAccuracyPercentage(int descriptionAccuracyPercentage) {
        this.descriptionAccuracyPercentage = descriptionAccuracyPercentage;
    }

    public int getValueAccuracyPercentage() {
        return valueAccuracyPercentage;
    }

    public void setValueAccuracyPercentage(int valueAccuracyPercentage) {
        this.valueAccuracyPercentage = valueAccuracyPercentage;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
