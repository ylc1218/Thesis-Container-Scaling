package ntu.dplab.thesis.containerscaling.trace;

import ntu.dplab.thesis.containerscaling.container.ContainerList;

public class TraceStatistic {
	private int adjCost, insufCost;
	private StepStatistic[] steps;
	private ContainerList[] deploy;
	
	// statistic of each step
	private class StepStatistic{
		private int need, have;
		private double utilization;
		
		private StepStatistic(int need, int have){
			this.need = need;
			this.have = have;
			utilization = (need * 100.0) / have;
		}
	}
	
	public TraceStatistic(int adjCost, int insufCost, ContainerList[] arg_deploy, Integer[] req) {
		this.adjCost = adjCost;
		this.insufCost = insufCost;
		deploy = arg_deploy.clone();
		int T = deploy.length;
		
		steps = new StepStatistic[T];
		for(int t = 0 ; t < T; t++){
			steps[t] = new StepStatistic(req[t], deploy[t].resourceUnitCnt());
		}
	}
	
	public void print(){
		System.out.println(String.format("Adj = %d\tInsuf=%d\tTotal=%d\t", adjCost, insufCost, adjCost + insufCost));
	}

	
}
