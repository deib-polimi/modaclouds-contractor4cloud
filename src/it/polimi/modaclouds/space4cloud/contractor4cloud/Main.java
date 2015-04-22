package it.polimi.modaclouds.space4cloud.contractor4cloud;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void doMain(String configuration, String solution) {
		doMain(configuration, solution, 1000.0);
	}
	
	public static void doMain(String configuration, String solution, double m) {
		if (configuration == null || !new File(configuration).exists()) {
			logger.error("The configuration file doesn't exist! Exiting...");
			return;
		}
		
		if (solution == null || !new File(solution).exists()) {
			logger.error("The solution file doesn't exist! Exiting...");
			return;
		}
		
		Contractor.removeTempFiles = false;
		
		try {
			File f = Contractor.perform(configuration, solution, m);
			if (f != null && f.exists())
				logger.debug("Solution: " + f.getAbsolutePath());
			else
				logger.error("No solution!");
		} catch (Exception e) {
			logger.error("Error while getting the solution!", e);
		}
	}
	
	public static void mainConstellation(String[] args) {
		String basePath       = "/Users/ft/Development/workspace-s4c-runtime/Constellation/";
		String configuration  = basePath + "OptimizationMacLocal.properties";
		String solution       = basePath + "ContainerExtensions/Computed/Solution-Conference-Amazon.xml";

		doMain(configuration, solution);
	}
	
	public static void mainUff(String[] args) {
		String basePath       = "/Users/ft/Desktop/tmp/trash/";
		String configuration  = basePath + "s4c.properties";
		String solution       = basePath + "solution.xml";

		doMain(configuration, solution, 1000.0);
	}
	
	public static void main(String[] args) {
//		mainConstellation(args);
		mainUff(args);
	}
}
