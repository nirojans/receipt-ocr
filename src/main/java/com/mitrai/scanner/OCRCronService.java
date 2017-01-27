package com.mitrai.scanner;

import com.mitrai.scanner.score.ScoreSummary;

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


        List<OCRStats> ocrStatsList = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileNameWithExtension = listOfFiles[i].getName();
                String fileNameWithoutExtension = FileHelper.getFileNameWithoutExtension(listOfFiles[i]);
                // check if it is a valid input
                String extensionName = FileHelper.getFileExtension(listOfFiles[i]);
                if (extensionName.equalsIgnoreCase("jpeg") || extensionName.equalsIgnoreCase("jpg") || extensionName.equalsIgnoreCase("png")) {

                    List<Receipt> receiptList = performOCRBasedOnProdOrDev(fileNameWithExtension, fileNameWithoutExtension);
                    OCRStats ocrStats = new OCRStats(fileNameWithoutExtension);
                    // read all text
                    receiptList = FileHelper.readAllResultsForAImage(fileNameWithoutExtension);

                    Result result = new Result(DataServiceImpl.getNextSequence());
                    result.setId(fileNameWithExtension);

                    if (receiptList.size() == 0) {
                        result.setStatus(Configs.CANNOT_PROCESS);
                        DataServiceImpl.insertBatchProcessDetails(result);
                        continue;
                    }

                    MasterReceipt masterReceipt = new MasterReceipt(fileNameWithoutExtension);
                    masterReceipt = TemplateEngine.identifySuperMarketName(receiptList, masterReceipt);

                    String superMarketBrand = masterReceipt.getSuperMarketName();
                    TemplateEngine.identifyLineItems(receiptList);

                    masterReceipt.setReceiptList(receiptList);

                    Receipt highReceipt = receiptList.get(receiptList.size()-1);
                    if (highReceipt.getLineItems().size() == 0) {
                        result.setStatus(Configs.CANNOT_PROCESS);
                        DataServiceImpl.insertBatchProcessDetails(result);
                        continue;
                    }

                    highReceipt = TemplateEngine.removeAppostrofeFromLineItems(highReceipt);
                    result.setRawData(highReceipt.getRawData());


                    masterReceipt.setLineItemList(highReceipt.getLineItems());
                    DataServiceImpl.insertIntoDB(masterReceipt);

//                    List<String> stringList = getLineItemsInStringArray(highReceipt.getLineItems());
                    // get Manual receipt data to compare
                    List<ManualReceiptLineItem> manualTescoReceiptList = DataServiceImpl.getReceiptFromManualData(fileNameWithoutExtension, DataServiceImpl.manualDataTescoCollection);
                    List<ManualReceiptLineItem> manualSainsReceiptList = DataServiceImpl.getReceiptFromManualData(fileNameWithoutExtension, DataServiceImpl.manualDataSaintsCollection);

                    List<ManualReceiptLineItem> selectedManualReceiptLineItemList = new ArrayList<>();

                    // Do the full text search for the Line Items identified with highest accuracy
                    ArrayList<LineItem> predictedLineItemList = new ArrayList<>();

                    try {
                        for (LineItem item : highReceipt.getLineItems()) {

                            String output = doFullTextSearchForLineItems(item.getDescription(), superMarketBrand);

                            LineItem predictedItems = new LineItem();
                            predictedItems.setValue(item.getValue());
                            predictedItems.setDescription(output);
                            predictedItems.setLineNumber(item.getLineNumber());
                            predictedLineItemList.add(predictedItems);
                        }
                    } catch (Exception e) {
                        System.out.printf("Exception occurred while doing the full text search for data cleaning");
                    }


                    highReceipt.setPredictedLineItemFromManualData(predictedLineItemList);

                    if (manualTescoReceiptList.size() != 0 && manualSainsReceiptList.size() != 0) {
                        System.out.println("Manual record data not found for this file name cannot compare accuracy");
                        result.setStatus("NO MANUAL RECORD FOUND FOR ACCURACY CHECK");
                        DataServiceImpl.insertBatchProcessDetails(result);
                        continue;
                    } else if (manualTescoReceiptList.size() != 0) {
                        selectedManualReceiptLineItemList = manualTescoReceiptList;
                    } else if (manualSainsReceiptList.size() != 0) {
                        selectedManualReceiptLineItemList = manualSainsReceiptList;
                    }

                    result.setFinalManualLineItemList(Utils.getManualFinalLineItemList(selectedManualReceiptLineItemList));

                    ScoreSummary scoreSummary = new ScoreSummary();

                    AccuracyTest.verifySuperMarketBrand(scoreSummary, masterReceipt.getSuperMarketName(), selectedManualReceiptLineItemList);
                    AccuracyTest.verifyLineItems(scoreSummary, ocrStats, highReceipt, selectedManualReceiptLineItemList, result);
                    AccuracyTest.verifyReceiptTotalScore(scoreSummary);
                    AccuracyTest.calculateFinalScore(scoreSummary);

                    result.setScoreSummary(scoreSummary);
                    result.setOcrStats(ocrStats);

                    DataServiceImpl.insertBatchProcessDetails(result);
                    ocrStatsList.add(ocrStats);
                }
            }
        }
        System.out.println("Ending the batch processing " + new Date());
    }


    public static String doFullTextSearchForLineItems(String lineItem, String superMarketBrand) throws UnknownHostException {

        // removes extra space and replace it with a single space
        lineItem = lineItem.trim().replaceAll(" +", " ");
        String collectionName = "";
        if (superMarketBrand.equalsIgnoreCase(Configs.TESCO_BRAND_NAME)) {
            collectionName = DataServiceImpl.manualDataTescoCollection;
        } else if (superMarketBrand.equalsIgnoreCase(Configs.SAINSBURY_BRAND_NAME)) {
            collectionName = DataServiceImpl.manualDataSaintsCollection;
        } else if (superMarketBrand.equalsIgnoreCase(Configs.NULL_STRING)) {
            // TODO search in both DB's and get the highest result
            List<ManualReceiptLineItem> tescoManualReceiptLineItemList = DataServiceImpl.doFullTextSearchFromManualData(lineItem.trim(), DataServiceImpl.manualDataTescoCollection);
            List<ManualReceiptLineItem> sainsManualReceiptLineItemList = DataServiceImpl.doFullTextSearchFromManualData(lineItem.trim(), DataServiceImpl.manualDataSaintsCollection);


            if (tescoManualReceiptLineItemList.size() != 0 && sainsManualReceiptLineItemList.size() != 0) {

                double tescoScore = tescoManualReceiptLineItemList.get(0).getScore();
                double sainsScore = sainsManualReceiptLineItemList.get(0).getScore();

                if (tescoScore > sainsScore && Configs.FULL_TEXT_THRESHOLD_SCORE <= tescoScore) {
                    return tescoManualReceiptLineItemList.get(0).getTILLROLL_LINE_DESC().trim();
                } else if(Configs.FULL_TEXT_THRESHOLD_SCORE <= sainsScore){
                    return sainsManualReceiptLineItemList.get(0).getTILLROLL_LINE_DESC().trim();
                }
            }
            return lineItem;
        }

        List<ManualReceiptLineItem> manualReceiptLineItemList = DataServiceImpl.doFullTextSearchFromManualData(lineItem.trim(), collectionName);
        if (manualReceiptLineItemList.size() != 0) {
            if (Configs.FULL_TEXT_THRESHOLD_SCORE <= manualReceiptLineItemList.get(0).getScore()) {
                lineItem = manualReceiptLineItemList.get(0).getTILLROLL_LINE_DESC().trim();
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
