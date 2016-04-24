package mp4;

public class State{

    int pendingJobs;
    int throttlingValue;
    double cpuUsePercent;

    public int getPendingJobs() {
        return pendingJobs;
    }

    public void setPendingJobs(int pendingJobs) {
        this.pendingJobs = pendingJobs;
    }

    public int getThrottlingValue() {
        return throttlingValue;
    }

    public void setThrottlingValue(int throttlingValue) {
        this.throttlingValue = throttlingValue;
    }

    public double getCpuUsePercent() {
        return cpuUsePercent;
    }

    public void setCpuUsePercent(double cpuUsePercent) {
        this.cpuUsePercent = cpuUsePercent;
    }

    public State(int pendingJobs, int throttlingValue, double cpuUsePercent) {
        this.pendingJobs = pendingJobs;
        this.throttlingValue = throttlingValue;
        this.cpuUsePercent = cpuUsePercent;
    }

}