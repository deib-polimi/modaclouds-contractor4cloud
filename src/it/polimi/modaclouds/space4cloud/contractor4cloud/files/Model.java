package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;

public abstract class Model {
	
	public abstract boolean print(String file, int i);
	
	public static void print(int datas) {
		switch (Configuration.MATH_SOLVER) {
		case AMPL:
			ModelAMPL.print(datas);
			break;
		case CMPL:
			ModelCMPL.print(datas);
			break;
		}
	}
	
}
