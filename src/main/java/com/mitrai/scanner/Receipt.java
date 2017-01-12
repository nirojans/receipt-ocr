package com.mitrai.scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niro273 on 1/11/17.
 */
public class Receipt {

    private String superMarketName;
    private int nameRecognitionRank;
    private List<LineItem> lineItems = new ArrayList<>();
    private String date;
    private String otherValues;
    private String[] rawData;

    private int lineItemEndLine;
    private int lineItemStartLine;
    private int preprocessMethod;

    public String getSuperMarketName() {
        return superMarketName;
    }

    public void setSuperMarketName(String superMarketName) {
        this.superMarketName = superMarketName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOtherValues() {
        return otherValues;
    }

    public void setOtherValues(String otherValues) {
        this.otherValues = otherValues;
    }

    public int getNameRecognitionRank() {
        return nameRecognitionRank;
    }

    public void setNameRecognitionRank(int nameRecognitionRank) {
        this.nameRecognitionRank = nameRecognitionRank;
    }

    public String[] getRawData() {
        return rawData;
    }

    public void setRawData(String[] rawData) {
        this.rawData = rawData;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public int getLineItemEndLine() {
        return lineItemEndLine;
    }

    public void setLineItemEndLine(int lineItemEndLine) {
        this.lineItemEndLine = lineItemEndLine;
    }

    public int getLineItemStartLine() {
        return lineItemStartLine;
    }

    public void setLineItemStartLine(int lineItemStartLine) {
        this.lineItemStartLine = lineItemStartLine;
    }

    public int getPreprocessMethod() {
        return preprocessMethod;
    }

    public void setPreprocessMethod(int preprocessMethod) {
        this.preprocessMethod = preprocessMethod;
    }
}
