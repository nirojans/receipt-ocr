package com.mitrai.scanner;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nirojans on 1/4/17.
 */
public class Configs {

    public static final String CONFIG_FILE_NAME = "config.properties";
    public static final String SUPER_MARKET_TEMPLATE_NAME = "template.properties";

    public static int maxFileSize;
    public static String moveToArchiveStatus;

    static{
        Properties properties = Configs.getConfigs(Configs.CONFIG_FILE_NAME);
        maxFileSize = Integer.parseInt(properties.getProperty("max_file_size"));
        moveToArchiveStatus = properties.getProperty("move_images_to_archive_after_processing");
    }

    public static Properties getConfigs(String fileName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(fileName)) {
            props.load(resourceStream);
        } catch (Exception e) {
            System.out.println("Exception has occurred during reading the configuration files");
        }
        return props;
    }
}
