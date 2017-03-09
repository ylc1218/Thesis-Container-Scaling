package ntu.dplab.thesis.containerscaling.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ntu.dplab.thesis.containerscaling.container.ContainerCost;
import ntu.dplab.thesis.containerscaling.container.ContainerList;
import ntu.dplab.thesis.containerscaling.container.ContainerUtil;
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
	
	private class Cell{
		private int id;
		private ContainerList l;
		
		private Cell(int id, ContainerList l){
			this.id = id;
			this.l = l.clone();
		}
		
		public ContainerList getList(){
			return l;
		}
		
		public int getId(){
			return id;
		}
		
	}		
	
	private DeployTrace backTrack(double[] dp, List<Cell> possibleCells, Cell[][] from, int T){
		DeployTrace deployment = new DeployTrace(T);
		
		// find minimum value
		double minCost = Double.POSITIVE_INFINITY;
		Cell bestCell = null;
		for(Cell now : possibleCells){
			int id = now.getId();
			if (dp[id] < minCost){ //TODO: modify the case of dp[id] == minCost
				minCost = dp[id];
				bestCell = now;
			}
			else if (dp[id] == minCost && id < bestCell.id){
				bestCell = now;
			}
		}
				
		// construct the trace
		ContainerList[] trace = new ContainerList[T];
		for(int t = T - 1; t >= 0; t--){
			trace[t] = bestCell.getList();
			bestCell = from[t][bestCell.getId()];			
		}
		deployment.addAll(trace);
		
		return deployment;
	}
	
	private Cell findPrevBestCell(double costUpBound, int curNeed, ContainerList now, List<Cell> possibleCells, double[] dp){
		double bestCost = costUpBound;
		Cell bestCell = null;
	   
		//check constraint
		if (!ContainerUtil.fitConstraints(now, curNeed)){
			return null;
		}
		
		// enumerate previous states
		for(Cell preCell : possibleCells){ 
			ContainerList pre = preCell.getList();
			double preCost = dp[preCell.getId()];										
			double cost = preCost + ContainerCost.stepCost(pre, now, curNeed);
			if (cost < bestCost){ //TODO: modify cost == bestCost case
				bestCost = cost;
				bestCell = preCell;
			}
			else if (cost == bestCost && (bestCell == null || preCell.id < bestCell.id)){
				bestCell = preCell;
			}
		}						
		
		return bestCell;
	}
	
	// get all possible combinations [minReq, floor(maxReq / U)]	
	private List<ContainerList> getPossibleCombination(RequestTrace req){
		Integer[] needArr = req.getNeedArr();
		int minReq = Collections.min(Arrays.asList(needArr));
		int maxReq = Collections.max(Arrays.asList(needArr));
		return ContainerUtil.getPossibleCombination(minReq, ContainerUtil.containerUpBound(maxReq));
	}
	
	@Override
	public DeployTrace schedule(RequestTrace req, ContainerList start) {
		assert (type == SCHED_TYPE.OPT): "Only OPT DpScheduler provides scheduling without predicted values.";
		
		@SuppressWarnings("unchecked")
		final List<Cell>[] possibleCells = (ArrayList<Cell>[])new ArrayList[2];
		final Integer[] needArr = req.getNeedArr();
		List<ContainerList> combinationLists = getPossibleCombination(req);
		
		int T = req.size(), L = combinationLists.size();
		int nowId = 0, preId = 1;
		 						
		System.out.println("# combinationLists = " + L);
		
		// dp table
		final double[][] dp = new double[2][L];
		final Cell[][] from = new Cell[T][L];
		
		// init
		possibleCells[nowId] = new ArrayList<Cell>();
		possibleCells[preId] = new ArrayList<Cell>();				
		possibleCells[nowId].add(new Cell(0, start));
		dp[nowId][0] = 0;
		
		// upper bound
		Scheduler boundScheduler = new GreedyScheduler(GreedyScheduler.SCHED_TYPE.FIT);
		final double costUpBound = ContainerCost.totalCost(boundScheduler.schedule(req, start), req);
				
		for (int t = 0; t < T; t++){ // for every step			
			nowId ^= 1; preId ^= 1;
			possibleCells[nowId].clear();
			System.out.println(t + " " + possibleCells[preId].size());
			
			ExecutorService executorService = Executors.newFixedThreadPool(32); // number of threads
			try{
				for(int i = 0; i < L; i++){ // enumerate current states
					final ContainerList now = combinationLists.get(i);
					final int nId = nowId, pId = preId;
					final int nT = t, nI = i;
					
					executorService.submit(new Runnable() {
						@Override
						public void run() {
							Cell bestCell = findPrevBestCell(costUpBound, needArr[nT], now, possibleCells[pId], dp[pId]);
							if (bestCell == null) return;
							
							// update current state
							synchronized (possibleCells) {								
								dp[nId][nI] = ContainerCost.stepCost(bestCell.getList(), now, needArr[nT]);
								from[nT][nI] = bestCell;
								possibleCells[nId].add(new Cell(nI, now));
							}
				       }
					});				
				}
			} finally{
				executorService.shutdown(); // don't accept new task
				try {
					executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // wait for tasks in executor to finish
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		}
		
		return backTrack(dp[nowId], possibleCells[nowId], from, T);
	}
	

	@Override
	public DeployTrace schedule(RequestTrace req, RequestTrace predict, ContainerList start) {
		assert (type == SCHED_TYPE.PREDICT): "Only PREDICT DpScheduler provides scheduling with predicted values.";
		
		return null;
	}

}
