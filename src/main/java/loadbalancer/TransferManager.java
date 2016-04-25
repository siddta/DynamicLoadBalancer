package loadbalancer;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import static loadbalancer.Config.QUEUE_LOCK;

/**
 * Transfers jobs between phones
 * Created by eideh on 4/23/2016.
 */
public class TransferManager{
	private static ObjectMapper mapper = new ObjectMapper();

	
	public static void sendJobs() throws InterruptedException, Exception{
	State localState=StateManager.localState;
	State remoteState=StateManager.remoteState;
	int totaljobs=(localState.pendingJobs+remoteState.pendingJobs);
	if (totaljobs < 50)
		return;
	int additionalremoteJobsNeeded= (int) ((remoteState.throttlingValue*totaljobs)/(localState.throttlingValue+remoteState.throttlingValue))-remoteState.pendingJobs;
	synchronized (Config.QUEUE_LOCK) {	
	while(additionalremoteJobsNeeded>0){
		HttpConnection.sendPost("addJob",Main.jobQueue.take());
		additionalremoteJobsNeeded--;
	}
	
	}
		

}
	
 public static void receiveJobs() throws JsonParseException, JsonMappingException, InterruptedException, IOException, Exception{
		State localState=StateManager.localState;
		State remoteState=StateManager.remoteState;
		int totaljobs=(localState.pendingJobs+remoteState.pendingJobs);
		if (totaljobs < 50)
			return;
		int additionalremoteJobsPresent= (int) ((remoteState.throttlingValue*totaljobs)/(localState.throttlingValue+remoteState.throttlingValue))-remoteState.pendingJobs;
		synchronized (Config.QUEUE_LOCK) {	
		while(additionalremoteJobsPresent>0){
			Main.jobQueue.put(mapper.readValue(HttpConnection.sendGet("getJob"),Job.class));
			additionalremoteJobsPresent--;
		}
		
	
	}
 
}

}