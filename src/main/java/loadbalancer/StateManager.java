package loadbalancer;

import org.codehaus.jackson.map.ObjectMapper;

public class StateManager implements Runnable {
    public  State localState;
    public  State remoteState;
    private ObjectMapper mapper = new ObjectMapper();
    public Main main;
  
    public StateManager(){
    	localState= new State();
    	remoteState=new State();
    }
    public void run() {
        //main queue processing
        try {
            while (true) {
                localState.pendingJobs=Main.jobQueue.size();
                sendLocalStatetoRemoteServer();
                Thread.sleep(Config.stateManagerSleeptime);
            }
        } catch (Exception e) {
        }
    }

	public  void sendLocalStatetoRemoteServer() throws Exception {
		try{
		HttpConnection.sendPost("submitState",localState);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	public  void updateRemoteState(State state) throws Exception {
		this.remoteState=state;
	}
	
	public  void updateThreshold(double throttlingValue) throws Exception {
		localState.throttlingValue=throttlingValue;
		sendLocalStatetoRemoteServer();
		
	}


}
