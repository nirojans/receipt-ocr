package com.mitrai.scanner;

import java.io.File;
import java.util.List;

/**
 * Created by nirojans on 1/28/17.
 */
public class Test {

    public static void copyRandomFilesToRawFolder() {
        // If the random batch processing status is true then copy files from tesco and sainsbury and put in the raw folder
        List<File> tescoFileList = FileHelper.selectRandomReceipts(FileHelper.tescoReceiptsFolderPath);
        List<File> sainsburyFileList = FileHelper.selectRandomReceipts(FileHelper.sainsburyReceiptsFolderPath);

        FileHelper.copySelectedReceiptsToRawImageFolder(tescoFileList);
        FileHelper.copySelectedReceiptsToRawImageFolder(sainsburyFileList);

    }
}
