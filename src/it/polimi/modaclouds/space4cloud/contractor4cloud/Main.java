package it.polimi.modaclouds.space4cloud.contractor4cloud;

import java.io.File;

public class Main {

	public static void mainConstellation(String[] args) {
		String basePath       = "/Users/ft/Development/workspace-s4c-runtime/Constellation/";
		String configuration  = basePath + "OptimizationMacLocal.properties";
		String solution       = basePath + "ContainerExtensions/Computed/Solution-Conference-Amazon.xml";

		
		Contractor.removeTempFiles = false;
		
		int daysConsidered = 400;
		double percentageOfS = 0.5;
		double m = 1000.0;
		
		try {
			File f = Contractor.perform(configuration, solution, daysConsidered, percentageOfS, m);
			if (f != null && f.exists())
				System.out.println("Solution: " + f.getAbsolutePath());
			else
				System.out.println("No solution!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		mainConstellation(args);
	}
}
