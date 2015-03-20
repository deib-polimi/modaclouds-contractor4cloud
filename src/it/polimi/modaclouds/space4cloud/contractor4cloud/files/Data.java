package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.ProblemInstance;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.SolutionMulti;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public abstract class Data {
	protected ProblemInstance pi;
	protected int daysConsidered;
	protected double percentageOfS;
	protected double m;
	
	public Data(ProblemInstance pi, int daysConsidered, double percentageOfS, double m) {
		this.pi = pi;
		this.daysConsidered = daysConsidered;
		this.percentageOfS = percentageOfS;
		this.m = m;
	}
	
	protected static DecimalFormat doubleFormatter() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat myFormatter = new DecimalFormat("0.000", otherSymbols);
		return myFormatter;
	}
	
	protected static DecimalFormat intFormatter(int maxValue) {
		String pattern = "0";
	    while (maxValue >= 10) {
		    maxValue = maxValue / 10;
		    pattern += "0";
	    }
		
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		return myFormatter;
	}
	
	public abstract boolean print(String file, int i);

	public static int print(SolutionMulti solution, int daysConsidered, double percentageOfS, double m) {
		int i = 0;
		switch (Configuration.MATH_SOLVER) {
		case AMPL:
			i = DataAMPL.print(solution, daysConsidered, percentageOfS, m);
			break;
		case CMPL:
			i = DataCMPL.print(solution, daysConsidered, percentageOfS, m);
			break;
		}
		return i;
	}
}
