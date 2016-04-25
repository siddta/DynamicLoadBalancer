package loadbalancer;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import static loadbalancer.Config.QUEUE_LOCK;

public class TransferManager {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * With Sender-initiated algorithms, an overloaded node is looking to send to an underloaded node
     * In our case it will call addJob (to be transferred)
     * @throws InterruptedException
     * @throws Exception
     */
    public void sendJobs() throws InterruptedException, Exception {
        //System.out.println(Config.mode + ":TransferManager send jobs called");
        State localState = Main.stateManager.localState;
        State remoteState = Main.stateManager.remoteState;
        int totaljobs = (localState.pendingJobs + remoteState.pendingJobs);
        if (totaljobs < 50)
            return;

        int additionalremoteJobsNeeded = (int) ((remoteState.throttlingValue * totaljobs) / (localState.throttlingValue + remoteState.throttlingValue)) - remoteState.pendingJobs;
        synchronized (Config.QUEUE_LOCK) {
            while (additionalremoteJobsNeeded > 0) {
                Job job = Main.jobQueue.remove();
                HttpConnection.sendPost("addJob", job);
                System.out.println(Config.mode + ":TransferManager sent job with id:" + job.getJobId());
                additionalremoteJobsNeeded--;
            }

        }

        System.out.println(Config.mode + ":TransferManager send jobs finished");

    }

    /**
     * With receiver-initiated algorithms, an underloaded node looks to receive jobs from an overloaded node
     * In our case it will call getJob
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws InterruptedException
     * @throws IOException
     * @throws Exception
     */
    public void receiveJobs() throws JsonParseException, JsonMappingException, InterruptedException, IOException, Exception {
        //System.out.println(Config.mode + ":TransferManager receive jobs called");
        State localState = Main.stateManager.localState;
        State remoteState = Main.stateManager.remoteState;
        int totaljobs = (localState.pendingJobs + remoteState.pendingJobs);
        if (totaljobs < 50)
            return;
        //int additionalremoteJobsPresent = (int) ((remoteState.throttlingValue * totaljobs) / (localState.throttlingValue + remoteState.throttlingValue)) - remoteState.pendingJobs;
        //^ actually still how may remote jobs needed (Aka remote should be taking some)
        int additionalLocalJobsNeeded = (int) ((localState.throttlingValue*totaljobs)/ (localState.throttlingValue + remoteState.throttlingValue)) - localState.pendingJobs;
        //^ how many more local jobs needed. Aka we are receiver-initiated!
            while (additionalLocalJobsNeeded > 0) {
                //System.out.println("add " + additionalLocalJobsNeeded);
                Job job = mapper.readValue(HttpConnection.sendGet("getJob"), Job.class);
                Main.jobQueue.add(job);
                System.out.println(Config.mode + ":TransferManager received job with id:" + job.getJobId());
                additionalLocalJobsNeeded--;
            }

            System.out.println(Config.mode + ":TransferManager receive jobs finhsed");



    }

}