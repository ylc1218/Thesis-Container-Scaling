package ntu.dplab.thesis.containerscaling.constant;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;


public class Constant {
	private static final String propFileName = "config/config.properties";
	public static final int N; 			// # container size
	public static final int U; 			// min resource usage
	public static final int UNIT_SERVE;
	public static final Integer[] UNIT_MUL;
	public static final int ALPHA; 		// weight of adjustment cost
	public static final int BETA; 		// weight of insufficiency cost
	
	private static void printConstants(){
		System.out.println("======== Settings ========");
		System.out.println("  UNIT_MUL = " + Arrays.toString(UNIT_MUL));
		System.out.println("  U = " + U);
		System.out.println("  ALPHA = " + ALPHA);
		System.out.println("  BETA = " + BETA);
		System.out.println("  UNIT_SERVE = " + UNIT_SERVE);				
		System.out.println("==========================");
	}
	
	static{
		Properties prop = new Properties();
		InputStream in = Constant.class.getClassLoader().getResourceAsStream(propFileName);
		try{
			if (in != null) {
				prop.load(in);
				in.close();
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
				
		// read properties
		U = Integer.parseInt(prop.getProperty("utilization_constraint"));
		ALPHA = Integer.parseInt(prop.getProperty("alpha"));
		BETA = Integer.parseInt(prop.getProperty("beta"));
		UNIT_SERVE = Integer.parseInt(prop.getProperty("unit_serve"));
		
				
		String str[] = prop.getProperty("container_sizes").split(",");
		UNIT_MUL = new Integer[str.length];
		for(int i = 0; i < str.length; i++){
			UNIT_MUL[i] = Integer.parseInt(str[i]);
		}
		Arrays.sort(UNIT_MUL, Collections.reverseOrder());
		
		N = UNIT_MUL.length;				
		
		printConstants();
	}		
}
