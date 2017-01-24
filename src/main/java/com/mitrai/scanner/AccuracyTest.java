package com.mitrai.scanner;

import javax.sound.sampled.Line;
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

    public static void verifyLineItems(Receipt receipt, List<ManualReceipt> manualReceiptList) {

        ArrayList<LineItem> lineItems = receipt.getPredictedLineItemFromManualData();

        // iterate through manual data
        for(int i=0; i < lineItems.size(); i++) {

            // Iterate through all template names
            Map<String, Integer> templateScoreMap = new HashMap<>();

            int[] scoreArrayForPreProcess = new int[manualReceiptList.size()];
            Arrays.fill(scoreArrayForPreProcess, 50);


            for(int x=0; x<manualReceiptList.size(); x++) {
                scoreArrayForPreProcess[x] = StringHelper.distance(lineItems.get(i).getDescription(), manualReceiptList.get(x).getTILLROLL_LINE_DESC());
            }

            // sort the array and get the first element to get the best match score
            Arrays.sort(scoreArrayForPreProcess);

            Map<String, Integer> sortedMapAsc = Utils.sortByComparator(templateScoreMap, ASC);
            Map.Entry<String, Integer> entry = sortedMapAsc.entrySet().iterator().next();
        }



    }

    public static void verifyDate() {

    }
}
