package com.mitrai.scanner;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
                    List<String[]> ocrResultsArrayList = getAllOCRResults(FileHelper.getFileNameWithoutExtension(listOfFiles[i]));
                    List<Receipt> receiptList = new ArrayList<>();
                    identifySuperMarketName(receiptList, ocrResultsArrayList);


                    // process receipt for restaurant name

                    // process receipt for line items

                    // process receipt for date




                }
            }
        }
    }

    public static List<Receipt> identifySuperMarketName(List<Receipt> receiptList, List<String[]> stringsArrayList) {
        Properties properties = Configs.getConfigs(Configs.SUPER_MARKET_TEMPLATE_NAME);

        for (String[] ocrResults : stringsArrayList) {

            for(String key : properties.stringPropertyNames()) {
                String templateName = properties.getProperty(key);
                int[] scoreArray = new int[ocrResults.length];

                for(int j=0; j < ocrResults.length; j++) {
                    if ((templateName.length() * 2) <= ocrResults[j].length()) {
                        scoreArray[j] = StringHelper.distance(templateName, ocrResults[j].toLowerCase());
                    }
                }
                Receipt receipt = new Receipt();
                receipt.setRestaurantName(templateName);
                Arrays.sort(scoreArray);
                receipt.setNameRecognitionRank(scoreArray[0]);

                receiptList.add(receipt);
            }
        }

        return receiptList;
    }

    public static List<String[]> getAllOCRResults(String fileName) {

        List<String[]> stringArrayList = new ArrayList<>();

        try {
            for (int i=1;i<4;i++) {
                stringArrayList.add(FileHelper.readFile(FileHelper.resultsFolderPath + FilenameUtils.removeExtension(fileName) + "_" + i));
            }
        } catch (Exception e) {
            System.out.println("File not found for the OCR's results");
        }

        return stringArrayList;
    }

    public static void performPreProcessingAndOCR(String fileName) throws IOException, InterruptedException {
        TesseractEngine.TerminalImplementation(TesseractEngine.getCommandForTesseract(fileName, method1ScriptName));
        TesseractEngine.TerminalImplementation(TesseractEngine.getCommandForTesseract(fileName, method2ScriptName));
        TesseractEngine.TerminalImplementation(TesseractEngine.getCommandForTesseract(fileName, method3ScriptName));
    }
}
