package loadbalancer;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import static loadbalancer.Config.QUEUE_LOCK;

public class TransferManager{
	private static ObjectMapper mapper = new ObjectMapper();
		
	public  void sendJobs() throws InterruptedException, Exception{
	System.out.println(Config.mode+":TransferManager send jobs called");
	State localState=Main.stateManager.localState;
	State remoteState=Main.stateManager.remoteState;
	int totaljobs=(localState.pendingJobs+remoteState.pendingJobs);
	if (totaljobs < 50)
		return;
	int additionalremoteJobsNeeded= (int) ((remoteState.throttlingValue*totaljobs)/(localState.throttlingValue+remoteState.throttlingValue))-remoteState.pendingJobs;
	synchronized (Config.QUEUE_LOCK) {	
	while(additionalremoteJobsNeeded>0){
		Job job=Main.jobQueue.remove();
		HttpConnection.sendPost("addJob",job);
		System.out.println(Config.mode+":TransferManager sent job with id:"+job.getJobId());
		additionalremoteJobsNeeded--;
	}
	
	}
	
	System.out.println(Config.mode+":TransferManager send jobs finished");

}
	
 public  void receiveJobs() throws JsonParseException, JsonMappingException, InterruptedException, IOException, Exception{
	   System.out.println(Config.mode+":TransferManager receive jobs called");
		State localState=Main.stateManager.localState;
		State remoteState=Main.stateManager.remoteState;
		int totaljobs=(localState.pendingJobs+remoteState.pendingJobs);
		if (totaljobs < 50)
			return;
		int additionalremoteJobsPresent= (int) ((remoteState.throttlingValue*totaljobs)/(localState.throttlingValue+remoteState.throttlingValue))-remoteState.pendingJobs;
		synchronized (Config.QUEUE_LOCK) {	
		while(additionalremoteJobsPresent>0){
			Job job=mapper.readValue(HttpConnection.sendGet("getJob"),Job.class);
			Main.jobQueue.add(mapper.readValue(HttpConnection.sendGet("getJob"),Job.class));
			System.out.println(Config.mode+":TransferManager received job with id:"+job.getJobId());
			additionalremoteJobsPresent--;
		}
		
		   System.out.println(Config.mode+":TransferManager receive jobs finhsed");
		   
	}
 
}

}