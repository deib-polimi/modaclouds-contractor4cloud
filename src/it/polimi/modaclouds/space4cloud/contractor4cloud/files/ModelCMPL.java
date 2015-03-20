package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class ModelCMPL extends Model {

	@Override
	public boolean print(String file, int i) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(file + "-" + i));
			
			String baseFile = "";
			
			Scanner sc = new Scanner(this.getClass().getResourceAsStream("/" + Configuration.RUN_MODEL_CMPL));
			
			while (sc.hasNextLine())
				baseFile += sc.nextLine() + "\n";
			
			sc.close();
			
			int threads = Configuration.CMPL_THREADS;
			if (Configuration.isRunningLocally()) {
				threads = Runtime.getRuntime().availableProcessors() - 1;
				if (threads <= 0)
					threads = 1;
			}
			
			out.print(String.format(baseFile,
					Configuration.RUN_SOLVER_CMPL,
					Configuration.RUN_RES_CMPL + "-" + i,
					Configuration.RUN_DATA_CMPL + "-" + i,
					threads));
			
			out.flush();
			out.close();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void print(int datas) {
		ModelCMPL m = new ModelCMPL();
		for (int i = 1; i <= datas; ++i)
			m.print(Configuration.RUN_MODEL_CMPL, i);
	}
	
}
