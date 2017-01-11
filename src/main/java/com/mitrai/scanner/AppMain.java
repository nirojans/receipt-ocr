package com.mitrai.scanner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AppMain {

    public static void main(String[] args) throws IOException, InterruptedException {

        // Initiate all the folders for batch processing
        FileHelper.initBaseFolder();
        File[] listOfFiles = FileHelper.getAllFileNames();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();
                String extension = FileHelper.getFileExtension(listOfFiles[i]);
                TesseractEngine.TerminalImplementation(FileHelper.rawFolderPath, fileName, extension);
            }
        }
    }
}
