package com.mitrai.scanner;

import com.mitrai.scanner.receipt.FinalLineItem;
import com.mitrai.scanner.score.ScoreSummary;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nirojans on 1/25/17.
 */
public class Result implements Serializable {

    private String id;
    private String status = "DONE";
    private int finalScore;
    private ScoreSummary scoreSummary;
    private String superMarketName;
    private String receiptTotal = "0";
    private List<FinalLineItem> OCRLineItemList;
    private List<FinalLineItem> ManualLineItemList;
    private OCRStats ocrStats;
    private List<String[]> rawDataList;

    public Result(String id) {
        this.id = id;
    }

    public ScoreSummary getScoreSummary() {
        return scoreSummary;
    }

    public void setScoreSummary(ScoreSummary scoreSummary) {
        this.scoreSummary = scoreSummary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OCRStats getOcrStats() {
        return ocrStats;
    }

    public void setOcrStats(OCRStats ocrStats) {
        this.ocrStats = ocrStats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FinalLineItem> getOCRLineItemList() {
        return OCRLineItemList;
    }

    public void setOCRLineItemList(List<FinalLineItem> OCRLineItemList) {
        this.OCRLineItemList = OCRLineItemList;
    }

    public List<FinalLineItem> getManualLineItemList() {
        return ManualLineItemList;
    }

    public void setManualLineItemList(List<FinalLineItem> manualLineItemList) {
        this.ManualLineItemList = manualLineItemList;
    }

    public List<String[]> getRawDataList() {
        return rawDataList;
    }

    public void setRawDataList(List<String[]> rawDataList) {
        this.rawDataList = rawDataList;
    }

    public String getSuperMarketName() {
        return superMarketName;
    }

    public void setSuperMarketName(String superMarketName) {
        this.superMarketName = superMarketName;
    }

    public String getReceiptTotal() {
        return receiptTotal;
    }

    public void setReceiptTotal(String receiptTotal) {
        this.receiptTotal = receiptTotal;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }
}
