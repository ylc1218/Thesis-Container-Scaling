package ntu.dplab.thesis.containerscaling.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ntu.dplab.thesis.containerscaling.container.ContainerCost;
import ntu.dplab.thesis.containerscaling.container.ContainerList;
import ntu.dplab.thesis.containerscaling.container.ContainerUtil;
import ntu.dplab.thesis.containerscaling.trace.DeployTrace;
import ntu.dplab.thesis.containerscaling.trace.RequestTrace;

public class MaxScheduler implements Scheduler{
	public MaxScheduler() {}
	
	private ContainerList scheduleStep(ContainerList pre, List<ContainerList> possible, int curNeed){
		double minCost = Double.POSITIVE_INFINITY;
		ContainerList bestList = null;
		for(ContainerList now : possible){
			double cost = ContainerCost.stepCost(pre, now, curNeed);
			if (cost < minCost){
				minCost = cost;
				bestList = now;
			}
		}
		return bestList;
	}
	
	public DeployTrace schedule(RequestTrace req, ContainerList start) {
		Integer[] needArr = req.getNeedArr();
		int maxReqUnit = Collections.max(Arrays.asList(needArr));
		ContainerList pre = start;
		DeployTrace deployment = new DeployTrace(needArr.length);
		
		for(int curNeed : needArr){
			int target = Math.min(maxReqUnit, ContainerUtil.containerUpBound(curNeed));
			List<ContainerList> possible = ContainerUtil.getPossibleCombination(target);
			pre = scheduleStep(pre, possible, curNeed);
			
			deployment.add(pre);
		}
		
		return deployment;
	}

	public DeployTrace schedule(RequestTrace req, RequestTrace predict, ContainerList start) {
		assert false: "MaxSchedluer does not provide scheduling with predicted value";
		return null;
	}
	
}
