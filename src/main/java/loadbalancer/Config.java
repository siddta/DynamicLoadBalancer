/**
 * 
 */
package loadbalancer;

/**
 * @author tarique
 *
 */
public class Config {
	 public final static int jobSize = 8192; //1024*1024*4/512
	 public final static int totalSize = 4194304; //1024*1024*4
//	 public final static int totalSize = 419430; //1024*1024*4
	 public final static int sum_iteration = 6000;
	 public final static String localHost="http://localhost:2222/requests";
	 public final static String remoteHost="http://localhost:3333/requests"; 
	 public static String mode;
	 public static double localthrottling=.75;
	 public static boolean shouldStartProcessing=false;
	 public static final Object QUEUE_LOCK = new Object();
	 public static final int hardwareMontiorSleeptime = 500;
	 public static final int stateManagerSleeptime = 100;
	 public static  int adaptor_policy = 0;  // 0=send, 1=receive, 2=mixed

	
}
