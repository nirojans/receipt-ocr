package com.mitrai.scanner;

import java.io.Serializable;

/**
 * Created by niro273 on 1/21/17.
 */
public class ManualReceipt implements Serializable {

    private String TILLROLL_DOC_ID;
    private String SHOP_NAME;
    private String TILLROLL_LINE_DESC;
    private String QUANTITY;
    private String PERIOD_YEAR;

    private double score;

    public String getTILLROLL_DOC_ID() {
        return TILLROLL_DOC_ID;
    }

    public void setTILLROLL_DOC_ID(String TILLROLL_DOC_ID) {
        this.TILLROLL_DOC_ID = TILLROLL_DOC_ID;
    }

    public String getSHOP_NAME() {
        return SHOP_NAME;
    }

    public void setSHOP_NAME(String SHOP_NAME) {
        this.SHOP_NAME = SHOP_NAME;
    }

    public String getTILLROLL_LINE_DESC() {
        return TILLROLL_LINE_DESC;
    }

    public void setTILLROLL_LINE_DESC(String TILLROLL_LINE_DESC) {
        this.TILLROLL_LINE_DESC = TILLROLL_LINE_DESC;
    }

    public String getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(String QUANTITY) {
        this.QUANTITY = QUANTITY;
    }

    public String getPERIOD_YEAR() {
        return PERIOD_YEAR;
    }

    public void setPERIOD_YEAR(String PERIOD_YEAR) {
        this.PERIOD_YEAR = PERIOD_YEAR;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}