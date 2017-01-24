package com.mitrai.scanner;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by niro273 on 1/4/17.
 */
public class TesseractEngine {

    public static String method1ScriptName = "text_clean_resize.sh";
    public static String method2ScriptName = "thresh_sharp.sh";
    public static String method3ScriptName = "fill_sharp_clean.sh";

    public static String moveFileScriptName = "move_raw_image_to_archive.sh";

    public static void performPreProcessingAndOCR(String fileName) throws IOException, InterruptedException {
        TesseractEngine.TerminalImplementation(TesseractEngine.getCommandForTesseract(fileName, method1ScriptName));
        TesseractEngine.TerminalImplementation(TesseractEngine.getCommandForTesseract(fileName, method2ScriptName));
        TesseractEngine.TerminalImplementation(TesseractEngine.getCommandForTesseract(fileName, method3ScriptName));

        // If dev environment keep the files in the raw images with out moving
        // If move to raw is set to true give high priority for that
        if (Configs.moveToArchiveStatus.equalsIgnoreCase("true") || FileHelper.isProd) {
            TesseractEngine.TerminalImplementation(TesseractEngine.getCommandToMoveImageToArchive(fileName, moveFileScriptName));
        }
    }

    /*
    This method invokes a script via the command line
     */
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
            System.out.println(output);
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

    public static String getCommandToMoveImageToArchive(String fileName, String scriptName) {
        String command = "sh scripts/" + scriptName + " " + FileHelper.rawFolderPath + fileName + " " + FileHelper.archivedFolderPath;
        System.out.println(command);
        return command;
    }
}
