package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ModelAMPL extends Model {

	@Override
	public boolean print(String file, int i) {
		
		try {
			Files.copy(Configuration.getStream(Configuration.RUN_MODEL), Paths.get(file), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	public static void print(int datas) {
		ModelAMPL m = new ModelAMPL();
		m.print(Configuration.RUN_MODEL, 0);
	}
	
}
