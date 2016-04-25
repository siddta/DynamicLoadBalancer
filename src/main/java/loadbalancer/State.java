package loadbalancer;

public class State{
	
	int pendingJobs;
    double throttlingValue;
    double cpuUsePercent;
    int stage=0;  // 0=boostrap, 1=processing, 2=aggregation, 3=done.
    

    /**
	 * @return the pendingJobs
	 */
	public int getPendingJobs() {
		return pendingJobs;
	}



	/**
	 * @param pendingJobs the pendingJobs to set
	 */
	public void setPendingJobs(int pendingJobs) {
		this.pendingJobs = pendingJobs;
	}



	/**
	 * @return the throttlingValue
	 */
	public double getThrottlingValue() {
		return throttlingValue;
	}



	/**
	 * @param throttlingValue the throttlingValue to set
	 */
	public void setThrottlingValue(double throttlingValue) {
		this.throttlingValue = throttlingValue;
	}



	/**
	 * @return the cpuUsePercent
	 */
	public double getCpuUsePercent() {
		return cpuUsePercent;
	}



	/**
	 * @param cpuUsePercent the cpuUsePercent to set
	 */
	public void setCpuUsePercent(double cpuUsePercent) {
		this.cpuUsePercent = cpuUsePercent;
	}



	/**
	 * @return the stage
	 */
	public int getStage() {
		return stage;
	}



	/**
	 * @param stage the stage to set
	 */
	public void setStage(int stage) {
		this.stage = stage;
	}

	 public State() {
	        
	 }


    public State(int pendingJobs, int throttlingValue, double cpuUsePercent) {
        this.pendingJobs = pendingJobs;
        this.throttlingValue = throttlingValue;
        this.cpuUsePercent = cpuUsePercent;
        this.stage=0;
    }

}