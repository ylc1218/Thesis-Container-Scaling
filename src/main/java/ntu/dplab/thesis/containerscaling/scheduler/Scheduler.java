package ntu.dplab.thesis.containerscaling.scheduler;

import ntu.dplab.thesis.containerscaling.container.ContainerList;
import ntu.dplab.thesis.containerscaling.trace.DeployTrace;
import ntu.dplab.thesis.containerscaling.trace.RequestTrace;

public interface Scheduler {
	DeployTrace schedule(RequestTrace req, ContainerList start);
	DeployTrace schedule(RequestTrace req, RequestTrace predict, ContainerList start);
}
