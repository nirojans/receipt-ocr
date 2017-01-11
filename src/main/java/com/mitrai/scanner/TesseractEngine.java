package com.mitrai.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertTrue;

/**
 * Created by niro273 on 1/4/17.
 */
public class TesseractEngine {

    public static String TerminalImplementation(String folderPath, String imageName, String extension) throws InterruptedException, IOException {

        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec("tesseract " + folderPath + imageName + " " + folderPath + File.separator + "out");
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
        //FileHelper.readFile();
        return output.toString().isEmpty() ? "success" : "fail";
    }
}
