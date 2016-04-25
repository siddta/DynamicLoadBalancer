package loadbalancer;

import java.util.concurrent.BlockingQueue;

import static loadbalancer.Config.QUEUE_LOCK;

/**
 * Created by eideh on 4/23/2016.
 */
public class WorkerThread implements Runnable {
	public Main main;
    public void run() {
        //main queue processing
    	 long estimatedTime=1;
        try {
            while (main.jobQueue.size()>0) {
                synchronized (QUEUE_LOCK) {
                    //take() from queue
                    Job job = main.jobQueue.remove();
                    //job.execute();
                  estimatedTime=executeJob(job);
                }
                Thread.sleep((long)(estimatedTime*(main.stateManager.localState.throttlingValue)));
            }
        } catch (InterruptedException e) {
            //if interrupted while taking
        }
        System.out.println(Config.mode+":all jobs finished");
        main.stateManager.localState.stage=2;
    }
    
    public long executeJob(Job job){
    	
    	long startTime = System.currentTimeMillis();
    	double [] values=job.getValues();
    	for(int i=0;i<Config.jobSize;i++)
    		for(int j=0;j<Config.sum_iteration;j++){{
        	values[i]+=1.11111;
        } 
    }
    	job.setValues(values);
    	Main.processedJobList.add(job);
        long estimatedTime = System.currentTimeMillis() - startTime;
		return estimatedTime;
    }
}
