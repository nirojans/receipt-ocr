package com.mitrai.scanner;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nirojans on 1/4/17.
 */
public class Configs {

    public static final String CONFIG_FILE_NAME = "config.properties";
    public static final String SUPER_MARKET_TEMPLATE_NAME = "template.properties";

    public static Properties getConfigs(String fileName) {
        String resourceName = fileName;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
        } catch (Exception e) {
            System.out.println("Exception has occurred during the config reading phase");
        }
        return props;
    }

    public static Properties getFolderNames() {
        String resourceName = CONFIG_FILE_NAME;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
        } catch (Exception e) {
            System.out.println("Exception has occurred during the config reading phase");
        }
        return props;
    }
}
