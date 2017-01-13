package com.mitrai.scanner;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by niro273 on 1/4/17.
 */
public class FileHelper {

    public static String baseFolder = "";
    public static String baseFolderPath = "";
    public static String rawFolderPath = "";
    public static String archivedFolderPath = "";
    public static String preprocessedFolderPath = "";
    public static String resultsFolderPath = "";

    static {
        Properties properties = Configs.getConfigs(Configs.CONFIG_FILE_NAME);
        baseFolderPath = properties.getProperty("base_folder");
        baseFolderPath = properties.getProperty("base_folder_path");
        rawFolderPath = baseFolderPath + properties.getProperty("raw_image_folder_path");
        archivedFolderPath = baseFolderPath + properties.getProperty("archived_image_folder_path");
        preprocessedFolderPath = baseFolderPath + properties.getProperty("preprocessed_image_folder_path");
        resultsFolderPath = baseFolderPath + properties.getProperty("results_folder_path");
    }

    public static String[] readFile(String fileLocation) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileLocation));
        String line;
        ArrayList<String> stringArrayList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            stringArrayList.add(line.trim());
        }
        return stringArrayList.toArray(new String[stringArrayList.size()]);
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(0, name.lastIndexOf("."));
        } catch (Exception e) {
            return "";
        }
    }

    public static File[] getAllFileNames() {
        File folder = new File(rawFolderPath);
        return folder.listFiles();
    }

    public static void initBaseFolder() {
        File baseFolder = new File(baseFolderPath);
        File raw_image_directory = new File(rawFolderPath);
        File archived_image_directory = new File(archivedFolderPath);
        File preprocessed_image_directory = new File(preprocessedFolderPath);
        File results_directory = new File(resultsFolderPath);

        if (!baseFolder.exists()) {
            System.out.printf("Base folder not found. Initializing base folder");
            baseFolder.mkdirs();
            raw_image_directory.mkdirs();
            archived_image_directory.mkdirs();
            preprocessed_image_directory.mkdirs();
            results_directory.mkdirs();
        } else {
            if (!raw_image_directory.exists()) {
                raw_image_directory.mkdirs();
            }
            if (!archived_image_directory.exists()) {
                archived_image_directory.mkdirs();
            }
            if (!preprocessed_image_directory.exists()) {
                preprocessed_image_directory.mkdirs();
            }
            if (!results_directory.exists()) {
                results_directory.mkdirs();
            }
        }
    }


    public static List<Receipt> readAllResultsForAImage(String fileName) {
        List<Receipt> receiptList = new ArrayList<>();
        try {
            for (int i=1;i<4;i++) {
                String nameOfTheFile = FilenameUtils.removeExtension(fileName) + "_" + i;
                Receipt receipt = new Receipt();
                receipt.setRawData(FileHelper.readFile(FileHelper.resultsFolderPath + FilenameUtils.removeExtension(fileName) + "_" + i));
                receipt.setPreprocessMethod(i);
                receipt.setId(nameOfTheFile);
                receiptList.add(receipt);
            }
        } catch (Exception e) {
            System.out.println("File not found for the OCR's results");
        }
        return receiptList;
    }

    public static void writeResultsToFile(Receipt receipt, String resultsFileName) {
        String FILENAME = FileHelper.resultsFolderPath + resultsFileName;
        String content = "Super Market Name : " + receipt.getSuperMarketName() + " \n";

        for (LineItem i : receipt.getLineItems()) {
            content +=  "Description : " + i.getDescription() + ". symbol : " + i.getCurrencySymbol() + ". value : "
                    + i.getValue() + " Line number" + i.getLineNumber() + " \n";
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
