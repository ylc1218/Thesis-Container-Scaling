package ntu.dplab.thesis.containerscaling.scheduler;

import ntu.dplab.thesis.containerscaling.container.ContainerCost;
import ntu.dplab.thesis.containerscaling.container.ContainerList;
import ntu.dplab.thesis.containerscaling.trace.DeployTrace;
import ntu.dplab.thesis.containerscaling.trace.RequestTrace;

public class DpScheduler implements Scheduler{
	private final SCHED_TYPE type;	
	public static enum SCHED_TYPE{
		OPT, PREDICT;
	}
	
	public DpScheduler(SCHED_TYPE type){
		this.type = type;
	}
			
	@Override
	public DeployTrace schedule(RequestTrace req, ContainerList start) {
		assert (type == SCHED_TYPE.OPT):
			"Only OPT DpScheduler provides scheduling without predicted values.";
		
		// upper bound
		Scheduler boundScheduler = new GreedyScheduler(GreedyScheduler.SCHED_TYPE.FIT);
		double costUpBound = 172; //ContainerCost.totalCost(boundScheduler.schedule(req, start), req);
		
		DpRunner dpRunner = new DpRunner(req, start, costUpBound);
		return dpRunner.schedule();				
	}
	

	@Override
	public DeployTrace schedule(RequestTrace req, RequestTrace predict, ContainerList start) {
		assert (type == SCHED_TYPE.PREDICT): 
			"Only PREDICT DpScheduler provides scheduling with predicted values.";
		
		return null;
	}

}
