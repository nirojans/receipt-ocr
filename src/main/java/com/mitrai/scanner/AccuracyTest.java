package com.mitrai.scanner;

import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by niro273 on 1/21/17.
 */
public class AccuracyTest {

    public static int verifySuperMarketBrand(String id, String brandName) throws UnknownHostException {

        List<ManualReceipt> manualReceiptList = DataServiceImpl.getReceiptFromManualData(id, DataServiceImpl.manualDataTescoCollection);
        String manualShopName = manualReceiptList.get(0).getSHOP_NAME();
        if (manualShopName.contains(manualShopName)) {
            return 100;
        }
        return 0;
    }

    public static void verifySuperMarketName() {

    }

    public static void verifyLineItems() {

    }

    public static void verifyDate() {

    }
}
