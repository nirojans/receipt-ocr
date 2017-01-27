package com.mitrai.scanner.score;

import java.io.Serializable;

/**
 * Created by nirojans on 1/26/17.
 */
public class LineScore implements Serializable{

    private String totalScore;
    private String desc;
    private String value;
    private String lineNumber;

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }
}
