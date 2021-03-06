package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Scanner;

public class BashAMPL extends Bash {

	@Override
	public boolean print(String file, int i) {
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(Paths.get(Configuration.LOCAL_TEMPORARY_FOLDER, file + "-" + i).toFile()));
			
			String baseFile = ""; //new String(Files.readAllBytes(Paths.get(Configuration.DEFAULTS_FOLDER, Configuration.RUN_FILE))); //, Charset.defaultCharset()); // StandardCharsets.UTF_8);
			
//			String baseFile = new String(Files.readAllBytes(Paths.get(this.getClass().getResource(Configuration.RUN_FILE).toURI())));
			
			Scanner sc = new Scanner(Configuration.getStream(Configuration.DEFAULTS_BASH));
			
			while (sc.hasNextLine())
				baseFile += sc.nextLine() + "\n";
			
			sc.close();
			
			out.printf(baseFile,
					Configuration.RUN_WORKING_DIRECTORY,
					Configuration.RUN_LOG + "-" + i,
					Configuration.RUN_RES + "-" + i,
					Configuration.RUN_AMPL_FOLDER,
					Configuration.RUN_FILE + "-" + i);
			
			out.flush();
			out.close();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	public static void print(int datas) {
		BashAMPL b = new BashAMPL();
		for (int i = 1; i <= datas; ++i)
			b.print(Configuration.DEFAULTS_BASH, i);
	}
	
}
