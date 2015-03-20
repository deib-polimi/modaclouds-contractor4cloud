package it.polimi.modaclouds.space4cloud.contractor4cloud;

import it.polimi.modaclouds.space4cloud.contractor4cloud.files.Bash;
import it.polimi.modaclouds.space4cloud.contractor4cloud.files.Data;
import it.polimi.modaclouds.space4cloud.contractor4cloud.files.Model;
import it.polimi.modaclouds.space4cloud.contractor4cloud.files.Result;
import it.polimi.modaclouds.space4cloud.contractor4cloud.files.Run;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.SolutionMulti;
import it.polimi.modaclouds.space4cloud.contractor4cloud.ssh.SshConnector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Calendar;

public class Contractor {
	
	private SolutionMulti solution;
	private int daysConsidered;
	private double percentageOfS;
	private double m;
	
	public Contractor(String configurationFile, String solutionFile, int daysConsidered, double percentageOfS, double m) {
		try {
			Configuration.loadConfiguration(configurationFile);
			
			if (SolutionMulti.isEmpty(new File(solutionFile))) {
				throw new Exception("The solution file doesn't exist or is empty!");
			} else {
				solution = new SolutionMulti(new File(solutionFile));
				
				if (solution.size() == 0)
					throw new Exception("Error with the solution!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.daysConsidered = daysConsidered;
		this.percentageOfS = percentageOfS;
		this.m = m;
	}
	
	public static String getDate() {
		Calendar c = Calendar.getInstance();
		
		DecimalFormat f = new DecimalFormat("00");
		
		return String.format("%d%s%s-%s%s%s",
				c.get(Calendar.YEAR),
				f.format(c.get(Calendar.MONTH) + 1),
				f.format(c.get(Calendar.DAY_OF_MONTH)),
				f.format(c.get(Calendar.HOUR_OF_DAY)),
				f.format(c.get(Calendar.MINUTE)),
				f.format(c.get(Calendar.SECOND))
				);
	}
	
	private File solutionFile = null;
	
	public File getSolutions(Path path) {
		if (solutionFile != null)
			return solutionFile;
		
		Configuration.RUN_WORKING_DIRECTORY = Configuration.DEFAULTS_WORKING_DIRECTORY + "/" + getDate();
		
		int datas = Data.print(solution, daysConsidered, percentageOfS, m);
		Run.print(datas);
		Model.print(datas);
		Bash.print(datas);
		
		SshConnector.run(datas);
		
		if (path == null)
			path = Paths.get(Configuration.PROJECT_BASE_FOLDER, Configuration.WORKING_DIRECTORY);
		
		solutionFile = Result.parse(solution, path, daysConsidered);
		
		if (removeTempFiles)
			cleanFiles();
		
		return solutionFile;
	}
	
	public File getSolutions() {
		return getSolutions(null);
	}
	
	public static boolean removeTempFiles = true;
	
	public void cleanFiles() {
		try {
			Files.deleteIfExists(Paths.get(Configuration.RUN_FILE));
			Files.deleteIfExists(Paths.get(Configuration.RUN_DATA));
			Files.deleteIfExists(Paths.get(Configuration.DEFAULTS_BASH));
			Files.deleteIfExists(Paths.get(Configuration.RUN_MODEL));
			Files.deleteIfExists(Paths.get(Configuration.RUN_LOG));
			Files.deleteIfExists(Paths.get(Configuration.RUN_RES));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File perform(String configurationFile, String solutionFile, String basePath, int daysConsidered, double percentageOfS, double m) {
		Contractor pc = new Contractor(configurationFile, solutionFile, daysConsidered, percentageOfS, m);
		
		Path path = null;
		if (basePath != null && basePath.length() > 0) {
			path = Paths.get(basePath);
			if (!path.toFile().exists())
				path = null;
		}
		
		return pc.getSolutions(path);
		
	}
	
	public static File perform(String configurationFile, String solutionFile, int daysConsidered, double percentageOfS, double m) {
		return perform(configurationFile, solutionFile, null, daysConsidered, percentageOfS, m);
	}
}
