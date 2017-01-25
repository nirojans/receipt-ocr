package com.mitrai.scanner;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AppMain {

    public static void main(String[] args) throws IOException, InterruptedException, SchedulerException {
        System.out.printf("Starting the main application");

        // If the random batch processing status is true then copy files from tesco and sainsbury and put in the raw folder
        if (DataServiceImpl.getRandomProcessingStatus()) {
            List<File> tescoFileList = FileHelper.selectRandomReceipts(FileHelper.tescoReceiptsFolderPath);
            List<File> sainsburyFileList = FileHelper.selectRandomReceipts(FileHelper.sainsburyReceiptsFolderPath);

            FileHelper.copySelectedReceiptsToRawImageFolder(tescoFileList);
            FileHelper.copySelectedReceiptsToRawImageFolder(sainsburyFileList);
        }
        cronJob();
        DataServiceImpl.getRandomProcessStatus();

    }

    public static void cronJob() throws SchedulerException {

        JobDetail job2 = JobBuilder.newJob(CronJob.class).withIdentity("batchJob", "group2").build();
        // Job runs for every 5 seconds
        Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group2")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();
        org.quartz.Scheduler scheduler2 = new StdSchedulerFactory().getScheduler();
        scheduler2.start();
        scheduler2.scheduleJob(job2, trigger2);

    }

}
