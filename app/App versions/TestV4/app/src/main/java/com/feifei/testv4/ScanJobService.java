package com.feifei.testv4;

/** unused function **/

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class ScanJobService extends JobService {
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("Job: ", "Job Started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(JobParameters params) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i<100; i++){
                    Log.d("Job: ", "run " +i);
                    if(jobCancelled){
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.d("Job: ", "Job Finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("Job: ", "Job Cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
