package it.polimi.modaclouds.space4cloud.contractor4cloud;

import java.io.File;
import java.util.List;

public class Main {

	public static void mainConstellation(String[] args) {
		String basePath       = "/Users/ft/Development/workspace-s4c-runtime/Constellation/";
		String configuration  = basePath + "OptimizationMac.properties";
		String solution       = basePath + "ContainerExtensions/Computed/Solution-Conference-Amazon.xml";

		
		Contractor.removeTempFiles = false;
		
		int daysConsidered = 400;
		double percentageOfS = 0.5;
		double m = 1000.0;
		
		List<File> files = Contractor.perform(configuration, solution, daysConsidered, percentageOfS, m);
		boolean done = false;
		for (File f : files) {
			System.out.println("Solution: " + f.getAbsolutePath());
			done = true;
		}
		if (!done)
			System.out.println("No solution!");
	}
	
	public static void main(String[] args) {
		mainConstellation(args);
	}
}
