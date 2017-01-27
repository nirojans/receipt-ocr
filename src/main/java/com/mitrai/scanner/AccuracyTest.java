package com.mitrai.scanner;

import com.mitrai.scanner.score.LineScore;
import com.mitrai.scanner.score.ScoreSummary;

import java.net.UnknownHostException;
import java.util.*;

import static com.mitrai.scanner.TemplateEngine.ASC;

/**
 * Created by niro273 on 1/21/17.
 */
public class AccuracyTest implements Cloneable {

    public static ScoreSummary calculateLineItemScore(ScoreSummary scoreSummary, int[] descriptionAccuracyArray, int[] valueAccuracyArray, int totalLineItems){

        // calculate line item accuracy
        List<LineScore> lineScoreList = new ArrayList<>();
        double totalScore = 0;

        for(int i=0;i< descriptionAccuracyArray.length; i++) {

//            double descriptionScore = (descriptionAccuracyArray[i] * Configs.LINE_ITEMS_DESC_WEIGHT) / 100;
//            double valueScore = (valueAccuracyArray[i] * Configs.LINE_ITEMS_VAL_WEIGHT) / 100;

            // ((description *  desc weight) + (value * val weight))/100
            double lineItemScore = ((descriptionAccuracyArray[i] * Configs.LINE_ITEMS_DESC_WEIGHT) + (valueAccuracyArray[i] * Configs.LINE_ITEMS_VAL_WEIGHT))/100;
            double lineItemWeightedScore = lineItemScore * Configs.LINE_ITEMS_WEIGHT/100;

            totalScore += lineItemWeightedScore;
            LineScore lineScorePoint = new LineScore();

            lineScorePoint.setDesc(String.valueOf(descriptionAccuracyArray[i]));
            lineScorePoint.setValue(String.valueOf(valueAccuracyArray[i]));

            lineScorePoint.setTotalScore(String.valueOf((int)lineItemWeightedScore));
            lineScoreList.add(lineScorePoint);
        }

        scoreSummary.setLineItemScore((int)(totalScore / totalLineItems));
        scoreSummary.setLineLineScoreList(lineScoreList);
        return scoreSummary;
    }

    public static ScoreSummary calculateFinalScore(ScoreSummary scoreSummary) {

        int totalScore = 0;

        totalScore += scoreSummary.getSuperMarketNameScore();
        totalScore += scoreSummary.getReceiptTotalScore();
        totalScore += scoreSummary.getLineItemScore();

        scoreSummary.setTotalScore(totalScore);
        return scoreSummary;
    }

    public static ScoreSummary verifyReceiptTotalScore(ScoreSummary scoreSummary) {

        scoreSummary.setReceiptTotalScore(0);
        return scoreSummary;
    }

    public static ScoreSummary verifySuperMarketBrand(ScoreSummary scoreSummary, String brandName, List<ManualReceiptLineItem> manualReceiptLineItemList) throws UnknownHostException {

        String manualTescoShopName = manualReceiptLineItemList.get(0).getSHOP_NAME().toLowerCase();
        if (manualTescoShopName.contains(brandName)) {
            scoreSummary.setSuperMarketNameScore(Configs.MARKETNAME_SCORE_WEIGHT);
        } else {
            scoreSummary.setSuperMarketNameScore(0);
        }
        return scoreSummary;
    }

    public static LineItem identifyAndRemoveFromManualList(List<ManualReceiptLineItem> manualReceiptLineItemList, LineItem lineItem) {

        Map<Integer, Integer> lineItemScoreMap = new HashMap<>();

        // x = manual data line items number
        for(int x = 0; x< manualReceiptLineItemList.size(); x++) {
            try{
                String manualDataDescription = manualReceiptLineItemList.get(x).getTILLROLL_LINE_DESC();
                lineItemScoreMap.put(x,StringHelper.distance(lineItem.getDescription(), manualDataDescription));
            }catch (Exception e){
                System.out.println("Array index out of bound exception occurred because of the Manual Data List");
            }
        }

        Map<Integer, Integer> sortedMapAsc = Utils.sortByComparatorIntegerMap(lineItemScoreMap, ASC);
        Map.Entry<Integer, Integer> entry = sortedMapAsc.entrySet().iterator().next();

        int length = lineItem.getDescription().length();
        // TODO compute for 80% line items accuracy
        int maxDifference = length /2;

        int accuracyLevel = entry.getValue();
        int manualDataLineNumber = entry.getKey();

        if (accuracyLevel <= maxDifference) {

            lineItem.setManualDataForLineItem(manualDataLineNumber, accuracyLevel, manualReceiptLineItemList.get(entry.getKey()));
            int cal = (length - lineItem.getDescriptionAccuracyLevel());
            int accuracyPercentage = 0;

            if (cal > 0) {
                accuracyPercentage = cal * 100 / length;
            }

            // TODO if below 30 also does not add
            lineItem.setDescriptionAccuracyPercentage(accuracyPercentage);
            lineItem.setManualDatalineNumber(manualDataLineNumber);

            manualReceiptLineItemList.remove(manualDataLineNumber);
            return lineItem;
        } else {
            return null;
        }
    }

