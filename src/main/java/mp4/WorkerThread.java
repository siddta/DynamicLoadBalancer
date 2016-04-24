package mp4;

import java.util.concurrent.BlockingQueue;

/**
 * Created by eideh on 4/23/2016.
 */
public class WorkerThread implements Runnable {

    //responsible for processing each job and locally storing the result
    //throttling
    /*
    a throttling value of 70% means that during each 100 milliseconds, the worker thread must be sleeping for
    30 milliseconds and processing the job for 70 milliseconds
     */
    public BlockingQueue<Job> jobQueue;
    private double throttle_percentage;

    public WorkerThread() {
        throttle_percentage = 100;
    }

    public WorkerThread(BlockingQueue<Job> jobQueue, double throttle_percentage){
        this.jobQueue = jobQueue;
        this.throttle_percentage = throttle_percentage;
    }


    public void run() {
        //main queue processing
        try {
            while (true) {
                //take() from queue
                jobQueue.take();
            }
        } catch (InterruptedException e) {
            //if interrupted while taking
        }
    }
}
