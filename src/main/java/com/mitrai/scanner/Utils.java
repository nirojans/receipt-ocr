package com.mitrai.scanner;

import com.mitrai.scanner.receipt.FinalLineItem;

import java.util.*;

/**
 * Created by niro273 on 1/20/17.
 */
public class Utils {

    public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static Map<Integer, Integer> sortByComparatorIntegerMap(Map<Integer, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<Integer, Integer>> list = new LinkedList<Map.Entry<Integer, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>()
        {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static List<FinalLineItem> getOCRFinalLineItemListAfterAccuracyCheck(ArrayList<LineItem> ocrLineItemList) {
        List<FinalLineItem> finalLineItemList = new ArrayList<>();

        for (LineItem item : ocrLineItemList) {
            FinalLineItem finalLineItem = new FinalLineItem();
            finalLineItem.setDescription(item.getDescription());
            finalLineItem.setValue(item.getValue());
            finalLineItemList.add(finalLineItem);
        }
        return finalLineItemList;
    }

    public static List<FinalLineItem> getManualFinalLineItemList(List<ManualReceiptLineItem> manualReceiptLineItemList) {
        List<FinalLineItem> finalLineItemList = new ArrayList<>();

        for (ManualReceiptLineItem item : manualReceiptLineItemList) {
            FinalLineItem finalLineItem = new FinalLineItem();
            finalLineItem.setDescription(item.getTILLROLL_LINE_DESC());
            finalLineItem.setValue(item.getLINE_PRICE());
            finalLineItemList.add(finalLineItem);
        }

        return finalLineItemList;
    }

    // TODO check if not in use remove

    public static List<String> getLineItemsInStringArray(List<LineItem> lineItems){
        List<String> stringList = new ArrayList<>();
        for (LineItem item : lineItems) {
            String desc = item.getDescription() + " | " + item.getValue();
            stringList.add(desc);
        }
        return stringList;
    }

    public static List<String> getLineManualItemsInStringArray(List<ManualReceiptLineItem> manualReceiptLineItemList){
        List<String> stringList = new ArrayList<>();
        for (ManualReceiptLineItem item : manualReceiptLineItemList) {
            String desc = item.getTILLROLL_LINE_DESC() + " | " + item.getLINE_PRICE();
            stringList.add(desc);
        }
        return stringList;
    }



}
