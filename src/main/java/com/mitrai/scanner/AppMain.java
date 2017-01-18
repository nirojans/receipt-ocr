package com.mitrai.scanner;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;

public class AppMain {

    public static void main(String[] args) throws IOException, InterruptedException, SchedulerException {
        System.out.printf("Starting the main application");
        cronJob();
    }

    public static void cronJob() throws SchedulerException {

        JobDetail job2 = JobBuilder.newJob(CronJob.class).withIdentity("batchJob", "group2").build();
        Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group2")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).build();
        org.quartz.Scheduler scheduler2 = new StdSchedulerFactory().getScheduler();
        scheduler2.start();
        scheduler2.scheduleJob(job2, trigger2);

    }

}
