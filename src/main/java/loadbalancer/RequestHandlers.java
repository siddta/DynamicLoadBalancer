package loadbalancer;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;


@Path("requests")
public class RequestHandlers {
	private ObjectMapper mapper = new ObjectMapper();
	public StateManager stateManager;

	
	@POST
	@Path("/addJob")
	@Consumes(MediaType.TEXT_PLAIN)
	public void addjob(String json) throws JsonParseException, JsonMappingException, IOException {	
	/*	
		final byte[] isoBytes = json.getBytes("ISO-8859-1");
		System.out.println("No. of bytes transferred for one job"+isoBytes.length);*/		
		Job job = mapper.readValue(json,Job.class);
		Main.jobQueue.add(job);
		System.out.println("Received Job with Id:"+job.getJobId()); //(our node is underloaded, sender is overloaded)
		
	}

	
	@GET
	@Path("/getJob")
	@Produces(MediaType.TEXT_PLAIN)
	public String getjob() throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		System.out.println("Received get job request "); //(our node is overloaded, receiver wants it)
		//System.out.println("Queue size " + Main.jobQueue.size());
		String response="";
		synchronized (Config.QUEUE_LOCK) {
			if(Main.jobQueue.size()>0){
			 response=mapper.writeValueAsString(Main.jobQueue.remove());
			}
		}
	 return response;
		
	}
	
	
	@POST
	@Path("/submitState")
	@Consumes(MediaType.TEXT_PLAIN)
	public String submitState(String json) throws Exception {
	//	System.out.println(json);
		State state = mapper.readValue(json,State.class);
		Main.stateManager.updateRemoteState(state);	
		return "got the state";

	}
	
	
	@GET
	@Path("/setThreshold/{threshold}")
	@Consumes(MediaType.TEXT_PLAIN)
	public String setThreshold(@PathParam("threshold")String thresholdString) throws Exception {
		double threshold=Double.parseDouble(thresholdString);
		Main.stateManager.updateThreshold(threshold);
		System.out.println("Threshold set to:"+threshold);
		return "threshold set to" + threshold;

	}
	
	@POST
	@Path("/submitAggregatedResults")
	@Consumes(MediaType.TEXT_PLAIN)
	public String submitAggregatedData(String json) throws JsonParseException, JsonMappingException, IOException {
		List<Job> jobs = mapper.readValue(json, new TypeReference<List<Job>>(){});
		for(Job job:jobs){
			Main.processedJobList.add(job);
		}
		
		return "got jobs";
	}
	

	@GET
	@Path("/getState")
	@Produces(MediaType.TEXT_PLAIN)
	public String getState() throws JsonGenerationException, JsonMappingException, IOException {
		State state = Main.stateManager.localState;
		return mapper.writeValueAsString(state);
	}
	
	
	@GET
	@Path("/startProcessing")
	@Consumes(MediaType.TEXT_PLAIN)
	public boolean startProcessing() throws JsonGenerationException, JsonMappingException, IOException {
		Config.shouldStartProcessing=true;
		return true;
	}
	
	
	

}