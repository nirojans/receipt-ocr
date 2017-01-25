package com.mitrai.scanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nirojans on 1/25/17.
 */
public class Result implements Serializable {

    private List<String> fileNames;
    List<ManualReceipt> manualReceipts = new ArrayList<>();

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }
}
