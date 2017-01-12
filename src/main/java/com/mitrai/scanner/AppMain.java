package com.mitrai.scanner;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static javax.script.ScriptEngine.FILENAME;
import static org.junit.Assert.assertTrue;

public class AppMain {

    public static String method1ScriptName = "text_clean_resize.sh";
    public static String method2ScriptName = "thresh_sharp.sh";
    public static String method3ScriptName = "fill_sharp_clean.sh";

    public static void main(String[] args) throws IOException, InterruptedException {
//        FileUtils.deleteDirectory(new File(FileHelper.baseFolder));
        // Initiate all the folders for batch processing
        FileHelper.initBaseFolder();
        File[] listOfFiles = FileHelper.getAllFileNames();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();
                // check if it is a valid input
                String extensionName = FileHelper.getFileExtension(listOfFiles[i]);
                System.out.println("extension name is " + extensionName);
                if (extensionName.equalsIgnoreCase("jpeg") || extensionName.equalsIgnoreCase("jpg") || extensionName.equalsIgnoreCase("png")) {
//                    performPreProcessingAndOCR(fileName);

                    // read all text
                    List<Receipt> receiptList = getAllOCRResults(FileHelper.getFileNameWithoutExtension(listOfFiles[i]));
                    receiptList = identifySuperMarketName(receiptList);
                    identifyLineItems(receiptList);

                    // process receipt for restaurant name

                    // process receipt for line items

                    // process receipt for date




                }
            }
        }
    }

    public static void writeResultsToFile(Receipt receipt) {
        String content = "";
        String FILENAME = FileHelper.resultsFolderPath + "results.txt";

        content = "Super Market Name : " + receipt.getRestaurantName() + " \n";

        for (LineItem i : receipt.getLineItemses()) {
            content +=  "Description : " + i.getDescription() + ". symbol : " + i.getCurrencySymbol() + ". value : "
                    + i.getValue() + " Line number" + i.getLineNumber() + " \n";
        }


        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void identifyLineItems(List<Receipt> receiptList) {
        String poundSymbol = "£|€";
        String regex = "(?<description>.+)"
                + "\\s{2,}"                             // two or more white space
                + "(?<currency>"+poundSymbol+"|\\w)"    // Pound symbol may be mis-reaad
                + "(?<amount>\\d+\\.\\d{2})";
        for (Receipt r : receiptList) {
            r = StringHelper.getLineItemsForReceipt(r, regex);
            String name = " niro";
        }


        Collections.sort(receiptList, new Comparator<Receipt>(){
            public int compare(Receipt r1, Receipt r2) {
                return r1.getLineItemses().size() - r2.getLineItemses().size();
            }
        });

        Receipt highReceipt = receiptList.get(receiptList.size()-1);
        writeResultsToFile(highReceipt);
    }

    public static String finalizeSuperMartketName() {


        return "";
    }

    public static List<Receipt> identifySuperMarketName(List<Receipt> receiptList) {
        Properties properties = Configs.getConfigs(Configs.SUPER_MARKET_TEMPLATE_NAME);

        for (Receipt receipt : receiptList) {

            String[] ocrResults = receipt.getRawData();
            for(String key : properties.stringPropertyNames()) {
                String templateName = properties.getProperty(key);
                int[] scoreArrayForPreProcess = new int[ocrResults.length];
                Arrays.fill(scoreArrayForPreProcess, 50);

                for(int j=0; j < ocrResults.length; j++) {
                    if ((templateName.length() * 2) >= ocrResults[j].length()) {
                        scoreArrayForPreProcess[j] = StringHelper.distance(templateName, ocrResults[j].toLowerCase());
                    }
                }
                receipt.setRestaurantName(templateName);
                Arrays.sort(scoreArrayForPreProcess);
                receipt.setNameRecognitionRank(scoreArrayForPreProcess[0]);
            }
        }
        return receiptList;
    }

    public static List<Receipt> getAllOCRResults(String fileName) {

        List<Receipt> receiptList = new ArrayList<>();
//        List<String[]> stringArrayList = new ArrayList<>();

        try {
            for (int i=1;i<4;i++) {
//                stringArrayList.add(FileHelper.readFile(FileHelper.resultsFolderPath + FilenameUtils.removeExtension(fileName) + "_" + i));
                Receipt receipt = new Receipt();
                receipt.setRawData(FileHelper.readFile(FileHelper.resultsFolderPath + FilenameUtils.removeExtension(fileName) + "_" + i));
                receipt.setPreprocessMethod(i);
                receiptList.add(receipt);
            }
        } catch (Exception e) {
            System.out.println("File not found for the OCR's results");
        }

        return receiptList;
    }

    public static void performPreProcessingAndOCR(String fileName) throws IOException, InterruptedException {
        TesseractEngine.TerminalImplementation(TesseractEngine.getCommandForTesseract(fileName, method1ScriptName));
        TesseractEngine.TerminalImplementation(TesseractEngine.getCommandForTesseract(fileName, method2ScriptName));
        TesseractEngine.TerminalImplementation(TesseractEngine.getCommandForTesseract(fileName, method3ScriptName));
    }
}
