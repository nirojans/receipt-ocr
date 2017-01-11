package com.mitrai.scanner;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertTrue;

/**
 * Created by niro273 on 1/4/17.
 */
public class TesseractEngine {

    public static String TerminalImplementation(String command) throws InterruptedException, IOException {

        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (InterruptedException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return output.toString().isEmpty() ? "success" : "fail";
    }

    public static String getCommandForTesseract(String fileName, String scriptName) {
        String command = "sh scripts/" + scriptName + " " + FileHelper.rawFolderPath + fileName + " " +
                FilenameUtils.removeExtension(fileName) + " " + FileHelper.preprocessedFolderPath + " " + FileHelper.resultsFolderPath + " " + FileHelper.archivedFolderPath;
        System.out.println(command);
        return command;
    }


}
