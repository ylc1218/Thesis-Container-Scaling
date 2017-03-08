package ntu.dplab.thesis.containerscaling.trace;

import ntu.dplab.thesis.containerscaling.container.ContainerList;

public class TraceStatistic {
	private int adjCost, insufCost;
	private double avgUtil;
	private StepStatistic[] steps;	
	
	
	// statistic of each step
	private class StepStatistic{
		private int need, have;
		private double utilization;
		private ContainerList deploy;
		
		private StepStatistic(int need, ContainerList deploy){
			this.need = need;
			this.deploy = deploy;
			this.have = deploy.resourceUnitCnt();
			utilization = (need * 100.0) / have;
		}
	}
	
	public TraceStatistic(int adjCost, int insufCost, ContainerList[] deploys, Integer[] req) {
		this.adjCost = adjCost;
		this.insufCost = insufCost;
		int T = deploys.length;
		
		avgUtil = 0;
		steps = new StepStatistic[T];
		for(int t = 0 ; t < T; t++){
			steps[t] = new StepStatistic(req[t], deploys[t]);
			avgUtil += steps[t].utilization;
		}
		avgUtil /= T;
	}
	
	public void print(){
		System.out.println(String.format("Adj = %d, Insuf = %d, Total = %d, Util = %.2f%%", adjCost, insufCost, adjCost + insufCost, avgUtil));
	}

	public void printTrace(){
		int T = steps.length;
		for(int t = 0; t < T; t++){
			StepStatistic step = steps[t];
			System.out.println(String.format("%d %d %d %.2f%% " + step.deploy, t, step.need, step.have, step.utilization));
		}
	}
	
}
