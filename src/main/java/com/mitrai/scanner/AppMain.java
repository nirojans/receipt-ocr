package com.mitrai.scanner;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AppMain {


    public static void main(String[] args) throws IOException, InterruptedException, SchedulerException {
        cronJob();
    }

    public static void ocrTask() throws IOException, InterruptedException {
//        FileUtils.deleteDirectory(new File(FileHelper.baseFolder));

        // Initiate all the folders for batch processing
        FileHelper.initBaseFolder();
        File[] listOfFiles = FileHelper.getAllFileNames();



        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileNameWithExtension = listOfFiles[i].getName();
                String fileNameWithoutExtension = FileHelper.getFileNameWithoutExtension(listOfFiles[i]);
                // check if it is a valid input
                String extensionName = FileHelper.getFileExtension(listOfFiles[i]);
                if (extensionName.equalsIgnoreCase("jpeg") || extensionName.equalsIgnoreCase("jpg") || extensionName.equalsIgnoreCase("png")) {
                    TesseractEngine.performPreProcessingAndOCR(fileNameWithExtension);

                    // read all text
                    List<Receipt> receiptList = FileHelper.readAllResultsForAImage(fileNameWithoutExtension);
                    receiptList = identifySuperMarketName(receiptList);
                    TemplateEngine.identifyLineItems(receiptList);

                    Receipt highReceipt = receiptList.get(receiptList.size()-1);
                    FileHelper.writeResultsToFile(highReceipt, "results.txt");
                    highReceipt.setId(fileNameWithoutExtension);
                    DataServiceImpl.insertIntoDB(highReceipt);
                }
            }
        }

    }

    public static void cronJob() throws SchedulerException {

        JobDetail job2 = JobBuilder.newJob(CronJob.class).withIdentity("batchJob", "group2").build();
        Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group2")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?")).build();
        org.quartz.Scheduler scheduler2 = new StdSchedulerFactory().getScheduler();
        scheduler2.start();
        scheduler2.scheduleJob(job2, trigger2);

    }

    public static List<Receipt> identifySuperMarketName(List<Receipt> receiptList) {
        Properties properties = Configs.getConfigs(Configs.SUPER_MARKET_TEMPLATE_NAME);

        for (Receipt receipt : receiptList) {

            String[] ocrResults = receipt.getRawData();
            for(String key : properties.stringPropertyNames()) {
                String templateName = properties.getProperty(key);
                int[] scoreArrayForPreProcess = new int[ocrResults.length];
                Arrays.fill(scoreArrayForPreProcess, 50);

                for(int j=0; j < ocrResults.length; j++) {
                    if ((templateName.length() * 2) >= ocrResults[j].length()) {
                        scoreArrayForPreProcess[j] = StringHelper.distance(templateName, ocrResults[j].toLowerCase());
                    }
                }
                receipt.setSuperMarketName(templateName);
                Arrays.sort(scoreArrayForPreProcess);
                receipt.setNameRecognitionRank(scoreArrayForPreProcess[0]);
            }
        }
        return receiptList;
    }


}
