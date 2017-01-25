package com.mitrai.scanner;

/**
 * Created by niro273 on 1/24/17.
 */
public class SystemParameters {

    private String id;
    private boolean random;

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
