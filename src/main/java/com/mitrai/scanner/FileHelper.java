package com.mitrai.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by niro273 on 1/4/17.
 */
public class FileHelper {

    private static String raw_image_folder_name = "raw_images";
    private static String archived_image_folder_name = "archived_images";
    private static String preprocessed_image_folder_name = "preprocessed_images";
    private static String results_folder_name = "results";

    public static String baseFolder = "";
    public static String baseFolderPath = "";
    public static String rawFolderPath = "";
    public static String archivedFolderPath = "";
    public static String preprocessedFolderPath = "";
    public static String resultsFolderPath = "";

    static {
        Properties properties = Configs.getFolderNames();
        baseFolderPath = properties.getProperty("base_folder");
        baseFolderPath = properties.getProperty("base_folder_path");
        rawFolderPath = properties.getProperty("raw_image_folder_path");
        archivedFolderPath = properties.getProperty("archived_image_folder_path");
        preprocessedFolderPath = properties.getProperty("preprocessed_image_folder_path");
        resultsFolderPath = properties.getProperty("results_folder_path");
    }

    public static BufferedReader readFile(String fileLocation) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileLocation));
//        String line;
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }
        return br;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static File[] getAllFileNames() {
        File folder = new File(baseFolderPath + raw_image_folder_name);
        File[] listOfFiles = folder.listFiles();

//        for (int i = 0; i < listOfFiles.length; i++) {
//            if (listOfFiles[i].isFile()) {
//                System.out.println("File " + listOfFiles[i].getName());
//            } else if (listOfFiles[i].isDirectory()) {
//                System.out.println("Directory " + listOfFiles[i].getName());
//            }
//        }
        return listOfFiles;
    }

    public static void initBaseFolder() {
        File file = new File(baseFolderPath);
        File raw_image_directory = new File(baseFolderPath + raw_image_folder_name);
        File archived_image_directory = new File(baseFolderPath + archived_image_folder_name);
        File preprocessed_image_directory = new File(baseFolderPath + preprocessed_image_folder_name);
        File results_directory = new File(baseFolderPath + results_folder_name);

        if (!file.exists()) {
            file.mkdirs();
            raw_image_directory.mkdirs();
            archived_image_directory.mkdirs();
            preprocessed_image_directory.mkdirs();
            results_directory.mkdirs();
        }

    }
}
