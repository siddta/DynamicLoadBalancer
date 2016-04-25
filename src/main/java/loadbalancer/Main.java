/**
 * 
 */
package loadbalancer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public static BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<Job>(); // do we need a blocking queue?
    private static StateManager stateManager;
    private static HardwareMonitor hardwareMonitor;
    public static ArrayList<Job> processedJobList = new ArrayList<Job>();
 
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
        	     aggregation();
    	     }
    	     
    	     server.join();
    	     
    	 } finally {
    	     server.destroy();
   	 }

    }

	private static void bootstrap() throws Exception{
	    stateManager=new StateManager();
		hardwareMonitor = new HardwareMonitor(stateManager);
     
      
        
        if( Config.mode.equals("local")){
            //Create the 512 jobs (add them to queue)
            //eg jobSize = 10
            //(start = 0, end = 9)
            //(start = 10, end = 19)
            for(int i = 0; i < Config.totalSize; i+=Config.jobSize){
                int start = 0;
                int end = i+(Config.jobSize-1);
                Job job = new Job(i%Config.jobSize,start,end);
                while(i<Config.totalSize/2){
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
         
        //initialize worker_thread
        Thread workerThread = new Thread(new WorkerThread(jobQueue, 70));
        workerThread.start();
        
        //initialize hardware_monitor thread
        Thread hardwareMonitorThread = new Thread(hardwareMonitor);
        hardwareMonitorThread.start();
        
        //initialize state_manager thread
        Thread stateManagerThread = new Thread(stateManager);
        stateManagerThread.start();

    }

    private static void aggregation() throws Exception{
        if(Config.mode.equals("Remote")){
        	HttpConnection.sendPost("submitAggregatedResults",processedJobList);
        }
        if(Config.mode.equals("Local")){
           while(stateManager.remoteState.stage<3)
           {
        	   Thread.sleep(10);
           }
         
           Collections.sort(processedJobList,new Comparator<Job>() {
			public int compare(Job a, Job b) {
				return (Integer.valueOf(a.getJobId()).compareTo(b.getJobId()));
			}});
           
        }
        
        for(Job b:processedJobList){
        	double [] values=b.getValues();
        	int offset=b.getJobId()*Config.jobSize;
        	for(int i=0;i<Config.jobSize;i++)
    		System.out.println("JobId:"+b.getJobId()+"\t"+"Element:"+(offset+i)+"\t"+"value:"+values[i]+"\n");
        } 
        }
        
}
    
