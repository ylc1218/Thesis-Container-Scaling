package ntu.dplab.thesis.containerscaling.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ntu.dplab.thesis.containerscaling.constant.Constant;

public class ContainerUtil {
	public ContainerUtil() {}
	
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
	
	public static int containerUpBound(int base){
		return (100 * base) / Constant.U;
	}
}
