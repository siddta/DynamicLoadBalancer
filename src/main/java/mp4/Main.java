/**
 * 
 */
package mp4;

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

/**
 * @author tarique
 *
 */
public class Main extends AbstractHandler
{
	private static String mode="Local";
	private static double[] A;
    private final static int jobSize = 8192; //1024*1024*4/512
    private final static int totalSize = 4194304; //1024*1024*4
    private static BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<Job>(); // do we need a blocking queue?
    private static int count = 0;
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Hello World</h1>");
        //String message = "Hello World!";
        //request.setAttribute("message", message); // This will be available as ${message}
        //request.getRequestDispatcher("page.jsp").forward(request, response);


    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8081);
        server.setHandler(new Main());

        server.start();

        A = new double[totalSize];
        // call boostrap
        bootstrap();
        processing();
        aggregation();

        server.join();

    }

	private static void bootstrap(){
        if(mode.equals("Local")){
            //Create the 512 jobs (add them to queue)
            //eg jobSize = 10
            //(start = 0, end = 9)
            //(start = 10, end = 19)
            for(int i = 0; i < totalSize; i+=jobSize){
                int start = 0;
                int end = i+(jobSize-1);
                Job job = new Job(start,end);
                try {
                    jobQueue.put(job);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //start transfer of half of them to remote node
            //from overloaded local queue to remote queue
        }
    }
    //main loop
    private static void processing(){
        System.out.println("hiww");
        //initialize hardware_monitor thread
        HardwareMonitor hardwareMonitor = new HardwareMonitor(count++);
        Thread hardwareMonitorThread = new Thread(hardwareMonitor);
        hardwareMonitorThread.start();

        //initialize state_manager thread
        Thread stateManagerThread = new Thread(new StateManager(jobQueue, hardwareMonitor));
        stateManagerThread.start();

        //initialize transfer_manager thread
        //should the transferManager have a different queue??
        //should the transferManager even be its own thread, or just part of the adaptor thread?
        Thread transferThread = new Thread(new TransferManager(jobQueue));
        transferThread.start();

        //initialize worker_thread
        Thread workerThread = new Thread(new WorkerThread(jobQueue, 70));
        workerThread.start();

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