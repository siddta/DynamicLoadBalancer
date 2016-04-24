package mp4;

/**
 * Created by eideh on 4/23/2016.
 */
public class Adaptor {

    //State manager reports remote state to adaptor
    //Hardware monitor reports local state to adaptor

    //Adaptor decides whether to transfer a job or continue with working thread

    //Adaptor will follow sender-initiated algorithm
    //!!!!! A basic transfer policy: totalJobsRemaining = localState.pendingJobs + remoteState.pendingJobs
    // if localSate.pendingJObs / (double) totalJobsRemaining > some percentage, transfer until local percent = 40, remote percent = 60 (or something)

    //Adaptor sends transfer decision to transfer manager (YES/NO)
    //Adaptor sends throttling info to worker thread
}
