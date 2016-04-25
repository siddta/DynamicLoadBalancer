/**
 *
 */
package loadbalancer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Queue;

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
 */


//Main controller class. It starts the process, makes it listen on a particular port, creates necessary threads, handles their coordination and runs the boostrap, processing 
// and aggregation phase. 
public class Main {
    public static Queue<Job> jobQueue = new ArrayDeque<Job>(); 
    public static StateManager stateManager;
    public static HardwareMonitor hardwareMonitor;
    public static TransferManager transferManager;
    public static ArrayList<Job> processedJobList = new ArrayList<Job>();
    private static long startTime;
    private static long endTime;
    private static Thread hardwareMonitorThread;

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        if (args.length > 0) {
            //local .75 0
            //remote should have same policy as local
            Config.mode = args[0];
            Config.localthrottling = Double.parseDouble(args[1]);
            if (args.length >= 3) {
                Config.adaptor_policy = Integer.parseInt(args[2]);
            }
            if(args.length >= 4){
                Config.localHost = args[3]; //of form "http://localhost:2222/requests";
            }
            if(args.length >= 5){
                Config.localPort = Integer.parseInt(args[4]); //port of local (smaller) node
            }
            if(args.length >= 6){
                Config.remoteHost = args[5]; // of form "http://localhost:3333/requests";
            }
            if(args.length >= 7){
                Config.remotePort = Integer.parseInt(args[6]); //8080
            }
        }
        int port = Config.remotePort;

        if (Config.mode.equals("local")) {
            port = Config.localPort;

        }

        ResourceConfig config = new ResourceConfig();
        config.packages("loadbalancer");
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        try {
            server.start();

            main.bootstrap();
            main.processing();
            main.aggregation();
            server.join();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }

    }

    private void bootstrap() throws Exception {
        stateManager = new StateManager();
        hardwareMonitor = new HardwareMonitor(stateManager);
        transferManager = new TransferManager();
        stateManager.localState.throttlingValue = Config.localthrottling;


        if (Config.mode.equals("local")) {
            int totaljobs = Config.totalSize / Config.jobSize;
            for (int i = 0; i < totaljobs; i++) {
                int start = 0;
                int end = i + (Config.jobSize - 1);
                Job job = new Job(i, start, end);
                if (i < totaljobs / 2) {
                    HttpConnection.sendPost("addJob", job);
                    System.out.println("Initial transfer of job: " + i);
                } else {
                    jobQueue.add(job);
                }
            }
            Config.shouldStartProcessing = true;
            HttpConnection.sendGet("startProcessing");
        } else if (Config.mode.equals("remote")) {
            while (!Config.shouldStartProcessing) {
                Thread.sleep(10);
            }
        }


    }
    

    private void processing() throws InterruptedException {
        startTime = System.currentTimeMillis();

        stateManager.localState.stage = 1;

        System.out.println(Config.mode + ":Processing");
        //initialize worker_thread
        WorkerThread wk = new WorkerThread();
        wk.main = this;
        Thread workerThread = new Thread(wk);
        workerThread.start();

        //initialize hardware_monitor thread
        hardwareMonitorThread = new Thread(hardwareMonitor);
        hardwareMonitorThread.start();

        //initialize state_manager thread
        Thread stateManagerThread = new Thread(stateManager);
        stateManagerThread.start();

        while (stateManager.localState.stage < 2) { //aka end of processing state
            Thread.sleep(25);
        }
        endTime = System.currentTimeMillis();

    }

    private void aggregation() throws Exception {
        Config.monitor = false;
        System.out.println(Config.mode + ":Aggregation called");
        if (Config.mode.equals("remote")) {
            HttpConnection.sendPost("submitAggregatedResults", processedJobList);
            StateManager.localState.stage = 3;
            StateManager.sendLocalStatetoRemoteServer();
            Thread.sleep(10000);
            System.out.println("Remote Finished!");
        }
        if (Config.mode.equals("local")) {
            while (StateManager.remoteState.stage < 3) {
                Thread.sleep(10);
            }

            Collections.sort(processedJobList, new Comparator<Job>() {
                public int compare(Job a, Job b) {
                    return (Integer.valueOf(a.getJobId()).compareTo(b.getJobId()));
                }
            });
            for (Job b : processedJobList) {
                double[] values = b.getValues();
                int offset = b.getJobId() * Config.jobSize;
                for (int i = 0; i < Config.jobSize; i++)
                    System.out.println("JobId:" + b.getJobId() + "\t" + "Element:" + (offset + i) + "\t" + "value:" + values[i] + "\n");
            }
            System.out.println("Local Finished");
            System.out.println("Both local and remoted finished");
            System.out.println("Total Jobs Transferred: " + Config.jobsTransferred);
            System.out.println("Processing time taken in seconds: "  + (endTime-startTime)/1000);


        }
    }

}
    
