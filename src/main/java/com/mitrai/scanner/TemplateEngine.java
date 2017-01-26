package com.mitrai.scanner;

import java.util.*;

/**
 * Created by niro273 on 1/4/17.
 */
public class TemplateEngine {

    public static boolean ASC = true;

    public static Receipt removeAppostrofeFromLineItems(Receipt highReceipt) {

        List<LineItem> lineItemList = highReceipt.getLineItems();
        String description;

        for (LineItem item : lineItemList) {

            description = item.getDescription().replace("'", " ");
            description = description.replace("’", " ");
            description = description.trim().replaceAll(" +", " ");
            item.setDescription(description);

        }

        return highReceipt;
    }

    public static MasterReceipt identifyTemplateProperties(MasterReceipt masterReceipt) {


        return masterReceipt;
    }

    public static List<Receipt> identifyDateOfReceipt(List<Receipt> receiptList) {



        return receiptList;
    }

    public static MasterReceipt identifySuperMarketName(List<Receipt> receiptList, MasterReceipt masterReceipt) {
        Properties properties = Configs.getConfigs(Configs.SUPER_MARKET_TEMPLATE_NAME);

        // Get receipt for each pre processing method
        for (Receipt receipt : receiptList) {
            // Get raw data of each receipt
            String[] ocrResults = receipt.getRawData();

            // Iterate through all template names
            Map<String, Integer> templateScoreMap = new HashMap<>();

            for(String key : properties.stringPropertyNames()) {

                // eg - templateNameIdentifier = TESCO/ clubcard statement
                String templateNameIdentifier = properties.getProperty(key);
                // create an integer array to save the string similarity score and fill with mx 50 value
                int[] scoreArrayForPreProcess = new int[ocrResults.length];
                Arrays.fill(scoreArrayForPreProcess, 50);

                // Iterate through the raw data of the preprocessed receipts
                for(int j=0; j < ocrResults.length; j++) {
                    // If the raw data is twice as big as the template name then do not consider
                    if ((templateNameIdentifier.length() * 2) >= ocrResults[j].length()) {
                        scoreArrayForPreProcess[j] = StringHelper.distance(templateNameIdentifier, ocrResults[j].toLowerCase());
                    }
                }

                // sort the array and get the first element to get the best match score
                Arrays.sort(scoreArrayForPreProcess);
                templateScoreMap.put(key, scoreArrayForPreProcess[0]);

            }

            Map<String, Integer> sortedMapAsc = Utils.sortByComparator(templateScoreMap, ASC);
            Map.Entry<String, Integer> entry = sortedMapAsc.entrySet().iterator().next();
            if (entry.getValue() < 2) {
                String superMarketName = entry.getKey();
                receipt.setSuperMarketName(superMarketName.toLowerCase().split("_")[0]);
            }else {
                receipt.setSuperMarketName(Configs.NULL_STRING);
            }
            receipt.setNameAccuracy(entry.getValue());
        }
        Collections.sort(receiptList, new Comparator<Receipt>(){
            public int compare(Receipt r1, Receipt r2) {
                return r1.getNameAccuracy() - r2.getNameAccuracy();
            }
        });

        // Set the highest accuracy value to the Master Receipt
        masterReceipt.setReceiptList(receiptList);
        masterReceipt.setSuperMarketName(receiptList.get(0).getSuperMarketName());
        masterReceipt.setSuperMarketNameAccuracy(receiptList.get(0).getNameAccuracy());
        return masterReceipt;
    }

    public static void identifyLineItems(List<Receipt> receiptList) {
        String currencySymbol = "£|€|$";

        String regex = "(?<description>.+)\\s{2,}(?<currency>"+currencySymbol+"|\\w)(?<amount>\\d+\\.\\d{2})";
        String strongRegex = "(?<description>.+)\\s{2,}(?<currency>"+currencySymbol+")(?<amount>\\d+\\.\\d{2})";
        for (Receipt r : receiptList) {
            StringHelper.getLineItemsForReceipt(r, strongRegex);
        }

        // This method compares all the line items identified and sorts them in to lowest to highest
        Collections.sort(receiptList, new Comparator<Receipt>(){
            public int compare(Receipt r1, Receipt r2) {
                return r1.getLineItems().size() - r2.getLineItems().size();
            }
        });
    }
}
