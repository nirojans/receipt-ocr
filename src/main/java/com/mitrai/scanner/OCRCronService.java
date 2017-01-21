package com.mitrai.scanner;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by nirojans on 1/13/17.
 */
public class OCRCronService {

    public void startDoingBatchProcessing() throws IOException, InterruptedException {
//        FileUtils.deleteDirectory(new File(FileHelper.baseFolder));

        System.out.println("Started the batch processing " + new Date());
        FileHelper.initBaseFolder();
        // Initiate all the folders for batch processing
        File[] listOfFiles = FileHelper.getAllFileNames();

        if (listOfFiles.length == 0) {
            System.out.println("No files found to be processed");
        }

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileNameWithExtension = listOfFiles[i].getName();
                String fileNameWithoutExtension = FileHelper.getFileNameWithoutExtension(listOfFiles[i]);
                // check if it is a valid input
                String extensionName = FileHelper.getFileExtension(listOfFiles[i]);
                if (extensionName.equalsIgnoreCase("jpeg") || extensionName.equalsIgnoreCase("jpg") || extensionName.equalsIgnoreCase("png")) {

                    if (FileHelper.isProd) {
                        TesseractEngine.performPreProcessingAndOCR(fileNameWithExtension);
                    }

                    // read all text
                    List<Receipt> receiptList = FileHelper.readAllResultsForAImage(fileNameWithoutExtension);
                    receiptList = TemplateEngine.identifySuperMarketName(receiptList);
                    TemplateEngine.identifyLineItems(receiptList);

                    MasterReceipt masterReceipt = new MasterReceipt(fileNameWithoutExtension);
                    masterReceipt.setReceiptList(receiptList);

                    Receipt highReceipt = receiptList.get(receiptList.size()-1);
                    FileHelper.writeResultsToFile(highReceipt, fileNameWithoutExtension + "_results.txt");

                    masterReceipt.setLineItemList(highReceipt.getLineItems());
//                    DataServiceImpl.insertIntoDB(masterReceipt);
                }
            }
        }

        System.out.println("Ending the batch processing " + new Date());
    }
}
