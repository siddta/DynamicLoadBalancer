package loadbalancer;

/**
 * Created by eideh on 4/23/2016.
 */
public class Adaptor {

	public Main main;
	
	public void apply_transfer_policy() throws InterruptedException, Exception{
		if(Config.adaptor_policy==0){
			main.transferManager.sendJobs();
		}
		else if (Config.adaptor_policy==1)
		{
			main.transferManager.receiveJobs();
		}
		else{
			main.transferManager.sendJobs();
			main.transferManager.receiveJobs();
		}
		
	}
	
	
}
