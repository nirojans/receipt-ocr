package com.mitrai.scanner.score;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nirojans on 1/27/17.
 */
public class ScoreSummary implements Serializable {

    private int totalScore = 0 ;
    private int superMarketNameScore = 0 ;
    private int receiptTotalScore = 0 ;
    private int lineItemScore = 0 ;
    private List<LineScore> lineLineScoreList;

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getSuperMarketNameScore() {
        return superMarketNameScore;
    }

    public void setSuperMarketNameScore(int superMarketNameScore) {
        this.superMarketNameScore = superMarketNameScore;
    }

    public int getReceiptTotalScore() {
        return receiptTotalScore;
    }

    public void setReceiptTotalScore(int receiptTotalScore) {
        this.receiptTotalScore = receiptTotalScore;
    }

    public int getLineItemScore() {
        return lineItemScore;
    }

    public void setLineItemScore(int lineItemScore) {
        this.lineItemScore = lineItemScore;
    }

    public List<LineScore> getLineLineScoreList() {
        return lineLineScoreList;
    }

    public void setLineLineScoreList(List<LineScore> lineLineScoreList) {
        this.lineLineScoreList = lineLineScoreList;
    }
}
