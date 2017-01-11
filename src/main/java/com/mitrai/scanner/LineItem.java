package com.mitrai.scanner;

/**
 * Created by niro273 on 1/11/17.
 */
public class LineItem {

    int accuracyLevel;
    String description;
    String units;
    String value;
    String currencySymbol;


    public int getAccuracyLevel() {
        return accuracyLevel;
    }

    public void setAccuracyLevel(int accuracyLevel) {
        this.accuracyLevel = accuracyLevel;
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
}