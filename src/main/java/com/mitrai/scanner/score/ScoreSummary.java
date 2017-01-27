package com.mitrai.scanner.score;

import java.util.List;

/**
 * Created by nirojans on 1/27/17.
 */
public class ScoreSummary {

    private int totalScore;
    private int superMarketNameScore;
    private int lineTotalScore;
    private int lineItemScore;
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

    public int getLineTotalScore() {
        return lineTotalScore;
    }

    public void setLineTotalScore(int lineTotalScore) {
        this.lineTotalScore = lineTotalScore;
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
