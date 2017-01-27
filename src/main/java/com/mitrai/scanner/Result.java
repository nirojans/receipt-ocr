package com.mitrai.scanner;

import com.mitrai.scanner.score.ScoreSummary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nirojans on 1/25/17.
 */
public class Result implements Serializable {

    private int batchProcessID;
    private List<String> fileNames;
    private List<OCRStats> ocrStatsList;
    private ScoreSummary scoreSummary;

    public Result(int batchProcessID) {
        this.batchProcessID = batchProcessID;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public int getBatchProcessID() {
        return batchProcessID;
    }

    public void setBatchProcessID(int batchProcessID) {
        this.batchProcessID = batchProcessID;
    }

    public List<OCRStats> getOcrStatsList() {
        return ocrStatsList;
    }

    public void setOcrStatsList(List<OCRStats> ocrStatsList) {
        this.ocrStatsList = ocrStatsList;
    }
}
