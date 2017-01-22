package com.mitrai.scanner;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AppMain {

    public static void main(String[] args) throws IOException, InterruptedException, SchedulerException {
        System.out.printf("Starting the main application");
//        List<File> fileList = FileHelper.selectRandomReceipts();
//        FileHelper.copySelectedReceiptsToRawImageFolder(fileList);
        cronJob();
    }

    public static void cronJob() throws SchedulerException {

        JobDetail job2 = JobBuilder.newJob(CronJob.class).withIdentity("batchJob", "group2").build();
        // Job runs for every 5 seconds
        Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group2")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).build();
        org.quartz.Scheduler scheduler2 = new StdSchedulerFactory().getScheduler();
        scheduler2.start();
        scheduler2.scheduleJob(job2, trigger2);

    }

}
