package loadbalancer;

/**
 * Created by eideh on 4/23/2016.
 */
public class Adaptor {

	
	public void apply_transfer_policy() throws InterruptedException, Exception{
		if(Config.adaptor_policy==0){
			TransferManager.sendJobs();
		}
		else if (Config.adaptor_policy==1)
		{
			TransferManager.receiveJobs();
		}
		else{
			TransferManager.sendJobs();
			TransferManager.receiveJobs();
		}
		
	}
	
	
}
