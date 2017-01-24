package com.mitrai.scanner;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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

                    List<Receipt> receiptList = performOCRBasedOnProdOrDev(fileNameWithExtension, fileNameWithoutExtension);

                    // read all text
                    receiptList = FileHelper.readAllResultsForAImage(fileNameWithoutExtension);

                    MasterReceipt masterReceipt = new MasterReceipt(fileNameWithoutExtension);
                    masterReceipt = TemplateEngine.identifySuperMarketName(receiptList, masterReceipt);

                    TemplateEngine.identifyLineItems(receiptList);

                    masterReceipt.setReceiptList(receiptList);

                    Receipt highReceipt = receiptList.get(receiptList.size()-1);
                    FileHelper.writeResultsToFile(highReceipt, fileNameWithoutExtension + "_results.txt");

                    masterReceipt.setLineItemList(highReceipt.getLineItems());
                    DataServiceImpl.insertIntoDB(masterReceipt);

                    // get Manual receipt data to compare
                    List<ManualReceipt> manualTescoReceiptList = DataServiceImpl.getReceiptFromManualData(fileNameWithoutExtension, DataServiceImpl.manualDataTescoCollection);
                    List<ManualReceipt> manualSainsReceiptList = DataServiceImpl.getReceiptFromManualData(fileNameWithoutExtension, DataServiceImpl.manualDataSaintsCollection);

                    List<ManualReceipt> selectedManualReceiptList = new ArrayList<>();

                    // Do the full text search for the Line Items identified
                    ArrayList<LineItem> predictedLineItemList = new ArrayList<>();
                    for (LineItem items : highReceipt.getLineItems()) {
                        String output = doFullTextSearchForLineItems(items.getDescription(), masterReceipt.getSuperMarketName());
                        LineItem predictedItems = new LineItem();
                        predictedItems.setDescription(output);
                        predictedLineItemList.add(predictedItems);
                    }

                    highReceipt.setPredictedLineItemFromManualData(predictedLineItemList);

                    // implement method to calculate line items accuracy for identified Line items


                    if (manualTescoReceiptList.size() != 0 && manualSainsReceiptList.size() != 0) {
                        System.out.println("record exists in both excel, cannot compare accuracy");
                    } else if (manualTescoReceiptList.size() != 0) {
                        selectedManualReceiptList = manualTescoReceiptList;
                    } else if (manualSainsReceiptList.size() != 0) {
                        selectedManualReceiptList = manualSainsReceiptList;
                    } else {
                        System.out.println("Manul record data not found for this file name cannot compare accuracy");
                    }

                    int superMarketNameAccuracy = AccuracyTest.verifySuperMarketBrand(masterReceipt.getSuperMarketName(), selectedManualReceiptList);

                }
            }
        }

        System.out.println("Ending the batch processing " + new Date());
    }

    public static String doFullTextSearchForLineItems(String lineItem, String superMarketBrand) throws UnknownHostException {

        lineItem = lineItem.trim().replaceAll(" +", " ");
        String collectionName = "";
        if (superMarketBrand.equalsIgnoreCase(Configs.TESCO_BRAND_NAME)) {
            collectionName = DataServiceImpl.manualDataTescoCollection;
        } else if (superMarketBrand.equalsIgnoreCase(Configs.SAINSBURY_BRAND_NAME)) {
            collectionName = DataServiceImpl.manualDataSaintsCollection;
        } else if (superMarketBrand.equalsIgnoreCase("null")) {
            // TODO search in both DB's and get the highest result
        }

        List<ManualReceipt> manualReceiptList = DataServiceImpl.doFullTextSearchFromManualData(lineItem, collectionName);
        if (manualReceiptList.size() != 0) {
            if (1.4 < manualReceiptList.get(0).getScore()) {
                lineItem = manualReceiptList.get(0).getTILLROLL_LINE_DESC();
            }
        }
        return lineItem;
    }


    public static List<Receipt> performOCRBasedOnProdOrDev(String fileNameWithExtension, String fileNameWithoutExtension) throws IOException, InterruptedException {
        List<Receipt> receiptList;
        if (FileHelper.isProd) {
            TesseractEngine.performPreProcessingAndOCR(fileNameWithExtension);
        } else {
            // If the results files are not found do the pre processing
            receiptList = FileHelper.readAllResultsForAImage(fileNameWithoutExtension);
            if (receiptList.size() == 0) {
                TesseractEngine.performPreProcessingAndOCR(fileNameWithExtension);
            }
        }
        return FileHelper.readAllResultsForAImage(fileNameWithoutExtension);
    }
}
