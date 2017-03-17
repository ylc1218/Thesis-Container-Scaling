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
	
	private DeployTrace optSchedule(RequestTrace req, ContainerList start){
		// upper bound		
		Scheduler boundScheduler = new GreedyScheduler(SCHED_TYPE.GREEDY_FIT);		
		double costUpBound = ContainerCost.totalCost(boundScheduler.schedule(req, start), req);
		
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
			
			DeployTrace subTrace = optSchedule(subReq, pre);
			pre = subTrace.getDeploymentArr()[0];
			trace.add(pre);
		}
		
		return trace;
	}
			
	@Override
	public DeployTrace schedule(RequestTrace req, ContainerList start) {
		assert (type == SCHED_TYPE.OPT_DP):
			"Only OPT DpScheduler provides scheduling without predicted values.";
		return optSchedule(req, start);						
	}
	

	@Override
	public DeployTrace schedule(RequestTrace req, RequestTrace predict, ContainerList start) {
		assert (type == SCHED_TYPE.PREDICTION_DP): 
			"Only PREDICT DpScheduler provides scheduling with predicted values.";		
		return predictSchedule(req, predict, start);
	}

}
