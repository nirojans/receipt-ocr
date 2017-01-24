package com.mitrai.scanner;

/**
 * Created by niro273 on 1/22/17.
 */
public class AccuracyMetrics {

    public static int superMarketNameAccuracy;
    public static int lineAccuracy;
    public static int priceAccuracy;
    public static int quantityAccuracy;

    public static int getSuperMarketNameAccuracy() {
        return superMarketNameAccuracy;
    }

    public static void setSuperMarketNameAccuracy(int superMarketNameAccuracy) {
        AccuracyMetrics.superMarketNameAccuracy = superMarketNameAccuracy;
    }

    public static int getLineAccuracy() {
        return lineAccuracy;
    }

    public static void setLineAccuracy(int lineAccuracy) {
        AccuracyMetrics.lineAccuracy = lineAccuracy;
    }

    public static int getPriceAccuracy() {
        return priceAccuracy;
    }

    public static void setPriceAccuracy(int priceAccuracy) {
        AccuracyMetrics.priceAccuracy = priceAccuracy;
    }

    public static int getQuantityAccuracy() {
        return quantityAccuracy;
    }

    public static void setQuantityAccuracy(int quantityAccuracy) {
        AccuracyMetrics.quantityAccuracy = quantityAccuracy;
    }
}
