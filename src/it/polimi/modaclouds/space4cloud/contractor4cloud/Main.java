package it.polimi.modaclouds.space4cloud.contractor4cloud;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static File doMain(String configuration, String solution, String path) {
		return doMain(configuration, solution, path, 1000.0);
	}
	
	public static File doMain(String configuration, String solution, String path, double m) {
		if (configuration == null || !new File(configuration).exists()) {
			logger.error("The configuration file doesn't exist! Exiting...");
			return null;
		}
		
		if (solution == null || !new File(solution).exists()) {
			logger.error("The solution file doesn't exist! Exiting...");
			return null;
		}
		
		Contractor.removeTempFiles = false;
		
		try {
			File f = Contractor.perform(configuration, solution, path, m);
			if (f != null && f.exists())
				logger.debug("Solution: " + f.getAbsolutePath());
			else
				logger.error("No solution!");
			return f;
		} catch (Exception e) {
			logger.error("Error while getting the solution!", e);
		}
		
		return null;
	}
	
	public static void mainConstellation(String[] args) {
		String basePath       = "/Users/ft/Development/workspace-s4c-runtime/Constellation/";
		String configuration  = basePath + "OptimizationMac.properties";
		String solution       = basePath + "ContainerExtensions/Computed/Solution-Conference-Amazon.xml";

		doMain(configuration, solution, null);
	}
	
	public static void mainUff(String[] args) {
		String basePath       = "/Users/ft/Desktop/tmp/trash/";
		String configuration  = basePath + "s4c.properties";
		String solution       = basePath + "solutionMulti.xml";
		
		File f = Paths.get(solution).toFile();

		File res = doMain(configuration, solution, Paths.get(solution).getParent().toString(), 1000.0);
		res.renameTo(Paths.get(f.getParent(), f.getName().replaceAll("solution", "generated-costs")).toFile());
	}
	
	public static void mainUffFolder(String[] args) {
		String basePath       = "/Users/ft/Downloads/ConstellationSaraNew1/";
		String configuration  = "/Users/ft/Desktop/tmp/trash/s4cRemote.properties";
		
		File[] folders = Paths.get(basePath).toFile().listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
						try {
							Integer.parseInt(pathname.getName());
							return true;
						} catch (Exception e) {
							return false;
						}
			}
		});
		
		for (File parent : folders) {
			parent = Paths.get(parent.toString(), "results").toFile();
			
			File[] files = parent.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return
							(pathname.getName().startsWith("solution-"));
				}
			});
			
			for (File f : files) {
				File res = doMain(configuration, f.getAbsolutePath(), f.getParent().toString(), 1000.0);
				res.renameTo(Paths.get(f.getParent(), f.getName().replaceAll("solution", "generated-costs")).toFile());
			}
		}
		
	}
	
	public static void main(String[] args) {
//		mainConstellation(args);
		mainUff(args);
	}
}
