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
                localState.pendingJobs=main.jobQueue.size();
                sendLocalStatetoRemoteServer();
                Thread.sleep(Config.stateManagerSleeptime);
            }
        } catch (Exception e) {
        }
    }

	public  void sendLocalStatetoRemoteServer() throws Exception {
		if(Config.mode=="remote")
		System.out.println(mapper.writeValueAsString(localState));
		HttpConnection.sendPost("submitState",localState);
		
	}

	
	public  void updateRemoteState(State state) throws Exception {
		this.remoteState=state;
	}
	
	public  void updateThreshold(double throttlingValue) throws Exception {
		localState.throttlingValue=throttlingValue;
		sendLocalStatetoRemoteServer();
		
	}


}
