package com.mitrai.scanner;

import java.util.ArrayList;

/**
 * Created by niro273 on 1/11/17.
 */
public class Receipt {

    String restaurantName;
    ArrayList<LineItem> lineItemses;
    String date;
    String otherValues;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public ArrayList<LineItem> getLineItemses() {
        return lineItemses;
    }

    public void setLineItemses(ArrayList<LineItem> lineItemses) {
        this.lineItemses = lineItemses;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOtherValues() {
        return otherValues;
    }

    public void setOtherValues(String otherValues) {
        this.otherValues = otherValues;
    }
}
