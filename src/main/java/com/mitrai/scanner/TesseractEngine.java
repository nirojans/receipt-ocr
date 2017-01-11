package com.mitrai.scanner;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.junit.Assert.assertTrue;

/**
 * Created by niro273 on 1/4/17.
 */
public class TesseractEngine {

    public static String wraperImplementation() {
        BytePointer outText;

        tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (api.Init(".", "ENG") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        // Open input image with leptonica library
        lept.PIX image = pixRead("test.png");
        api.SetImage(image);
        // Get OCR result
        outText = api.GetUTF8Text();
        String string = outText.getString();
        assertTrue(!string.isEmpty());
        System.out.println("OCR output:\n" + string);

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);

        return string;
    }

    public static String TerminalImplementation() throws InterruptedException, IOException {

        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec("sh text_clean_resize.sh images/dark_back_clear_receipt.jpeg");
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
