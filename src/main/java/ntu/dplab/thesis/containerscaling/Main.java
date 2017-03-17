package ntu.dplab.thesis.containerscaling;

import ntu.dplab.thesis.containerscaling.container.ContainerCost;
import ntu.dplab.thesis.containerscaling.container.ContainerList;
import ntu.dplab.thesis.containerscaling.scheduler.DpScheduler;
import ntu.dplab.thesis.containerscaling.scheduler.Scheduler;
import ntu.dplab.thesis.containerscaling.trace.DeployTrace;
import ntu.dplab.thesis.containerscaling.trace.RequestTrace;
import ntu.dplab.thesis.containerscaling.trace.TraceStatistic;

public class Main{
    public static void main(String[] args) {
        //check argument length
    	if (args.length != 2){
        	throw new IllegalArgumentException("Need exactly two arguments: [actual request trace] [predict request trace]");
        }
        RequestTrace req = new RequestTrace(args[0]);
        RequestTrace predict = new RequestTrace(args[1]);
    	
        //check argument size
        if (req.size() != predict.size()){
        	throw new IllegalArgumentException("request trace size (" + req.size() + ") "
        				+ "should be equal to predict trace size (" + predict.size() + ")");
        }
        
        //schedule
        ContainerList zeroList = new ContainerList();
        Scheduler[] schedulers = {
        	//new GreedyScheduler(GreedyScheduler.SCHED_TYPE.MAX),
        	//new GreedyScheduler(GreedyScheduler.SCHED_TYPE.FIT),
        	//new DpScheduler(DpScheduler.SCHED_TYPE.OPT),
        	new DpScheduler(DpScheduler.SCHED_TYPE.PREDICT),
        };
        
        for(Scheduler scheduler : schedulers){        		        
        	//DeployTrace deployTrace = scheduler.schedule(req, zeroList);
        	DeployTrace deployTrace = scheduler.schedule(req, predict, zeroList);
	        assert (deployTrace.size() == req.size()): 
	        	String.format("Trace size (%d) should be equal to req size (%d)", deployTrace.size(), req.size());
	        
	        TraceStatistic stat = ContainerCost.constructTraceStat(deployTrace, req);
	        stat.print();
	        stat.printTrace();
        }
    }
}
