package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class BashCMPL extends Bash {

	@Override
	public boolean print(String file, int i) {
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(file + "-" + i));
			
			String baseFile = ""; //new String(Files.readAllBytes(Paths.get(Configuration.DEFAULTS_FOLDER, Configuration.RUN_FILE))); //, Charset.defaultCharset()); // StandardCharsets.UTF_8);
			
//			String baseFile = new String(Files.readAllBytes(Paths.get(this.getClass().getResource(Configuration.RUN_FILE).toURI())));
			
			Scanner sc = new Scanner(this.getClass().getResourceAsStream("/" + Configuration.DEFAULTS_BASH_CMPL));
			
			while (sc.hasNextLine())
				baseFile += sc.nextLine() + "\n";
			
			sc.close();
			
			out.printf(baseFile,
					Configuration.RUN_WORKING_DIRECTORY,
					Configuration.RUN_LOG_CMPL + "-" + i,
					Configuration.RUN_RES_CMPL + "-" + i,
					Configuration.RUN_WORKING_DIRECTORY,
					Configuration.RUN_FILE_CMPL + "-" + i);
			
			out.flush();
			out.close();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	public static void print(int datas) {
		BashCMPL b = new BashCMPL();
		for (int i = 1; i <= datas; ++i)
			b.print(Configuration.DEFAULTS_BASH_CMPL, i);
	}
	
}
