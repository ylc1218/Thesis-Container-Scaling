package ntu.dplab.thesis.containerscaling.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ntu.dplab.thesis.containerscaling.constant.Constant;

public class ContainerUtil {
	public ContainerUtil() {}
	
	/**
	 	Possible combinations
	*/	
	
	private static void dfs(ArrayList<ContainerList> retList, int[] l, int id, int target){
		// boundary
		if (target <= 0 || id == Constant.N){
			// fill the rest with zero
			for(int n = id; n < Constant.N; n++){
				l[n] = 0;
			}
			// check if resource units are enough
			if (target <= 0){
				retList.add(new ContainerList(l));
			}
			return;
		}
		
		// maximum number of id-th container
		int maxChoose = (int) Math.ceil((double)target / Constant.UNIT_MUL[id]);  
		for (int x = 0; x <= maxChoose; x++){
			l[id] = x;
			dfs(retList, l, id + 1, target - x * Constant.UNIT_MUL[id]);
		}
		
	}
	
	public static List<ContainerList> getPossibleCombination(int target){
		ArrayList<ContainerList> retList = new ArrayList<ContainerList>();
		dfs(retList, new int[Constant.N], 0, target);
		
		// choose only the minimum number of resource units
		Collections.sort(retList);
		int minNum = retList.size() == 0 ? 0 : retList.get(0).resourceUnitCnt();
		int idx = 0;
		while(idx < retList.size() && retList.get(idx).resourceUnitCnt() == minNum){
			idx++;
		}
		
		return retList.subList(0, idx);
	}
	
	private static void dfs(ArrayList<ContainerList> retList, ContainerList l, int id, int minReq, int maxReq){
		// boundary
		if (id == Constant.N - 1){
			int maxChoose = Math.max(0, (maxReq - l.resourceUnitCnt()) / Constant.UNIT_MUL[id]);
			int minChoose = (int) Math.max(0, Math.ceil((double)(minReq - l.resourceUnitCnt()) / Constant.UNIT_MUL[id]));
			
			for(int x = minChoose; x <= maxChoose; x++){
				l.set(id, x);
				retList.add(l.clone());
			}
			l.set(id, 0);
			return;
		}
		
		// maximum number of id-th container
		int maxChoose = Math.max(0, (maxReq - l.resourceUnitCnt()) / Constant.UNIT_MUL[id]);
		for (int x = 0; x <= maxChoose; x++){
			l.set(id, x);
			dfs(retList, l, id + 1, minReq, maxReq);			
		}		
		l.set(id, 0);
	}
	
	public static List<ContainerList> getPossibleCombination(int minReq, int maxReq){
		ArrayList<ContainerList> retList = new ArrayList<ContainerList>();
		dfs(retList, new ContainerList(), 0, minReq, maxReq);						
		return retList;
	}
	
	
	/**
 		Constraints
	*/	
	
	public static int containerUpBound(int base){
		return (100 * base) / Constant.U;
	}
	
	public static boolean fitConstraints(ContainerList deploy, int need){
		int have = deploy.resourceUnitCnt();
		return (have >= need) && (have <= containerUpBound(need));				
	}
}
