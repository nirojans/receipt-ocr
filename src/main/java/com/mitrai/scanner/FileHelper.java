package com.mitrai.scanner;

import org.apache.commons.io.FileUtils;
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
import java.util.Random;

import static com.mitrai.scanner.Configs.maxFileSize;

/**
 * Created by niro273 on 1/4/17.
 */
public class FileHelper {

    public static String baseFolderPath = "";
    public static String rawFolderPath = "";
    public static String archivedFolderPath = "";
    public static String preprocessedFolderPath = "";
    public static String resultsFolderPath = "";
    public static String receiptsFolderPath = "";

    public static String tescoReceiptsFolderPath = "";
    public static String sainsburyReceiptsFolderPath = "";

    public static boolean isProd = false;


    public static int maxRandomFileCount = 10;

    static {
        Properties properties = Configs.getConfigs(Configs.CONFIG_FILE_NAME);
        if (properties.getProperty("production").equals("true")) {
            isProd = true;
            baseFolderPath = properties.getProperty("base_folder_path_prod");
        } else {
            baseFolderPath = properties.getProperty("base_folder_path_dev");
        }
        rawFolderPath = baseFolderPath + properties.getProperty("raw_image_folder_path");
        archivedFolderPath = baseFolderPath + properties.getProperty("archived_image_folder_path");
        preprocessedFolderPath = baseFolderPath + properties.getProperty("preprocessed_image_folder_path");
        resultsFolderPath = baseFolderPath + properties.getProperty("results_folder_path");

        tescoReceiptsFolderPath = baseFolderPath + properties.getProperty("tesco_receipts_folder_path");
        sainsburyReceiptsFolderPath = baseFolderPath + properties.getProperty("sainsbury_receipts_folder_path");

        receiptsFolderPath = properties.getProperty("receipts_folder_path");
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
                // Fix for array index out of bound problem
                if (receipt.getRawData().length == 0) {
                    continue;
                }
                receipt.setPreprocessMethod(i);
                receipt.setId(nameOfTheFile);
                receiptList.add(receipt);
            }
        } catch (Exception e) {
//            System.out.println("File not found for the OCR's results");
        }
        return receiptList;
    }

    //  FileHelper.writeResultsToFile(highReceipt, fileNameWithoutExtension + "_results.txt");
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

    public static List<File> selectRandomReceipts(String receiptFolderPathForRandomProcessing) {

        List<File> fileList = new ArrayList<>();

        File folder = new File(receiptFolderPathForRandomProcessing);
        File[] files = folder.listFiles();

        if (files != null) {
            for (int i=1; i<files.length; i++) {

                int random = new Random().nextInt(files.length);
                File file = files[random];
                if(file.exists()){

                    double bytes = file.length();
                    double kilobytes = (bytes / 1024);

                    if (maxFileSize < kilobytes) {
                        fileList.add(file);
                    }
                    if (fileList.size() == maxRandomFileCount) {
                        break;
                    }
                }
            }
        }
        return fileList;
    }

    public static void copySelectedReceiptsToRawImageFolder(List<File> fileList) {

        for (File sourceFile : fileList) {
            String fileName = sourceFile.getName();
            File dest = new File(rawFolderPath + fileName);

            try {
                FileUtils.copyFile(sourceFile, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
