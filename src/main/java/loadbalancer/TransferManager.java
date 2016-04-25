package loadbalancer;

import java.util.concurrent.BlockingQueue;

import static loadbalancer.Config.QUEUE_LOCK;

/**
 * Transfers jobs between phones
 * Created by eideh on 4/23/2016.
 */
public class TransferManager implements Runnable {

    public BlockingQueue<Job> transferQueue;


    public TransferManager(BlockingQueue<Job> transferQueue){
        this.transferQueue = transferQueue;
    }


    public void run() {
        //main queue processing
        try {
            while (true) {
                //take() from queue
                synchronized (QUEUE_LOCK) {
                    while (Main.transferSize > 0) {
                        Job job = transferQueue.take();
                        HttpConnection.sendPost("addJob", job);
                    }
                }
                Thread.sleep(10);
            }
        } catch (Exception e) {
            //if interrupted while taking
        }
    }
}
