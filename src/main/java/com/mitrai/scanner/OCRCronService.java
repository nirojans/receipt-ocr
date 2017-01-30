package com.mitrai.scanner;

import com.mitrai.scanner.receipt.FinalLineItem;
import com.mitrai.scanner.score.ScoreSummary;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                if (extensionName.equalsIgnoreCase("jpeg") || extensionName.equalsIgnoreCase("jpg") || extensionName.equalsIgnoreCase("png")
                        || extensionName.equalsIgnoreCase("tif") || extensionName.equalsIgnoreCase("tiff")) {

                    System.out.printf("started processing file : " + fileNameWithExtension);
                    List<Receipt> receiptList = performOCRBasedOnProdOrDev(fileNameWithExtension, fileNameWithoutExtension);
                    OCRStats ocrStats = new OCRStats(fileNameWithoutExtension);
                    // read all text
                    receiptList = FileHelper.readAllResultsForAImage(fileNameWithoutExtension);

                    Result result = new Result(fileNameWithExtension);

                    if (receiptList.size() == 0) {
                        result.setStatus(Configs.CANNOT_PROCESS);
                        DataServiceImpl.insertBatchProcessDetails(result);
                        continue;
                    }

                    MasterReceipt masterReceipt = new MasterReceipt(fileNameWithoutExtension);
                    masterReceipt = TemplateEngine.identifySuperMarketName(receiptList, masterReceipt);

                    String superMarketBrand = masterReceipt.getSuperMarketName();
                    result.setSuperMarketName(superMarketBrand);

                    TemplateEngine.identifyLineItems(receiptList);

                    masterReceipt.setReceiptList(receiptList);

                    Receipt highReceipt = receiptList.get(receiptList.size()-1);
                    if (highReceipt.getLineItems().size() == 0) {
                        result.setStatus(Configs.CANNOT_PROCESS);
                        DataServiceImpl.insertBatchProcessDetails(result);
                        continue;
                    }

                    highReceipt = TemplateEngine.removeAppostrofeFromLineItems(highReceipt);

                    // setting raw data to result
                    List<String[]> rawDataList = new ArrayList<>();
                    for (Receipt r : receiptList) {
                        rawDataList.add(r.getRawData());
                    }
                    result.setRawDataList(rawDataList);

                    masterReceipt.setLineItemList(highReceipt.getLineItems());

                    DataServiceImpl.insertIntoDB(masterReceipt);
                    DataServiceImpl.insertLineItemsIntoDB(highReceipt);
                    DataServiceImpl.insertRawDataIntoDB(highReceipt);

                    doFullTextSearchForReceiptTotal(result, superMarketBrand);
                    removeUnwantedOccurrencesFromReceipt(highReceipt);

                    List<ManualReceiptLineItem> selectedManualReceiptLineItemList = new ArrayList<>();

                    // Do the full text search for the Line Items identified with highest accuracy
                    ArrayList<LineItem> fullTextPredictedLineItems = new ArrayList<>();

                    try {
                        for (LineItem item : highReceipt.getLineItems()) {

                            String output = doFullTextSearchForLineItems(item.getDescription(), superMarketBrand);

                            LineItem predictedItems = new LineItem();
                            predictedItems.setValue(item.getValue());
                            predictedItems.setDescription(output);
                            predictedItems.setLineNumber(item.getLineNumber());
                            fullTextPredictedLineItems.add(predictedItems);
                        }
                    } catch (Exception e) {
                        System.out.printf("Exception occurred while doing the full text search for data cleaning");
                    }
                    // set the full text predicted line items to the highly identified receipt
                    highReceipt.setFullTextPredictedLineItems(fullTextPredictedLineItems);

                    // process line item with description value and with out currency symbol
                    try {
                        for (LineItem item : highReceipt.getPossibleLineItems()) {
                            if (StringHelper.regexForDescWithNumbersAndPeriod(item)) {
                                doFullTextSearchForPossibleLineItems(item.getDescription(), highReceipt.getSuperMarketName());
                            } else if (StringHelper.regexForDescAndLongSpace(item)) {
                                doFullTextSearchForPossibleLineItems(item.getDescription(), highReceipt.getSuperMarketName());
                            }else{
                                // TODO implement method to process this string
                            }
                        }
                    } catch (Exception e) {
                        System.out.printf("Exception occurred while doing the full text search for data cleaning");
                    }

                    // get Manual receipt data to compare
                    List<ManualReceiptLineItem> manualTescoReceiptList = DataServiceImpl.getReceiptFromManualData(fileNameWithoutExtension, DataServiceImpl.manualDataTescoCollection);
                    List<ManualReceiptLineItem> manualSainsReceiptList = DataServiceImpl.getReceiptFromManualData(fileNameWithoutExtension, DataServiceImpl.manualDataSaintsCollection);

                    if (manualTescoReceiptList.size() != 0 && manualSainsReceiptList.size() != 0) {
                        System.out.println("Manual record data not found for this file name cannot compare accuracy");
                        result.setStatus("NO MANUAL RECORD FOUND FOR ACCURACY CHECK");
                        List<LineItem> lineItemList = highReceipt.getFullTextPredictedLineItems();
                        for (LineItem item : highReceipt.getPossibleLineItems()) {
                            lineItemList.add(item);
                        }

                        List<FinalLineItem> finalLineItemList = new ArrayList<>();
                        for (LineItem item : lineItemList) {
                            FinalLineItem finalLineItem = new FinalLineItem();
                            finalLineItem.setDescription(item.getDescription());
                            finalLineItem.setValue(item.getValue());
                            finalLineItem.setLineNumber(item.getLineNumber());
                            finalLineItemList.add(finalLineItem);
                        }

                        result.setOCRLineItemList(finalLineItemList);

                        // TODO format text and save to DB
                        DataServiceImpl.insertBatchProcessDetails(result);

                        continue;
                    } else if (manualTescoReceiptList.size() != 0) {
                        selectedManualReceiptLineItemList = manualTescoReceiptList;
                    } else if (manualSainsReceiptList.size() != 0) {
                        selectedManualReceiptLineItemList = manualSainsReceiptList;
                    }

                    result.setManualLineItemList(Utils.getManualFinalLineItemList(selectedManualReceiptLineItemList));
                    ScoreSummary scoreSummary = new ScoreSummary();
                    String receiptTotal = new String(selectedManualReceiptLineItemList.get(0).getTILLROLL_RECORDED_SPEND());

                    AccuracyTest.verifySuperMarketBrand(scoreSummary, masterReceipt.getSuperMarketName(), selectedManualReceiptLineItemList);
                    AccuracyTest.verifyLineItems(scoreSummary, ocrStats, highReceipt, selectedManualReceiptLineItemList, result);
                    AccuracyTest.verifyReceiptTotalScore(result, scoreSummary, receiptTotal);
                    AccuracyTest.calculateFinalScore(scoreSummary);

                    result.setScoreSummary(scoreSummary);
                    result.setOcrStats(ocrStats);
                    result.setFinalScore(scoreSummary.getTotalScore());
                    DataServiceImpl.insertBatchProcessDetails(result);
                    // TODO remove temporary data from DB
                }
            }
        }
        System.out.println("Ending the batch processing " + new Date());
    }

    public static void removeUnwantedOccurrencesFromReceipt(Receipt receipt) throws UnknownHostException {

        String[] totalArray = {"Total" , "sub total"};
        List<LineItem> lineItemsWithTotal = new ArrayList<>();
        // Data cleaning to remove total string occurrences
        for(int i=0;i < totalArray.length;i ++) {
            LineItem fullTextItem = DataServiceImpl.doFullTextSearchForLineItem(totalArray[i], "lineItems");
            if (fullTextItem != null && 1.4 <= fullTextItem.getScore()) {
                // check if already exists
                boolean isExists = false;
                for (LineItem item : lineItemsWithTotal) {
                    if (fullTextItem.getLineNumber() == item.getLineNumber()) {
                        isExists = true;
                    }
                }
                if (!isExists) {
                    lineItemsWithTotal.add(fullTextItem);
                }
            }
        }

        // remove the identified total occurrence from line item list
        List<LineItem> lineItemList = receipt.getLineItems();

        for (LineItem item : lineItemsWithTotal) {
            removeLineItemFromList(lineItemList, item.getLineNumber());
        }
    }

    public static void doFullTextSearchForReceiptTotal(Result result, String superMarketName) throws UnknownHostException {

        // get the total of the receipt
        // If the super market name is tesco
        LineItem totalLineItem = DataServiceImpl.doFullTextSearchForLineItem("TOTAL TO PAY", "rawData");
        // score 1.2
        // TODO refactor this method
        setReceiptTotal(result,totalLineItem,superMarketName);

        if (superMarketName.equalsIgnoreCase(Configs.SAINSBURY_BRAND_NAME)) {
            totalLineItem = DataServiceImpl.doFullTextSearchForLineItem("BALANCE DUE", "rawData");
            setReceiptTotal(result,totalLineItem, superMarketName);
        }
    }

    public static void setReceiptTotal(Result result, LineItem totalLineItem, String superMarketName) {
        if (totalLineItem != null && (1.2 <= totalLineItem.getScore())) {
            boolean totalValueFound = StringHelper.regexForDescWithNumbersAndPeriod(totalLineItem);
            if (superMarketName.equalsIgnoreCase(Configs.SAINSBURY_BRAND_NAME)) {
                totalValueFound = StringHelper.regexForCurrencySymbolAndDecimals(totalLineItem);
            }
            if (!totalValueFound) {
                if (StringHelper.regexForDescWithNumbersOnly(totalLineItem)) {
                    result.setReceiptTotal(totalLineItem.getValue());
                }
                // TOTAL with currency symbol
            } else {
                result.setReceiptTotal(totalLineItem.getValue());
            }
        }
    }

    public static void removeLineItemFromList(List<LineItem> lineItemList, int lineNumber) {
        for(int i=0; i < lineItemList.size(); i++) {
            if (lineItemList.get(i).getLineNumber() == lineNumber) {
                lineItemList.remove(i);
            }
        }
    }

    public static void reorderResutBasedOnLineItemNumber(Result result) {

        List<FinalLineItem> finalLineItemList = result.getOCRLineItemList();
        Collections.sort(finalLineItemList, new Comparator<FinalLineItem>(){
            public int compare(FinalLineItem f1, FinalLineItem f2) {
                return f1.getLineNumber() - f2.getLineNumber();
            }
        });
        result.setOCRLineItemList(finalLineItemList);
    }


    public static String doFullTextSearchForPossibleLineItems(String lineItem, String superMarketBrand) throws UnknownHostException {
        return doFullTextSearchForLineItems(lineItem, superMarketBrand);
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
