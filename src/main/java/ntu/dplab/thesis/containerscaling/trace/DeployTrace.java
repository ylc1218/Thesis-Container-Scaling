package ntu.dplab.thesis.containerscaling.trace;

import java.util.ArrayList;

import ntu.dplab.thesis.containerscaling.container.ContainerList;

public class DeployTrace {
	private ArrayList<ContainerList> deployment;
	
	public DeployTrace(int T){
		deployment = new ArrayList<ContainerList>(T);
	}
	
	public void add(ContainerList l){
		deployment.add(l);
	}
	
	public ContainerList[] getDeploymentArr(){
		return deployment.toArray(new ContainerList[0]);
	}
	
	public int size(){
		return deployment.size();
	}
	
	
}
