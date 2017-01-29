package com.mitrai.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niro273 on 1/6/17.
 */
public class StringHelper {

    public static boolean regexForDescAndLongSpace(LineItem lineItem) {
        String descriptionWithDecimalRegex = "(?<description>.+)\\s{2,}";
        Pattern p = Pattern.compile(descriptionWithDecimalRegex);

        Matcher m = p.matcher(lineItem.getDescription());
        if (m.find()) {
            lineItem.setDescription(m.group("description").trim().replaceAll(" +", " "));
        } else {
            return false;
        }
        return true;
    }

    public static boolean regexForDescWithNumbersOnly(LineItem lineItem) {
        String descriptionWithDecimalRegex = "(?<description>.+)\\s{2,}(?<amount>\\d+)";
        Pattern p = Pattern.compile(descriptionWithDecimalRegex);

        Matcher m = p.matcher(lineItem.getDescription());
        if (m.find()) {
            lineItem.setDescription(m.group("description").trim().replaceAll(" +", " "));
            lineItem.setValue(m.group("amount"));
        } else {
            return false;
        }
        return true;
    }

    public static boolean regexForDescWithNumbersAndPeriod(LineItem lineItem) {
        String descriptionWithDecimalRegex = "(?<description>.+)\\s{2,}(?<amount>\\d+\\.\\d{2})";
        Pattern p = Pattern.compile(descriptionWithDecimalRegex);

        Matcher m = p.matcher(lineItem.getDescription());
        if (m.find()) {
            lineItem.setDescription(m.group("description").trim().replaceAll(" +", " "));
            lineItem.setValue(m.group("amount"));
        } else {
            return false;
        }
        return true;
    }

    public static boolean regexForDescWithNumbersWithOutDecimalDot(LineItem lineItem) {
        String descriptionWithDecimalRegex = "(?<description>.+)\\s{2,}(?<amount>\\d+\\.\\d{2})";
        Pattern p = Pattern.compile(descriptionWithDecimalRegex);

        Matcher m = p.matcher(lineItem.getDescription());
        if (m.find()) {
            lineItem.setDescription(m.group("description").trim().replaceAll(" +", " "));
            lineItem.setValue(m.group("amount"));
        } else {
            return false;
        }
        return true;
    }

    public static Receipt getLineItemsForReceipt(Receipt receipt, String regex) {

        boolean firstLineItem = true;

        ArrayList<LineItem> lineItemList = new ArrayList<>();
        String[] inputStrings = receipt.getRawData();
        Pattern p = Pattern.compile(regex);

        List<Integer> integerList = new ArrayList<>();
        List<LineItem> possibleLineItems = new ArrayList<>();

        for (int i=0; i < inputStrings.length; i++) {
            String inputString = inputStrings[i];
            Matcher m = p.matcher(inputString);
            if (m.find()) {

                if (firstLineItem) {
                    firstLineItem = false;
                    receipt.setLineItemStartLine(i);
                }
                receipt.setLineItemEndLine(i);

                // replaces extra spaces with one single space
                String description  = m.group("description").trim().replaceAll(" +", " ");
                String currency     = m.group("currency");
                String amountString = m.group("amount");

                LineItem lineItem = new LineItem();
                lineItem.setDescription(description);
                lineItem.setCurrencySymbol(currency);
                lineItem.setValue(amountString);
                lineItem.setLineNumber(i);

                lineItemList.add(lineItem);
            } else if (!firstLineItem) {
                integerList.add(i);

                LineItem possibleLineItem = new LineItem();
                possibleLineItem.setDescription(inputString);
                possibleLineItem.setLineNumber(i);
                possibleLineItems.add(possibleLineItem);
            }
        }
        receipt.setLineItems(lineItemList);
        receipt.setUnclassifiedLineItemNumbers(integerList);
        receipt.setPossibleLineItems(possibleLineItems);
        return receipt;
    }

    public static void testForDate() {

        String[] inputStrings = {"12/10/19   12:36", "12/12/13"};
        String regex = "([0-9]{2})\\/([0-9]{2})\\/([0-9]{2})";

        Pattern p = Pattern.compile(regex);
        for (String input : inputStrings) {
            Matcher m = p.matcher(input);
            if (m.find()) {
                String day = m.group(0);
                String month = m.group(1);
                String year = m.group(1);
            }
        }

    }

    /*  This method is for String similarity computations (levenshtein distance)
        If the words are same will give zero
     */
    public static int distance(final String s1, final String s2) {
        if (s1.equals(s2)) {
            return 0;
        }

        if (s1.length() == 0) {
            return s2.length();
        }

        if (s2.length() == 0) {
            return s1.length();
        }

        // create two work vectors of integer distances
        int[] v0 = new int[s2.length() + 1];
        int[] v1 = new int[s2.length() + 1];
        int[] vtemp;

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }

        for (int i = 0; i < s1.length(); i++) {
            // calculate v1 (current row distances) from the previous row v0
            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < s2.length(); j++) {
                int cost = 1;
                if (s1.charAt(i) == s2.charAt(j)) {
                    cost = 0;
                }
                v1[j + 1] = Math.min(
                        v1[j] + 1,              // Cost of insertion
                        Math.min(
                                v0[j + 1] + 1,  // Cost of remove
                                v0[j] + cost)); // Cost of substitution
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            //System.arraycopy(v1, 0, v0, 0, v0.length);

            // Flip references to current and previous row
            vtemp = v0;
            v0 = v1;
            v1 = vtemp;

        }

        return v0[s2.length()];
    }
}
