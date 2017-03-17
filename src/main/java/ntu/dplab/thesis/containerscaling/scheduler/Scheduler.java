package ntu.dplab.thesis.containerscaling.scheduler;

import ntu.dplab.thesis.containerscaling.container.ContainerList;
import ntu.dplab.thesis.containerscaling.trace.DeployTrace;
import ntu.dplab.thesis.containerscaling.trace.RequestTrace;

public abstract class Scheduler{
	public final SCHED_TYPE type;
	public static enum SCHED_TYPE{
		GREEDY_MAX, GREEDY_FIT, OPT_DP, PREDICTION_DP;
	}
	
	public Scheduler(SCHED_TYPE type){
		this.type = type;
	}
	
	abstract public DeployTrace schedule(RequestTrace req, ContainerList start);
	abstract public DeployTrace schedule(RequestTrace req, RequestTrace predict, ContainerList start);
}
