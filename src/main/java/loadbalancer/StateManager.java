package loadbalancer;

import java.util.concurrent.BlockingQueue;

/**
 * Transfer local state to remote node
 * Transfer remote state to local node
 * Created by eideh on 4/23/2016.
 */
public class StateManager implements Runnable {
    public State localState;
    public State remoteState;
    public BlockingQueue<Job> jobQueue;
    private HardwareMonitor hardwareMonitor;

    public StateManager(BlockingQueue<Job> jobQueue, HardwareMonitor hardwareMonitor){
        this.jobQueue = jobQueue;
        this.hardwareMonitor = hardwareMonitor;
        // initialize local state and remote state
        
    }


    public void run() {
        //main queue processing
        try {
            while (true) {
                //take() from queue
                localState.setPendingJobs(jobQueue.size());
                //localState.setThrottlingValue();
                localState.setCpuUsePercent(hardwareMonitor.cpuUsePercentage);

                Thread.sleep(10);

            }
        } catch (Exception e) {
        }
    }

    //stateManager is a thread that gets info from other threads it seems

}
