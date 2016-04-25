package loadbalancer;

/**
 * What a job of our entire task is
 * Created by eideh on 4/23/2016.
 */
public class Job {
   

	private boolean done=false;
    private int jobId;
	private int start_index; //inclusive
    private int end_index; //inclusive
    private double [] values;

    public Job(int jobId, int start, int end){
    	this.jobId=jobId;
        this.start_index = start;
        this.end_index = end;
        values= new double [Config.jobSize];
        for(int i=0;i<Config.jobSize;i++){
        	values[i]=1.11111;
        }        
    	System.out.println(this.jobId);
    }
    public Job(){
    	
    }
    
    public void setDone(){
    	done=true;
    }
    
    public boolean isDone(){
    	return done;
    }
    
     
  
    /**
   	 * @return the jobId
   	 */
   	public int getJobId() {
   		return jobId;
   	}

   	/**
   	 * @param jobId the jobId to set
   	 */
   	public void setJobId(int jobId) {
   		this.jobId = jobId;
   	}

   	/**
   	 * @return the start_index
   	 */
   	public int getStart_index() {
   		return start_index;
   	}

   	/**
   	 * @param start_index the start_index to set
   	 */
   	public void setStart_index(int start_index) {
   		this.start_index = start_index;
   	}

   	/**
   	 * @return the end_index
   	 */
   	public int getEnd_index() {
   		return end_index;
   	}

   	/**
   	 * @param end_index the end_index to set
   	 */
   	public void setEnd_index(int end_index) {
   		this.end_index = end_index;
   	}

   	/**
   	 * @return the values
   	 */
   	public double[] getValues() {
   		return values;
   	}

   	/**
   	 * @param values the values to set
   	 */
   	public void setValues(double[] values) {
   		this.values = values;
   	}

   	/**
   	 * @param done the done to set
   	 */
   	public void setDone(boolean done) {
   		this.done = done;
   	}


    
    }
    
