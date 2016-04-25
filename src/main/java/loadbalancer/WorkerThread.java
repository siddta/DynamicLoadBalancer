package loadbalancer;

import java.util.concurrent.BlockingQueue;

import static loadbalancer.Config.QUEUE_LOCK;

/**
 * Created by eideh on 4/23/2016.
 */
public class WorkerThread implements Runnable {
	
	public BlockingQueue<Job> jobQueue;
    private double throttle_percentage;

    public WorkerThread() {
        this.throttle_percentage = 100;
    }

    public WorkerThread(BlockingQueue<Job> jobQueue, double throttle_percentage){
        this.jobQueue = jobQueue;
        this.throttle_percentage = throttle_percentage;
    }


    public void run() {
        //main queue processing
        try {
            while (true) {
                synchronized (QUEUE_LOCK) {
                    //take() from queue
                    Job job = jobQueue.take();
                    //job.execute();
                    executeJob(job);
                }
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            //if interrupted while taking
        }
    }
    
    public void executeJob(Job job){
    	double [] values=job.getValues();
    	for(int i=0;i<Config.jobSize;i++)
    		for(int j=0;j<Config.sum_iteration;j++){{
        	values[i]+=1.11111;
        } 
    }
    	job.setValues(values);
    	Main.processedJobList.add(job);
    }
}
