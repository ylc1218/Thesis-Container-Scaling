package ntu.dplab.thesis.containerscaling.trace;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import ntu.dplab.thesis.containerscaling.constant.Constant;

public class RequestTrace {
	private ArrayList<Integer> needArr;
	
	public RequestTrace(String filename) {
		needArr = new ArrayList<Integer>();
		Scanner infile = null;
		try {
			infile = new Scanner(new File(filename));
			infile.useDelimiter("\\s*\\n");
			
			while(infile.hasNextDouble()){
				int unit = (int) Math.ceil(infile.nextDouble() / Constant.UNIT_SERVE);
				needArr.add(unit);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally{
			if (infile != null){
				infile.close();
			}
		}
	}
	
	public RequestTrace() {
		needArr = new ArrayList<Integer>();
	}
	
	public void add(int x){
		needArr.add(x);
	}
	
	public int size() {
		return needArr.size();
	}
	
	public int get(int idx){
		return needArr.get(idx);
	}
	
	public Integer[] getNeedArr() {
		return needArr.toArray(new Integer[0]);
	}
}
