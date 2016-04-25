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
        values= new double [10];
        for(int i=0;i<config.jobSize;i++){
        	values[i]=1.11111;
        }        
    }
    
    public void setDone(){
    	done=true;
    }
    
    public boolean isDone(){
    	return done;
    }
    
    public void execute(){
    	for(int i=0;i<config.jobSize;i++)
    		for(int j=0;j<config.sum_iteration;j++){{
        	values[i]+=1.11111;
        } 
    }
    }
    
    public void print(){
    	for(int i=0;i<config.jobSize;i++)
    		System.out.println("JobId:"+jobId+"\t"+"Element:"+i+"\t"+"value:"+values[i]+"\n");
        } 
    }
    
