package com.mitrai.scanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nirojans on 1/25/17.
 */
public class Result implements Serializable {

    private int batchProcessID;
    private List<String> fileNames;
    List<ManualReceipt> manualReceipts = new ArrayList<>();

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

    public List<ManualReceipt> getManualReceipts() {
        return manualReceipts;
    }

    public void setManualReceipts(List<ManualReceipt> manualReceipts) {
        this.manualReceipts = manualReceipts;
    }
}
