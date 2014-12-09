package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.ProblemInstance;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.SolutionMulti;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class Data {
	private ProblemInstance pi;
	private int daysConsidered;
	private double percentageOfS;
	private double m;
	
	public Data(ProblemInstance pi, int daysConsidered, double percentageOfS, double m) {
		this.pi = pi;
		this.daysConsidered = daysConsidered;
		this.percentageOfS = percentageOfS;
		this.m = m;
	}
	
	private static DecimalFormat doubleFormatter() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat myFormatter = new DecimalFormat("0.000", otherSymbols);
		return myFormatter;
	}
	
	private static DecimalFormat intFormatter(int maxValue) {
		String pattern = "0";
	    while (maxValue >= 10) {
		    maxValue = maxValue / 10;
		    pattern += "0";
	    }
		
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		return myFormatter;
	}
	
	public boolean print(String file) {
//		StringWriter sw = new StringWriter();
//		PrintWriter out = new PrintWriter(sw);
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(file));
			
			DecimalFormat doubleFormatter = doubleFormatter();
		
			DecimalFormat contractFormatter = intFormatter(pi.getNumberOfContracts());
			out.print("set CONTRACT :=");
			for (int i = 1; i <= pi.getNumberOfContracts() ; ++i)
				out.printf(" c%s", contractFormatter.format(i));
			out.println(";");
			
			DecimalFormat timeIntFormatter = intFormatter(24);
			out.print("set TIME_INT :=");
			for (int i = 1; i <= 24; ++i)
				out.printf(" t%s", timeIntFormatter.format(i));
			out.println(";");
			
			out.println();
			
			out.printf("param DaysConsidered := %d;\n", daysConsidered);
			out.printf("param M := %s;\n", doubleFormatter.format(m));
			out.printf("param PercentageOfS := %s;\n", doubleFormatter.format(percentageOfS));
			
			out.printf("param CostD := %s;\n", doubleFormatter.format(pi.getCostOnDemand()));
			
			out.print("param CostS default 0 :=");
			for (int i = 1; i <= 24; ++i)
				out.printf("\nt%s %s", timeIntFormatter.format(i), doubleFormatter.format(pi.getCostOnSpot()));
			out.println(";");
			
			out.print("param CostR default 0 :=");
			List<Double> hourlyReserved = pi.getHourlyCostsReserved();
			for (int i = 0; i < hourlyReserved.size(); ++i) {
				out.printf("\nc%s %s", contractFormatter.format(i+1), doubleFormatter.format(hourlyReserved.get(i)));
			}
			out.println(";");
			
			out.print("param InitialCostR default 0 :=");
			List<Double> initialReserved = pi.getInitialCostsReserved(daysConsidered);
			for (int i = 0; i < initialReserved.size(); ++i) {
				out.printf("\nc%s %s", contractFormatter.format(i+1), doubleFormatter.format(initialReserved.get(i)));
			}
			out.println(";");
			
			out.print("param Instances default 0 :=");
			int replicas[] = pi.getReplicas();
			for (int i = 0; i < replicas.length; ++i)
				out.printf("\nt%s %d", timeIntFormatter.format(i+1), replicas[i]);
			out.println(";");
			
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
//		System.out.println(sw.toString());
		
		return true;
	}

	public static int print(SolutionMulti solution, int daysConsidered, double percentageOfS, double m) {
		int i = 0;
		for (ProblemInstance pi : ProblemInstance.getProblemInstances(solution)) {
			Data data = new Data(pi, daysConsidered, percentageOfS, m);
			data.print(Configuration.RUN_DATA + "-" + (++i));
		}
		return i;
	}
}
