package com.mitrai.scanner.receipt;

/**
 * Created by nirojans on 1/27/17.
 */
public class FinalLineItem {

    private String description;
    private String value;
    private int lineNumber;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
