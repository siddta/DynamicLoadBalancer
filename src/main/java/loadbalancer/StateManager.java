package loadbalancer;

import java.util.concurrent.BlockingQueue;

/**
 * Transfer local state to remote node
 * Transfer remote state to local node
 * Created by eideh on 4/23/2016.
 */
public class StateManager implements Runnable {
    public static State localState;
    public static State remoteState;
    private HardwareMonitor hardwareMonitor;

    public void run() {
        //main queue processing
        try {
            while (true) {
                //take() from queue
                localState.setPendingJobs(Main.jobQueue.size());
                sendLocalStatetoRemoteServer();
                Thread.sleep(Config.stateManagerSleeptime);

            }
        } catch (Exception e) {
        }
    }


	/**
	 * @throws Exception 
	 * 
	 */
	public static void sendLocalStatetoRemoteServer() throws Exception {
		HttpConnection.sendPost("submitState",localState);
		
	}

	
	public static void updateRemoteState(State state) throws Exception {
		remoteState=state;
		
	}
	
	public static void updateThreshold(double throttlingValue) throws Exception {
		localState.throttlingValue=throttlingValue;
		sendLocalStatetoRemoteServer();
		
	}


}
