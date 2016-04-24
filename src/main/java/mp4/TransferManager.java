package mp4;

import java.util.concurrent.BlockingQueue;

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
                transferQueue.take();
            }
        } catch (InterruptedException e) {
            //if interrupted while taking
        }
    }
}
