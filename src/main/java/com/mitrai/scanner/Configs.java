package com.mitrai.scanner;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nirojans on 1/4/17.
 */
public class Configs {

    public static Boolean getOSDetails() {
        String status = "true";
        String resourceName = "config.properties"; // could also be a constant
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
            System.out.print(props.toString());
        } catch (Exception e) {
            System.out.println("Exception has occurred during the config reading phase");
        }
        return true;
    }
}
