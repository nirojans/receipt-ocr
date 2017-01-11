import org.bytedeco.javacpp.*;
import org.junit.Test;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;
import static org.junit.Assert.assertTrue;

public class ReciptOCRTest {
    
    @Test
    public void givenTessBaseApi_whenImageOcrd_thenTextDisplayed() throws Exception {
//        TesseractEngine.TerminalImplementation();
//        System.out.print("hello");
//        getLineItems();
        System.out.print("The results for " + StringHelper.distance("TESCO","tesco"));
    }
}