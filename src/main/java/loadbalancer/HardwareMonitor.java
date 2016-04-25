package loadbalancer;

import org.hyperic.sigar.*;

/**
 * Query system to monitor system state
 * CPU Usage
 * User defined throttling
 * Created by eideh on 4/23/2016.
 */
public class HardwareMonitor implements Runnable {

    //http://stackoverflow.com/questions/28039533/how-to-find-total-cpu-utilisation-in-java-using-sigar
    private static Sigar sigar = new Sigar();
    private StateManager stateManager;
    
    public HardwareMonitor(StateManager stateManager)
    {
    	this.stateManager=stateManager;
    }
   
    public void run() {
        while(true){
            //maybe sleep for awhile?
            try
            {
                Thread.sleep(Config.hardwareMontiorSleeptime);
            } catch (Exception e)
            {
                // TODO: handle exception
            }
            getSystemStatistics();
        }
    }


    public void getSystemStatistics(){
        Mem mem = null;
        CpuPerc cpuperc = null;
        FileSystemUsage filesystemusage = null;
        try {
            //mem = sigar.getMem();
            cpuperc = sigar.getCpuPerc();
            stateManager.localState.cpuUsePercent=cpuperc.getCombined()*100;
            //filesystemusage = sigar.getFileSystemUsage("C:");
        } catch (SigarException se) {
            se.printStackTrace();
        }
        //System.out.print(mem.getUsedPercent()+"\t");
        System.out.print( "CPU Usage " +(cpuperc.getCombined()*100)+"\n");
        //System.out.print(filesystemusage.getUsePercent()+"\n");
    }
}
