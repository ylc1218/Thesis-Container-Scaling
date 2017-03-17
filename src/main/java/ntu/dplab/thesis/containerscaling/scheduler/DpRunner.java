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

public class DpRunner {
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
	
	private final List<Cell>[] possibleCells;
	private final Integer[] needArr;
	private final double[][] dp;
	private final Cell[][] from;
	private final List<ContainerList> combinationLists;
	private final int T, L;	
	private final double costUpBound;
	private final ContainerList start;
	
	
	@SuppressWarnings("unchecked")
	public DpRunner(RequestTrace req, ContainerList start, double costUpBound) {
		possibleCells = (ArrayList<Cell>[])new ArrayList[2];
		needArr = req.getNeedArr();
		combinationLists = getPossibleCombination(start);
		
		T = req.size();
		L = combinationLists.size();
		dp = new double[2][L];
		from = new Cell[T][L];
		
		this.costUpBound = costUpBound;
		this.start = start;			
	}

	private DeployTrace backTrack(){
		int arrId = T % 2;
		DeployTrace deployment = new DeployTrace(T);
		
		// find minimum value
		double minCost = costUpBound;
		Cell bestCell = null;
		for(Cell now : possibleCells[arrId]){
			int id = now.getId();
			if (dp[arrId][id] < minCost){ //TODO: modify the case of dp[id] == minCost				
				minCost = dp[arrId][id];
				bestCell = now;
			}
			else if (dp[arrId][id] == minCost && (bestCell == null || id > bestCell.id)){
				bestCell = now;
			}
		}
		System.out.println("minCost = " + minCost);
				
		// construct the trace
		ContainerList[] trace = new ContainerList[T];
		for(int t = T - 1; t >= 0; t--){
			trace[t] = bestCell.getList();
			bestCell = from[t][bestCell.getId()];			
		}
		deployment.addAll(trace);
		
		return deployment;
	}
	
	private Cell findPrevBestCell(int curNeed, ContainerList now, int arrId){		
		double bestCost = costUpBound;
		Cell bestCell = null;
	   
		//check constraint
		if (!ContainerUtil.fitConstraints(now, curNeed)){
			return null;
		}
		
		// enumerate previous states
		for(Cell preCell : possibleCells[arrId]){ 
			ContainerList pre = preCell.getList();
			double preCost = dp[arrId][preCell.getId()];										
			double cost = preCost + ContainerCost.stepCost(pre, now, curNeed);
			if (cost < bestCost){ //TODO: modify cost == bestCost case
				bestCost = cost;
				bestCell = preCell;
			}
			else if (cost == bestCost && (bestCell == null || bestCell.id < preCell.id)){
				bestCell = preCell;
			}
		}
		
		return bestCell;
	}
	
	// get all possible combinations [minReq, maxReq]	
	private List<ContainerList> getPossibleCombination(ContainerList start){
		int minReq = Collections.min(Arrays.asList(needArr));
		int maxReq = Collections.max(Arrays.asList(needArr));
		
		ContainerUtil util = new ContainerUtil();
		return util.getPossibleDpCombination(minReq, maxReq, start);
	}
	
	private void runTimeStep(final int t){
		final int preId = t % 2,  nowId = preId ^ 1;
		ExecutorService executorService = Executors.newFixedThreadPool(16); // number of threads
		try{
			for(int i = 0; i < L; i++){ // enumerate current states
				final ContainerList now = combinationLists.get(i);
				final int nI = i;
				
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						Cell bestCell = findPrevBestCell(needArr[t], now, preId);
						if (bestCell == null) return;
						
						// update current state
						synchronized (possibleCells) {								
							dp[nowId][nI] = dp[preId][bestCell.getId()] + 
											ContainerCost.stepCost(bestCell.getList(), now, needArr[t]);								
							from[t][nI] = bestCell;
							possibleCells[nowId].add(new Cell(nI, now));
						}
			       }
				});				
			}
		} finally{
			// don't accept new task
			executorService.shutdown(); 
			try {
				// wait for tasks in executor to finish
				executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void runTimeSteps(){		
		for (int t = 0; t < T; t++){ // for every step						
			possibleCells[(t % 2) ^ 1].clear();
			System.out.println(t + " " + possibleCells[(t % 2)].size());
			runTimeStep(t);			
		}
	}
	
	public DeployTrace schedule(){
		System.out.println("# combinationLists = " + L);
		int arrId = 0;
		
		// init dp state
		possibleCells[arrId] = new ArrayList<Cell>();
		possibleCells[arrId ^ 1] = new ArrayList<Cell>();				
		possibleCells[arrId].add(new Cell(0, start));
		dp[arrId][0] = 0;		
		
		runTimeSteps();
		
		return backTrack();
	}
}
