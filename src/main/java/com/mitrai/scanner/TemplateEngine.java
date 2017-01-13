package com.mitrai.scanner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

/**
 * Created by niro273 on 1/4/17.
 */
public class TemplateEngine {


    public static List<Receipt> identifySuperMarketName(List<Receipt> receiptList) {
        Properties properties = Configs.getConfigs(Configs.SUPER_MARKET_TEMPLATE_NAME);

        for (Receipt receipt : receiptList) {

            String[] ocrResults = receipt.getRawData();
            for(String key : properties.stringPropertyNames()) {
                String templateName = properties.getProperty(key);
                int[] scoreArrayForPreProcess = new int[ocrResults.length];
                Arrays.fill(scoreArrayForPreProcess, 50);

                for(int j=0; j < ocrResults.length; j++) {
                    if ((templateName.length() * 2) >= ocrResults[j].length()) {
                        scoreArrayForPreProcess[j] = StringHelper.distance(templateName, ocrResults[j].toLowerCase());
                    }
                }
                receipt.setSuperMarketName(templateName);
                Arrays.sort(scoreArrayForPreProcess);
                receipt.setNameRecognitionRank(scoreArrayForPreProcess[0]);
            }
        }
        return receiptList;
    }


    public static String getRetaurantName() {
        try {
            String[] br = FileHelper.readFile("images/sharp_clean_resize_result.txt");

            // Run a for loop, until we reach the Restaurant Name or the Max lines

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Not Found";
    }


    public static void identifyLineItems(List<Receipt> receiptList) {
        String currencySymbol = "£|€";

        String regex = "(?<description>.+)\\s{2,}(?<currency>"+currencySymbol+"|\\w)(?<amount>\\d+\\.\\d{2})";
        for (Receipt r : receiptList) {
            StringHelper.getLineItemsForReceipt(r, regex);
        }

        // This method compares all the line items identified and sorts them in to lowest to highest
        Collections.sort(receiptList, new Comparator<Receipt>(){
            public int compare(Receipt r1, Receipt r2) {
                return r1.getLineItems().size() - r2.getLineItems().size();
            }
        });
    }
}
