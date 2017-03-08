package ntu.dplab.thesis.containerscaling.container;

import java.util.Arrays;

import ntu.dplab.thesis.containerscaling.constant.Constant;
public class ContainerList implements Comparable<ContainerList>{
	private int[] num;
	
	public ContainerList() {
		num = new int[Constant.N];
	}
	
	public ContainerList(int[] num){
		assert (num.length == Constant.N): 
			"num.length should be " + Constant.N + " instead of " + num.length;
		this.num = num.clone();
	}
	
	public int get(int idx){
		return num[idx];
	}
	
	public int resourceUnitCnt(){
		int sum = 0;
		for(int n = 0; n < Constant.N; n++){
			sum += num[n] * Constant.UNIT_MUL[n];
		}
		return sum;
	}

	@Override
	public String toString(){
		return String.join(",", Arrays.toString(num));
	}
	
	@Override
	public int compareTo(ContainerList o) {
		return this.resourceUnitCnt() - o.resourceUnitCnt();
	}
}
