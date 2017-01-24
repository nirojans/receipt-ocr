package com.mitrai.scanner;

import java.net.UnknownHostException;
import java.util.*;

import static com.mitrai.scanner.TemplateEngine.ASC;

/**
 * Created by niro273 on 1/21/17.
 */
public class AccuracyTest {

    public static int verifySuperMarketBrand(String brandName, List<ManualReceipt> manualReceiptList) throws UnknownHostException {

        String manualTescoShopName = manualReceiptList.get(0).getSHOP_NAME().toLowerCase();

        if (manualTescoShopName.contains(brandName)) {
            return 100;
        }
        return 0;
    }

    public static void verifySuperMarketName() {

    }

    public static ArrayList<LineItem> verifyLineItems(Receipt receipt, List<ManualReceipt> manualReceiptList) {

        ArrayList<LineItem> lineItems = receipt.getPredictedLineItemFromManualData();
        ArrayList<LineItem> finalLineItems = new ArrayList<>();

        // iterate through manual data
        for(int i=0; i < lineItems.size(); i++) {

            // Iterate through all template names
            LineItem lineItem = lineItems.get(i);
            // key = manualData Line number , Distance
            Map<Integer, Integer> lineItemScoreMap = new HashMap<>();

            for(int x=0; x<manualReceiptList.size(); x++) {
                lineItemScoreMap.put(x,StringHelper.distance(lineItem.getDescription(), manualReceiptList.get(x).getTILLROLL_LINE_DESC()));
            }

            Map<Integer, Integer> sortedMapAsc = Utils.sortByComparatorIntegerMap(lineItemScoreMap, ASC);
            // entry is the best match for the given Items
            Map.Entry<Integer, Integer> entry = sortedMapAsc.entrySet().iterator().next();

//            manualReceiptList.remove(entry.getKey());

            // atleast 50% accuracy is needed
            int length = lineItem.getDescription().length();
            int maxDifference = length / 2;

            int accuracyLevel = entry.getValue();
            int manualDataLineNumber = entry.getValue();

            if (accuracyLevel <= length) {

                lineItem.setManualDataForLineItem(manualDataLineNumber, accuracyLevel,manualReceiptList.get(entry.getKey()));
                int accuracyPercentage = (length - lineItem.getDescriptionAccuracyLevel()) * 100 / length;
                lineItem.setDescriptionAccuracyPercentage(accuracyPercentage);
                // TODO remove the item from the list
//                manualReceiptList.remove(manualDataLineNumber);
                finalLineItems.add(lineItem);
            }
        }

        // check for the value accuracy
        for (LineItem item : finalLineItems) {

            String value = item.getValue();
            value = value.indexOf(".") < 0 ? value : value.replaceAll("0*$", "").replaceAll("\\.$", "");
            item.setValueAccuracyLevel(StringHelper.distance(value, item.getManualDataValue()));
            int valueAccuracyPercentage = (value.length() - item.getValueAccuracyLevel()) * 100 / value.length();
            item.setValueAccuracyPercentage(valueAccuracyPercentage);
        }

        // create a histogram for the results
        int[] descriptionHistogram = new int[10];
        int[] valueHistogram = new int[10];

        for(int i=0; i < finalLineItems.size(); i++) {
            descriptionHistogram[i] = finalLineItems.get(i).getDescriptionAccuracyPercentage();
            valueHistogram[i] = finalLineItems.get(i).getValueAccuracyPercentage();
        }

        int[] histogramDesc = histogram(0, 100, 10, 10, descriptionHistogram);
        int[] histogramValue = histogram(0, 100, 10, 10, valueHistogram);

        return finalLineItems;
    }

    public static int[] histogram(final int min, final int max, final int bucket, final int count, int[] array) {
        // note, in this method, there's no need to know the actual values, just the range.
        final int range = max - min + 1;
        final int buckets = (range + bucket - 1) / bucket;
        final int[] results = new int[buckets];
        for (int i = 0; i < array.length; i++) {
            int value = array[i];
            results[value / bucket]++;
        }
        return results;
    }

    public static int calculateAccuracyForDescription() {


        return 10;
    }

    public static void verifyDate() {

    }
}
