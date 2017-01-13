package com.mitrai.scanner;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

/**
 * Created by nirojans on 1/13/17.
 */

@DisallowConcurrentExecution
public class CronJob implements Job {

    private OCRCronService OCRCronService = new OCRCronService();

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            OCRCronService.startDoingBatchProcessing();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Exception occurred in the batch processing process");
            e.printStackTrace();
        }
    }

}
