package com.mitrai.scanner;

import java.util.List;

/**
 * Created by nirojans on 1/13/17.
 * This class will be considered as the final receipt of the
 * data extraction process
 */
public class MasterReceipt {

    private String id;
    private String superMartketName;
    private List<LineItem> lineItemList;
    private List<Receipt> receiptList;
    private String date;
    private String time;
    private String additionalInfo;
    private String unclassifiedData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSuperMartketName() {
        return superMartketName;
    }

    public void setSuperMartketName(String superMartketName) {
        this.superMartketName = superMartketName;
    }

    public List<LineItem> getLineItemList() {
        return lineItemList;
    }

    public void setLineItemList(List<LineItem> lineItemList) {
        this.lineItemList = lineItemList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getUnclassifiedData() {
        return unclassifiedData;
    }

    public void setUnclassifiedData(String unclassifiedData) {
        this.unclassifiedData = unclassifiedData;
    }

    public List<Receipt> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }
}
