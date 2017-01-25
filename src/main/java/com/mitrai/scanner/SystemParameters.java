package com.mitrai.scanner;

/**
 * Created by niro273 on 1/24/17.
 */
public class SystemParameters {

    private String id;
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
