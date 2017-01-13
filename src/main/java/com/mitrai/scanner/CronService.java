package com.mitrai.scanner;

import java.util.Date;

/**
 * Created by nirojans on 1/13/17.
 */
public class CronService {

    public void startDoingBatchProcessing() {
        System.out.println("Batch processing started " + new Date());
        try {
            Thread.sleep(5000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Finished processing started " + new Date());
    }
}