    public static void calculatePriceAccuracyToDescIdentidiedLineItems(List<LineItem> identifiedLineItemList) {

        for (LineItem identifiedItem : identifiedLineItemList) {
            try {
                if (identifiedItem.getValue() != null) {

                    String value = identifiedItem.getValue();
                    // This removes the trailing zeros from the value (Because the excel sheet does not have trailing zeros)
                    value = value.indexOf(".") < 0 ? value : value.replaceAll("0*$", "").replaceAll("\\.$", "");
                    identifiedItem.setValueAccuracyLevel(StringHelper.distance(value, identifiedItem.getManualDataValue()));

                    int cal = (value.length() - identifiedItem.getValueAccuracyLevel());
                    int valueAccuracyPercentage = 0;
                    if (cal > 0) {
                        valueAccuracyPercentage = cal * 100 / value.length();
                    }

                    identifiedItem.setValueAccuracyPercentage(valueAccuracyPercentage);
                    identifiedItem.setValue(value);

                } else {
                    identifiedItem.setValueAccuracyLevel(5);
                    identifiedItem.setValueAccuracyPercentage(0);
                }
            } catch (Exception e) {
                System.out.printf("Exception Occurred while computing the accuracy of the line item values");
            }
        }
    }

    public static OCRStats verifyLineItems(ScoreSummary scoreSummary, OCRStats ocrStats, Receipt receipt,
                                           List<ManualReceiptLineItem> manualLineItemsList, Result result) {

        ArrayList<LineItem> ocrLineItemList = receipt.getFullTextPredictedLineItems();
        ArrayList<LineItem> identifiedLineItemList = new ArrayList<>();
        ArrayList<LineItem> unIdentifiedLineItemList = new ArrayList<>();

        int totalManualLineItemSize = manualLineItemsList.size();

        // iterate through manual data
        for(int i=0; i < ocrLineItemList.size(); i++) {
            // key = manualData Line number , Distance
            try {
                LineItem lineItem = ocrLineItemList.get(i);
                // null returned when no match is found from the manual data set
                if (manualLineItemsList.size() != 0) {
                    LineItem item = identifyAndRemoveFromManualList(manualLineItemsList, lineItem);
                    if (item != null) {
                        identifiedLineItemList.add(item);
                    } else {
                        unIdentifiedLineItemList.add(lineItem);
                    }
                }
            } catch (Exception e) {
                System.out.println("Array index out of bound exception when measuring the accuracy of the description" + e );
            }
        }


        // TODO convert possible line items through the manual data set
        List<LineItem> possibleLineItemList = receipt.getPossibleLineItems();

        for(int i=0; i< possibleLineItemList.size(); i++) {
            try {
                LineItem lineItem = possibleLineItemList.get(i);
                if (manualLineItemsList.size() != 0) {
                    LineItem item = identifyAndRemoveFromManualList(manualLineItemsList, lineItem);
                    if (item != null) {
                        identifiedLineItemList.add(item);
                    } else {
                        unIdentifiedLineItemList.add(lineItem);
                    }
                }
            } catch (Exception e) {
                System.out.println("Array index out of bound exception when measuring the accuracy of the possible description" + e );
            }
        }

        // check for the value accuracy
        calculatePriceAccuracyToDescIdentidiedLineItems(identifiedLineItemList);

        // create a histogram for the results
        int[] descriptionHistogram = new int[identifiedLineItemList.size()];
        int[] valueHistogram = new int[identifiedLineItemList.size()];

        for (int i = 0; i < identifiedLineItemList.size(); i++) {
            descriptionHistogram[i] = identifiedLineItemList.get(i).getDescriptionAccuracyPercentage();
            valueHistogram[i] = identifiedLineItemList.get(i).getValueAccuracyPercentage();
        }

        //
        calculateLineItemScore(scoreSummary,descriptionHistogram, valueHistogram, totalManualLineItemSize);

        // This computes the histogram (min,max,bucketsize, valuesForHistogram)
        int[] histogramDesc = histogram(0, 100, 10, descriptionHistogram);
        int[] histogramValue = histogram(0, 100, 10, valueHistogram);


        // add the missed items to the accuracy test
        histogramDesc[0] = histogramDesc[0] + manualLineItemsList.size();
        histogramValue[0] = histogramValue[0] + manualLineItemsList.size();

        // remove the zero elements from the histogram
        HashMap<Integer, Integer> histogramAccuracyMap = new HashMap<>();
        HashMap<Integer, Integer> valueAccuracyMap = new HashMap<>();

        for(int i=0;i<histogramDesc.length;i++) {

            if (histogramDesc[i] != 0) {
                histogramAccuracyMap.put(i,histogramDesc[i]);
            }
            if (histogramValue[i] != 0) {
                valueAccuracyMap.put(i,histogramValue[i]);
            }
        }

        ocrStats.setDescriptionHistogram(histogramAccuracyMap);
        ocrStats.setValueHistogram(valueAccuracyMap);
        ocrStats.setDescriptionValueStats(finalResultsInStringArray(identifiedLineItemList));
        ocrStats.setUnclassifiedManualLineItem(Utils.setUnClassifiedManualData(manualLineItemsList));
        ocrStats.setUnclassifiedOCRLineItem(Utils.setUnClassifiedOCRData(unIdentifiedLineItemList));

        result.setFinalOCRLineItemList(Utils.getOCRFinalLineItemListAfterAccuracyCheck(identifiedLineItemList));

        return ocrStats;
    }

    public static String[] finalResultsInStringArray(List<LineItem> finalLineItems){

        String[] array = new String[finalLineItems.size()];
        for (int i=0; i< finalLineItems.size(); i++) {
            LineItem item = finalLineItems.get(i);
            String desc = "Desc : " + item.getDescription() + " | " + item.getManualDataDescription() + " | " +
                    String.valueOf(item.getDescriptionAccuracyPercentage()) + " |||| ";
            String value = "Value : " + item.getValue() + " | " + item.getManualDataValue() + " | " + String.valueOf(item.getValueAccuracyPercentage());
            array[i] = desc + value;
        }
        return array;
    }

    public static int[] histogram(final int min, final int max, final int bucket, int[] array) {
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
