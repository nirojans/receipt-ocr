package com.mitrai.scanner;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by nirojans on 1/13/17.
 */

@DisallowConcurrentExecution
public class CronJob implements Job {

    private CronService cronService = new CronService();

    public void execute(JobExecutionContext context) throws JobExecutionException {
        cronService.startDoingBatchProcessing();
    }

}
