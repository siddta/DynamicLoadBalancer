package loadbalancer;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

	@POST
	@Path("/addjob")
	@Consumes(MediaType.TEXT_PLAIN)
	public String addjob(String json) throws JsonParseException, JsonMappingException, IOException {
		System.out.println("Received add job request:"+ json);
		Job job = mapper.readValue(json,Job.class);
		return "got the job";
	}

	
	@POST
	@Path("/submitState")
	@Consumes(MediaType.TEXT_PLAIN)
	public String submitState(String json) throws JsonParseException, JsonMappingException, IOException {
		System.out.println("Received submit state request:"+ json);
		State state = mapper.readValue(json,State.class);
		return "got the state";

	}
	
	
	@POST
	@Path("/setThreshold")
	@Consumes(MediaType.TEXT_PLAIN)
	public String setThreshold(double threshold) {
		System.out.println("Received set threshold request:"+ threshold);
		return "threshold set";

	}
	
	@POST
	@Path("/submitAggregatedData")
	@Consumes(MediaType.TEXT_PLAIN)
	public String submitAggregatedData(String json) throws JsonParseException, JsonMappingException, IOException {
		System.out.println("Received aggregated results request:"+ json);
		List<Job> jobs = mapper.readValue(json, new TypeReference<List<Job>>(){});
		return "got jobs";
	}
	

	@GET
	@Path("/getState")
	@Produces(MediaType.TEXT_PLAIN)
	public String getState() throws JsonGenerationException, JsonMappingException, IOException {
		System.out.println("Received get state request");
		State state= new State();
		return mapper.writeValueAsString(state);
	}
	

}