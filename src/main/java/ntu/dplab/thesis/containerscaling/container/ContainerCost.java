package ntu.dplab.thesis.containerscaling.container;

import ntu.dplab.thesis.containerscaling.constant.Constant;
import ntu.dplab.thesis.containerscaling.trace.DeployTrace;
import ntu.dplab.thesis.containerscaling.trace.RequestTrace;
import ntu.dplab.thesis.containerscaling.trace.TraceStatistic;

public class ContainerCost {
	private static int adjustmentCost(ContainerList pre, ContainerList now){
		int cnt = 0;
		for(int n = 0; n < Constant.N; n++){
			cnt += Math.max(0, now.get(n) - pre.get(n));
		}
		return cnt * Constant.ALPHA;
	}
	
	private static int insufficiencyCost(ContainerList pre, int curNeed){
		return (pre.resourceUnitCnt() < curNeed) ? Constant.BETA : 0;
	}
	
	public static double stepCost(ContainerList pre, ContainerList now, int curNeed){
		return adjustmentCost(pre, now) + insufficiencyCost(pre, curNeed);
	}
	
	public static double totalCost(DeployTrace deploy, RequestTrace req){
		// check the size of deployment and request
		assert deploy.size() == req.size():
			"Size of deploy trace ( " + deploy.size() + ") should be equal to "
			+ "the size of request trace (" + req.size() + ")";
		
		ContainerList[] deployment = deploy.getDeploymentArr();
		Integer[] reqNum = req.getNeedArr();
		int T = deployment.length;
		ContainerList pre = new ContainerList(); // zero list
		double totalCost = 0;
		
		for(int t = 0; t < T; t++){
			ContainerList now = deployment[t];
			assert (now.resourceUnitCnt() >= reqNum[t]):
				"At step " + t + ": deployed #unit (" + now.resourceUnitCnt() + ") should >= " + reqNum[t];
			
			totalCost += stepCost(pre, now, reqNum[t]);
			pre = now;
		}
		return totalCost;
	}
	
	public static TraceStatistic constructTraceStat(DeployTrace deploy, RequestTrace req){
		// check the size of deployment and request
		assert deploy.size() == req.size():
			"Size of deploy trace ( " + deploy.size() + ") should be equal to "
			+ "the size of request trace (" + req.size() + ")";
		
		ContainerList[] deployment = deploy.getDeploymentArr();
		Integer[] reqNum = req.getNeedArr();
		ContainerList pre = new ContainerList(); // zero list
		int adjCost = 0, insufCost = 0;
		int T = deployment.length;
		
		for(int t = 0; t < T; t++){
			ContainerList now = deployment[t];
			assert (now.resourceUnitCnt() >= reqNum[t]):
				"At step " + t + ": deployed #unit (" + now.resourceUnitCnt() + ") should >= " + reqNum[t];
			adjCost += adjustmentCost(pre, deployment[t]);
			insufCost += insufficiencyCost(pre, reqNum[t]);
			pre = now;
		}
		return new TraceStatistic(adjCost, insufCost, deployment, reqNum);
	}
}
