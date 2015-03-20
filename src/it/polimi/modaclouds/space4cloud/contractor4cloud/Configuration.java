package it.polimi.modaclouds.space4cloud.contractor4cloud;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	
	// Information about the application
	public static String WORKING_DIRECTORY = "space4cloud";
	public static String PROJECT_BASE_FOLDER;
	public static String PALLADIO_RESOURCE_MODEL;
	public static String PALLADIO_USAGE_MODEL;
	public static String PALLADIO_ALLOCATION_MODEL;
	public static String PALLADIO_REPOSITORY_MODEL;
	public static String PALLADIO_SYSTEM_MODEL;
	
	public static String CONSTRAINTS;
	public static String USAGE_MODEL_EXTENSION;
	public static String RESOURCE_ENVIRONMENT_EXTENSION;

	// Information about the DB
	public static String DB_CONNECTION_FILE;

	// Information about the machine with AMPL
	public static String SSH_HOST = "specclient1.dei.polimi.it";
	public static String SSH_USER_NAME;
	public static String SSH_PASSWORD;
	
	// Information about the private cloud
	public static boolean USE_PRIVATE_CLOUD = true;
	public static String PRIVATE_CLOUD_HOSTS;
	
	// Information used in the AMPL.run file
	public static String DEFAULTS_WORKING_DIRECTORY = "/tmp/s4c/contractor4cloud"; //upload directory on AMPL server
	public static String RUN_WORKING_DIRECTORY = DEFAULTS_WORKING_DIRECTORY;
	public static String RUN_FILE = "AMPL.run";
	public static String RUN_MODEL = "modelcontracts.mod";
	public static String RUN_DATA = "data.dat";
	public static String RUN_SOLVER = "/usr/optimization/cplex-studio/cplex/bin/x86-64_linux/cplexamp";
	public static String RUN_AMPL_FOLDER = "/usr/optimization/ampl";
	public static String RUN_LOG = "solution.log"; // TODO: usarlo!
	public static String RUN_RES = "solution.sol"; // TODO: usarlo!
	public static String DEFAULTS_BASH = "bashAMPL.run";
	
	public static String RUN_FILE_CMPL = "CMPL.run";
	public static String RUN_MODEL_CMPL = "modelcontracts.cmpl";
	public static String RUN_DATA_CMPL = "data.cdat";
	public static String RUN_SOLVER_CMPL = "cbc"; // glpk, cbc, scip, gurobi, cplex
	public static String RUN_CMPL_FOLDER = "/usr/share/Cmpl";
	public static String RUN_LOG_CMPL = "solution.log"; 
	public static String RUN_RES_CMPL = "solution.sol";
	public static String DEFAULTS_BASH_CMPL = "bashCMPL.run";
	public static int CMPL_THREADS = 4;
	
	public static Solver MATH_SOLVER = Solver.AMPL;
	
	public static String GENERATED_COSTS = "generated-costs.xml";
	
	public static enum Solver {
		AMPL("AMPL"), CMPL("CMPL");
		
		private String name;
		
		private Solver(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public static Solver getById(int id) {
			Solver[] values = Solver.values();
			if (id < 0)
				id = 0;
			else if (id >= values.length)
				id = values.length - 1;
			return values[id];
		}

		public static int size() {
			return Solver.values().length;			
		}
		
		public static Solver getByName(String name) {
			Solver[] values = Solver.values();
			for (Solver s : values)
				if (s.name.equals(name))
					return s;
			return values[0];
		}

	}
	
	public static void saveConfiguration(String filePath) throws IOException{
		FileOutputStream fos = new FileOutputStream(filePath);
		Properties prop = new Properties();
		prop.put("PALLADIO_REPOSITORY_MODEL", PALLADIO_REPOSITORY_MODEL);
		prop.put("PALLADIO_SYSTEM_MODEL", PALLADIO_SYSTEM_MODEL);
		prop.put("PALLADIO_ALLOCATION_MODEL", PALLADIO_ALLOCATION_MODEL);
		prop.put("PALLADIO_USAGE_MODEL", PALLADIO_USAGE_MODEL);
		prop.put("PALLADIO_RESOURCE_MODEL", PALLADIO_RESOURCE_MODEL);
		prop.put("USAGE_MODEL_EXTENSION", USAGE_MODEL_EXTENSION);
		prop.put("RESOURCE_ENVIRONMENT_EXTENSION", RESOURCE_ENVIRONMENT_EXTENSION);
		prop.put("CONSTRAINTS", CONSTRAINTS);
		prop.put("PROJECT_BASE_FOLDER", PROJECT_BASE_FOLDER);
		prop.put("WORKING_DIRECTORY", WORKING_DIRECTORY);
		prop.put("DB_CONNECTION_FILE", DB_CONNECTION_FILE);
		
		prop.put("SSH_HOST", SSH_HOST);
		prop.put("SSH_USER_NAME", SSH_USER_NAME);
		prop.put("SSH_PASSWORD", SSH_PASSWORD);
		
		prop.put("USE_PRIVATE_CLOUD", Boolean.toString(USE_PRIVATE_CLOUD));
		prop.put("PRIVATE_CLOUD_HOSTS", PRIVATE_CLOUD_HOSTS);
		
		prop.put("RUN_WORKING_DIRECTORY", RUN_WORKING_DIRECTORY);
		prop.put("RUN_MODEL", RUN_MODEL);
		prop.put("RUN_DATA", RUN_DATA);
		prop.put("RUN_SOLVER", RUN_SOLVER);
		prop.put("RUN_AMPL_FOLDER", RUN_AMPL_FOLDER);
		prop.put("RUN_FILE", RUN_FILE);
		prop.put("RUN_LOG", RUN_LOG);
		prop.put("RUN_RES", RUN_RES);
		
		prop.put("DEFAULTS_BASH", DEFAULTS_BASH);
		
		prop.put("RUN_MODEL_CMPL", RUN_MODEL_CMPL);
		prop.put("RUN_DATA_CMPL", RUN_DATA_CMPL);
		prop.put("RUN_SOLVER_CMPL", RUN_SOLVER);
		prop.put("RUN_CMPL_FOLDER", RUN_CMPL_FOLDER);
		prop.put("RUN_FILE_CMPL", RUN_FILE_CMPL);
		prop.put("RUN_LOG_CMPL", RUN_LOG_CMPL);
		prop.put("RUN_RES_CMPL", RUN_RES_CMPL);
		prop.put("CMPL_THREADS", CMPL_THREADS);
		
		prop.put("DEFAULTS_BASH_CMPL", DEFAULTS_BASH_CMPL);
		
		prop.put("MATH_SOLVER", MATH_SOLVER.getName());
		
		prop.store(fos, "S4C-Contractor configuration properties");
		fos.flush();
	}
	
	public static void loadConfiguration(String filePath) throws IOException {
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream(filePath);
		prop.load(fis);
		PALLADIO_REPOSITORY_MODEL = prop.getProperty("PALLADIO_REPOSITORY_MODEL", PALLADIO_REPOSITORY_MODEL);
		PALLADIO_SYSTEM_MODEL = prop.getProperty("PALLADIO_SYSTEM_MODEL", PALLADIO_SYSTEM_MODEL);
		PALLADIO_ALLOCATION_MODEL = prop.getProperty("PALLADIO_ALLOCATION_MODEL", PALLADIO_ALLOCATION_MODEL);
		PALLADIO_USAGE_MODEL = prop.getProperty("PALLADIO_USAGE_MODEL", PALLADIO_USAGE_MODEL);
		PALLADIO_RESOURCE_MODEL = prop.getProperty("PALLADIO_RESOURCE_MODEL", PALLADIO_RESOURCE_MODEL);
		USAGE_MODEL_EXTENSION = prop.getProperty("USAGE_MODEL_EXTENSION", USAGE_MODEL_EXTENSION);
		RESOURCE_ENVIRONMENT_EXTENSION = prop.getProperty("RESOURCE_ENVIRONMENT_EXTENSION", RESOURCE_ENVIRONMENT_EXTENSION);
		CONSTRAINTS = prop.getProperty("CONSTRAINTS", CONSTRAINTS);
		PROJECT_BASE_FOLDER = prop.getProperty("PROJECT_BASE_FOLDER", PROJECT_BASE_FOLDER);
		WORKING_DIRECTORY = prop.getProperty("WORKING_DIRECTORY", WORKING_DIRECTORY);
		DB_CONNECTION_FILE= prop.getProperty("DB_CONNECTION_FILE", DB_CONNECTION_FILE);
		SSH_PASSWORD = prop.getProperty("SSH_PASSWORD", SSH_PASSWORD);
		SSH_USER_NAME = prop.getProperty("SSH_USER_NAME", SSH_USER_NAME);
		SSH_HOST = prop.getProperty("SSH_HOST", SSH_HOST);
		
		USE_PRIVATE_CLOUD = Boolean.parseBoolean(prop.getProperty("USE_PRIVATE_CLOUD", String.valueOf(USE_PRIVATE_CLOUD)));
		PRIVATE_CLOUD_HOSTS = prop.getProperty("PRIVATE_CLOUD_HOSTS", PRIVATE_CLOUD_HOSTS);
		
		RUN_WORKING_DIRECTORY = prop.getProperty("RUN_WORKING_DIRECTORY", RUN_WORKING_DIRECTORY);
		RUN_MODEL = prop.getProperty("RUN_MODEL", RUN_MODEL);
		RUN_DATA = prop.getProperty("RUN_DATA", RUN_DATA);
		RUN_SOLVER = prop.getProperty("RUN_SOLVER", RUN_SOLVER);
		RUN_AMPL_FOLDER = prop.getProperty("RUN_AMPL_FOLDER", RUN_AMPL_FOLDER);
		RUN_FILE = prop.getProperty("RUN_FILE", RUN_FILE);
		RUN_LOG = prop.getProperty("RUN_LOG", RUN_LOG);
		RUN_RES = prop.getProperty("RUN_RES", RUN_RES);
		
		RUN_MODEL_CMPL = prop.getProperty("RUN_MODEL_STANDARD_CMPL", RUN_MODEL_CMPL);
		RUN_DATA_CMPL = prop.getProperty("RUN_DATA_CMPL", RUN_DATA_CMPL);
		RUN_SOLVER_CMPL = prop.getProperty("RUN_SOLVER_CMPL", RUN_SOLVER_CMPL);
		RUN_CMPL_FOLDER = prop.getProperty("RUN_CMPL_FOLDER", RUN_CMPL_FOLDER);
		RUN_FILE_CMPL = prop.getProperty("RUN_FILE_CMPL", RUN_FILE_CMPL);
		RUN_LOG_CMPL = prop.getProperty("RUN_LOG_CMPL", RUN_LOG_CMPL);
		RUN_RES_CMPL = prop.getProperty("RUN_RES_CMPL", RUN_RES_CMPL);
		try {
			CMPL_THREADS = Integer.parseInt(prop.getProperty("CMPL_THREADS", String.valueOf(CMPL_THREADS)));
		} catch (Exception e) { }
		
		DEFAULTS_BASH = prop.getProperty("DEFAULTS_BASH", DEFAULTS_BASH);
		
		DEFAULTS_BASH_CMPL = prop.getProperty("DEFAULTS_BASH_CMPL", DEFAULTS_BASH_CMPL);
		
		MATH_SOLVER = Solver.getByName(prop.getProperty("MATH_SOLVER", MATH_SOLVER.getName()));
		
	}
	
	public static boolean isRunningLocally() {
		return (SSH_HOST.equals("localhost") || SSH_HOST.equals("127.0.0.1"));
	}
}
