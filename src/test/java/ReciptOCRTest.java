import org.bytedeco.javacpp.*;
import org.junit.Test;

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
        getLineItems();
    }

    public static void getLineItems() {
        String poundSymbol = "£";
        String[] inputStrings = {
                "CHOC. ORANGE    x         " + poundSymbol + "1.00"
                , "CHOC.    ORANGE    x         L1.00",
                "NUMBER   I W'xwxmwxxx‘xxx7089  ICC",
                "CHOC. Niro    x         $1.00",
                "CHOC.    ORANGE    x         L1.0"
        };

        String regex = "(?<description>.+)"
                + "\\s{2,}"                             // two or more white space
                + "(?<currency>"+poundSymbol+"|\\w)"    // Pound symbol may be mis-reaad
                + "(?<amount>\\d+\\.\\d{2})";
        Pattern p = Pattern.compile(regex);
        for (String inputString : inputStrings) {
            Matcher m = p.matcher(inputString);
            if (m.find()) {
                String description  = m.group("description");
                String currency     = m.group("currency");
                String amountString = m.group("amount");

                System.out.format("Desciption: %s%n" + "Currency: %s%n" + "Amount: %s%n", description.trim() , currency
                        , amountString);
            }
        }
    }
}