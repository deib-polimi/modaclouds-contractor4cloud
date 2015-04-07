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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Contractor {
	
	private static final Logger logger = LoggerFactory.getLogger(Contractor.class);
	
	private SolutionMulti solution;
	private int daysConsidered;
	private double percentageOfS;
	private double m;
	
	public Contractor(String configurationFile, String solutionFile, int daysConsidered, double percentageOfS, double m) throws Contractor4CloudException {
		try {
			Configuration.loadConfiguration(configurationFile);
		} catch (Exception e) {
			throw new Contractor4CloudException("Error while loading the configuration file!", e);
		}
			
		if (SolutionMulti.isEmpty(new File(solutionFile))) {
			throw new Contractor4CloudException("The solution file doesn't exist or is empty!");
		} else {
			solution = new SolutionMulti(new File(solutionFile));
			
			if (solution.size() == 0)
				throw new Contractor4CloudException("Error with the solution!");
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
	
	public File getSolutions(Path path) throws Contractor4CloudException {
		if (path == null)
			path = Paths.get(Configuration.PROJECT_BASE_FOLDER, Configuration.WORKING_DIRECTORY);
		
		if (Configuration.usesPaaS()) {
			logger.error("PaaS not supported at the moment.");
			solutionFile = Result.printEmpty(path);
			return solutionFile;
		}
		
		List<String> errors = Configuration.checkValidity(); 
		if (errors.size() == 1)
			throw new Contractor4CloudException("There is 1 problem with the configuration:\n- " + errors.get(0)); 
		else if (errors.size() > 1) {
			String message = "There are " + errors.size() + " problems with the configuration:";
			for (String s : errors)
				message += "\n- " + s;
			throw new Contractor4CloudException(message);
		}
		
		if (solutionFile != null)
			return solutionFile;
		
		Configuration.RUN_WORKING_DIRECTORY = Configuration.DEFAULTS_WORKING_DIRECTORY + Configuration.DEFAULTS_WORKING_DIRECTORY_SUFFIX + "/" + getDate();
		
		int datas = 0;
		
		try {
			datas = Data.print(solution, daysConsidered, percentageOfS, m);
			Run.print(datas);
			Model.print(datas);
			Bash.print(datas);
		} catch (Exception e) {
			throw new Contractor4CloudException("Error when creating the problem files.", e);
		}
		
		try {
			SshConnector.run(datas);
		} catch (Exception e) {
			throw new Contractor4CloudException("Error when sending or receiving file or when executing the script.", e);
		}
		
		try {
			solutionFile = Result.parse(solution, path, daysConsidered);
		} catch (Exception e) {
			throw new Contractor4CloudException("Error when parsing the solution.", e);
		}
		
		if (removeTempFiles)
			cleanFiles();
		
		return solutionFile;
	}
	
	public File getSolutions() throws Contractor4CloudException {
		return getSolutions(null);
	}
	
	public static boolean removeTempFiles = true;
	
	public void cleanFiles() throws Contractor4CloudException {
		try {
			Files.deleteIfExists(Paths.get(Configuration.RUN_FILE));
			Files.deleteIfExists(Paths.get(Configuration.RUN_DATA));
			Files.deleteIfExists(Paths.get(Configuration.DEFAULTS_BASH));
			Files.deleteIfExists(Paths.get(Configuration.RUN_MODEL));
			Files.deleteIfExists(Paths.get(Configuration.RUN_LOG));
			Files.deleteIfExists(Paths.get(Configuration.RUN_RES));
		} catch (IOException e) {
			throw new Contractor4CloudException("Error while removing the temporary files.", e);
		}
	}

	public static File perform(String configurationFile, String solutionFile, String basePath, int daysConsidered, double percentageOfS, double m) throws Contractor4CloudException {
		Contractor pc = new Contractor(configurationFile, solutionFile, daysConsidered, percentageOfS, m);
		
		Path path = null;
		if (basePath != null && basePath.length() > 0) {
			path = Paths.get(basePath);
			if (!path.toFile().exists())
				path = null;
		}
		
		return pc.getSolutions(path);
		
	}
	
	public static File perform(String configurationFile, String solutionFile, int daysConsidered, double percentageOfS, double m) throws Contractor4CloudException {
		return perform(configurationFile, solutionFile, null, daysConsidered, percentageOfS, m);
	}
}
