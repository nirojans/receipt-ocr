package com.mitrai.scanner;

import java.io.IOException;
import java.util.*;

/**
 * Created by niro273 on 1/4/17.
 */
public class TemplateEngine {

    public static MasterReceipt identifyTemplateProperties(MasterReceipt masterReceipt) {


        return masterReceipt;
    }

    public static List<Receipt> identifyDateOfReceipt(List<Receipt> receiptList) {



        return receiptList;
    }


    public static List<Receipt> identifySuperMarketName(List<Receipt> receiptList) {
        Properties properties = Configs.getConfigs(Configs.SUPER_MARKET_TEMPLATE_NAME);

        // Get receipt for each pre processing method
        for (Receipt receipt : receiptList) {
            // Get raw data of each receipt
            String[] ocrResults = receipt.getRawData();

            // Iterate through all template names
            Map<String, Integer> templateScoreMap = new HashMap<>();

            for(String key : properties.stringPropertyNames()) {

                // eg - templateName = TESCO
                String templateName = properties.getProperty(key);
                // create an integer array to save the string similarity score and fill with mx 50 value
                int[] scoreArrayForPreProcess = new int[ocrResults.length];
                Arrays.fill(scoreArrayForPreProcess, 50);

                // Iterate through the raw data of the preprocessed receipts
                for(int j=0; j < ocrResults.length; j++) {
                    // If the raw data is twice as big as the template name then do not consider
                    if ((templateName.length() * 2) >= ocrResults[j].length()) {
                        scoreArrayForPreProcess[j] = StringHelper.distance(templateName, ocrResults[j].toLowerCase());
                    }
                }


                // sort the array and get the first element to get the best match score
                Arrays.sort(scoreArrayForPreProcess);
                templateScoreMap.put(templateName, scoreArrayForPreProcess[0]);

            }
            receipt.setSuperMarketName("Template Name");
        }
        return receiptList;
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
