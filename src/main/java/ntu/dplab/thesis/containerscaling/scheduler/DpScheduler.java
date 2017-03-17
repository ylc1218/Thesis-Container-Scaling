package ntu.dplab.thesis.containerscaling.scheduler;

import ntu.dplab.thesis.containerscaling.constant.Constant;
import ntu.dplab.thesis.containerscaling.container.ContainerCost;
import ntu.dplab.thesis.containerscaling.container.ContainerList;
import ntu.dplab.thesis.containerscaling.trace.DeployTrace;
import ntu.dplab.thesis.containerscaling.trace.RequestTrace;

public class DpScheduler extends Scheduler{		
	public DpScheduler(SCHED_TYPE type){
		super(type);
	}
	
	
	// use GREEDY_FIT as base cost
	private DeployTrace optSchedule(RequestTrace req, RequestTrace predict, ContainerList start){
		// upper bound		
		double costUpBound = Double.POSITIVE_INFINITY;
		if (predict == null){
			Scheduler boundScheduler = new GreedyScheduler(SCHED_TYPE.GREEDY_FIT);
			costUpBound = ContainerCost.totalCost(boundScheduler.schedule(req, start), req);
		}
		else{
			Scheduler boundScheduler = new DpScheduler(SCHED_TYPE.OPT_DP);
			costUpBound = ContainerCost.totalCost(boundScheduler.schedule(req, predict, start), req);
		}		
		
		DpRunner dpRunner = new DpRunner(req, start, costUpBound);
		return dpRunner.schedule();
	}
	
	private DeployTrace predictSchedule(RequestTrace req, RequestTrace predict, ContainerList start){
		int T = req.size();
		DeployTrace trace = new DeployTrace(T);
		ContainerList pre = start;
		
		for(int t = 0; t < T; t++){
			RequestTrace subReq = new RequestTrace();
			subReq.add(req.get(t));
			subReq.add(predict.get(t) + (int) Math.ceil(Constant.ALPHA / (double) Constant.BETA));
			
			DeployTrace subTrace = optSchedule(subReq, null, pre);
			pre = subTrace.getDeploymentArr()[0];
			trace.add(pre);
		}
		
		return trace;
	}
			
	@Override
	public DeployTrace schedule(RequestTrace req, ContainerList start) {
		assert (type == SCHED_TYPE.OPT_DP):
			"Only OPT DpScheduler provides scheduling without predicted values.";
		return optSchedule(req, null, start);						
	}
	

	@Override
	public DeployTrace schedule(RequestTrace req, RequestTrace predict, ContainerList start) {
		switch(type){
			case OPT_DP:
				return optSchedule(req, predict, start);
			case PREDICTION_DP:
				return predictSchedule(req, predict, start);
			default:
				assert false: "Unknown type: " + type;					
		}		
		return null;
	}

}
