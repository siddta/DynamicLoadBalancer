/**
 * 
 */
package loadbalancer;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * @author tarique
 *
 */
public class Main
{
	private static String mode="Local";
	private static double[] A;
    private final static int jobSize = 8192; //1024*1024*4/512
    private final static int totalSize = 4194304; //1024*1024*4
    private static BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<Job>(); // do we need a blocking queue?
    private static StateManager stateManager;
    private static HardwareMonitor hardwareMonitor;
    private static TransferManager transferManager;
   

    public static void main(String[] args) throws Exception
    {
    	ResourceConfig config = new ResourceConfig();
    	 config.packages("loadbalancer");
    	 ServletHolder servlet = new ServletHolder(new ServletContainer(config));
       	Server server = new Server(3333);
    	 ServletContextHandler context = new ServletContextHandler(server, "/*");
    	 context.addServlet(servlet, "/*");
    	 
    	try {
    	     server.start();
    	     if(args.length > 0) {
        	     Config.mode=args[0];
        	     Config.localThreshold=Double.parseDouble(args[1]);
        	     bootstrap();   
        	     processing();
    	     }
    	     
    	     server.join();
    	     
    	 } finally {
    	     server.destroy();
   	 }

    }

	private static void bootstrap() throws Exception{
		
		hardwareMonitor = new HardwareMonitor();
        stateManager=new StateManager(jobQueue, hardwareMonitor);
        transferManager=new TransferManager(jobQueue);
        
        if( Config.mode.equals("local")){
            //Create the 512 jobs (add them to queue)
            //eg jobSize = 10
            //(start = 0, end = 9)
            //(start = 10, end = 19)
            for(int i = 0; i < totalSize; i+=jobSize){
                int start = 0;
                int end = i+(jobSize-1);
                Job job = new Job(i%jobSize,start,end);
                while(i<totalSize/2){
                	HttpConnection.sendPost("addJob",job);
                }
                try {
                    jobQueue.put(job);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Config.shouldStartProcessing=true;
            HttpConnection.sendGet("startProcessing");
        }
        else if( Config.mode.equals("remote")){
        	while(!Config.shouldStartProcessing){
        		Thread.sleep(10);
        	}
        }       
        
        
    }
    //main loop

	
  private static void processing(){
	  	stateManager.localState.stage=1;
   
        //initialize transfer_manager thread
        //should the transferManager have a different queue??
        //should the transferManager even be its own thread, or just part of the adaptor thread?
        Thread transferThread = new Thread(new TransferManager(jobQueue));
        transferThread.start();
        
        //initialize worker_thread
        Thread workerThread = new Thread(new WorkerThread(jobQueue, 70));
        workerThread.start();
        
        //initialize hardware_monitor thread
        Thread hardwareMonitorThread = new Thread(hardwareMonitor);
        hardwareMonitorThread.start();
        
        //initialize state_manager thread
        Thread stateManagerThread = new Thread(new StateManager(jobQueue, hardwareMonitor));
        stateManagerThread.start();

       
        //initialize Adaptor(state_manager, hardware_monitor, transfer_manager, worker_thread)
        //Adaptor should have callbacks from state_manager and hardware_monitor threads
        //and should be able to call stuff in transfer_manger and worker_thread

        //while queue is not empty
            //run adaptor algorithm
    }

    private static void aggregation(){
        if(mode.equals("Remote")){
            //send all data back to local
        }
        if(mode.equals("Local")){
            //wait for all data from remote, then display data
            //is there a wait for reply command?
        }
    }
}