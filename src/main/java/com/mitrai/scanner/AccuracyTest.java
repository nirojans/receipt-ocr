package com.mitrai.scanner;

import java.net.UnknownHostException;
import java.util.*;

import static com.mitrai.scanner.TemplateEngine.ASC;

/**
 * Created by niro273 on 1/21/17.
 */
public class AccuracyTest implements Cloneable {

    public static int verifySuperMarketBrand(String brandName, List<ManualReceipt> manualReceiptList) throws UnknownHostException {

        String manualTescoShopName = manualReceiptList.get(0).getSHOP_NAME().toLowerCase();

        if (manualTescoShopName.contains(brandName)) {
            return 100;
        }
        return 0;
    }

    public static LineItem identifyAndRemoveFromManualList(List<ManualReceipt> manualReceiptList, LineItem lineItem) {

        Map<Integer, Integer> lineItemScoreMap = new HashMap<>();

        // x = manual data line items number
        for(int x=0; x<manualReceiptList.size(); x++) {
            try{
                String manualDataDescription = manualReceiptList.get(x).getTILLROLL_LINE_DESC();
                lineItemScoreMap.put(x,StringHelper.distance(lineItem.getDescription(), manualDataDescription));
            }catch (Exception e){
                System.out.println("Array index out of bound exception occurred because of the Manula Data List");
            }
        }

        Map<Integer, Integer> sortedMapAsc = Utils.sortByComparatorIntegerMap(lineItemScoreMap, ASC);
        Map.Entry<Integer, Integer> entry = sortedMapAsc.entrySet().iterator().next();

        int length = lineItem.getDescription().length();
        // TODO compute for 80% line items accuracy
        int maxDifference = length / 2;

        int accuracyLevel = entry.getValue();
        int manualDataLineNumber = entry.getKey();

        if (accuracyLevel <= maxDifference) {

            lineItem.setManualDataForLineItem(manualDataLineNumber, accuracyLevel, manualReceiptList.get(entry.getKey()));
            int accuracyPercentage = (length - lineItem.getDescriptionAccuracyLevel()) * 100 / length;
            lineItem.setDescriptionAccuracyPercentage(accuracyPercentage);

            manualReceiptList.remove(manualDataLineNumber);
        } else {
            return null;
        }

        return lineItem;
    }

    public static ArrayList<LineItem> verifyLineItems(Receipt receipt, List<ManualReceipt> manualReceiptList) {

        ArrayList<LineItem> predictedLineItems = receipt.getPredictedLineItemFromManualData();
        ArrayList<LineItem> finalLineItems = new ArrayList<>();

        int size = manualReceiptList.size();

        // iterate through manual data
        for(int i=0; i < predictedLineItems.size(); i++) {
            // key = manualData Line number , Distance
            try {
                LineItem lineItem = predictedLineItems.get(i);
                // TODO handle manual receipt list empty scenario

                // null returned when no match is found from the manual data set
                LineItem item = identifyAndRemoveFromManualList(manualReceiptList, lineItem);
                if (item != null) {
                    finalLineItems.add(item);
                }
            } catch (Exception e) {
                System.out.println("Array index out of bound exception");
            }
        }

        // check for the value accuracy
        for (LineItem finalLineItem : finalLineItems) {
            try {
                String value = finalLineItem.getValue();
                // This removes the trailing zeros from the value (Because the excel sheet does not have trailing zeros)
                value = value.indexOf(".") < 0 ? value : value.replaceAll("0*$", "").replaceAll("\\.$", "");
                finalLineItem.setValueAccuracyLevel(StringHelper.distance(value, finalLineItem.getManualDataValue()));
                int valueAccuracyPercentage = (value.length() - finalLineItem.getValueAccuracyLevel()) * 100 / value.length();
                finalLineItem.setValueAccuracyPercentage(valueAccuracyPercentage);
                finalLineItem.setValue(value);
            } catch (Exception e) {
                System.out.printf("Exception");
            }
        }

        // create a histogram for the results
        int[] descriptionHistogram = new int[finalLineItems.size()];
        int[] valueHistogram = new int[finalLineItems.size()];

        for (int i = 0; i < finalLineItems.size(); i++) {
            descriptionHistogram[i] = finalLineItems.get(i).getDescriptionAccuracyPercentage();
            valueHistogram[i] = finalLineItems.get(i).getValueAccuracyPercentage();
        }



        int[] histogramDesc = histogram(0, 100, 10, 10, descriptionHistogram);
        int[] histogramValue = histogram(0, 100, 10, 10, valueHistogram);

        // add the missed items to the accuracy test
        histogramDesc[0] = histogramDesc[0] + manualReceiptList.size();
        histogramValue[0] = histogramValue[0] + manualReceiptList.size();

        List<String[]> stringList = finalResultsInStringArray(finalLineItems);
        return finalLineItems;
    }

    public static List<String[]> finalResultsInStringArray(List<LineItem> finalLineItems){

        List<String[]> stringList = new ArrayList<>();
        for (LineItem item : finalLineItems) {
            String desc = item.getDescription() + " | " + item.getManualDataDescription() + " | " +
                    String.valueOf(item.getDescriptionAccuracyPercentage());
            String value = item.getValue() + " | " + item.getManualDataValue() + " | " + String.valueOf(item.getValueAccuracyPercentage());
            String[] array = {desc, value};
            stringList.add(array);
        }
        return stringList;
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
}
