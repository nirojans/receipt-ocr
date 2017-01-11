package com.mitrai.scanner;
import org.bytedeco.javacpp.*;
import org.junit.Test;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;
import static org.junit.Assert.assertTrue;

public class AppMain {

    public static void main(String[] args){

        // Initiate all the folders for batch processing
        FileHelper.initBaseFolder();

        // Call the schduler to process all images in raw folder

        System.out.print("hello this is from app main");
        TemplateEngine.getRetaurantName();
    }
}
