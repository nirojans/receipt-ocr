package com.mitrai.scanner;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by niro273 on 1/4/17.
 */
public class TemplateEngine {

    public static String getRetaurantName() {
        try {
            String[] br = FileHelper.readFile("images/sharp_clean_resize_result.txt");

            // Run a for loop, until we reach the Restaurant Name or the Max lines

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Not Found";
    }
}
