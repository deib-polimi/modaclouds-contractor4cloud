package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.ProblemInstance;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.SolutionMulti;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

public class DataCMPL extends Data {

	public DataCMPL(ProblemInstance pi, int daysConsidered,
			double percentageOfS, double m) {
		super(pi, daysConsidered, percentageOfS, m);
	}
	
	@Override
	public boolean print(String file, int u) {
//		StringWriter sw = new StringWriter();
//		PrintWriter out = new PrintWriter(sw);
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(file + "-" + u));
			
			DecimalFormat doubleFormatter = doubleFormatter();
		
			DecimalFormat contractFormatter = intFormatter(pi.getNumberOfContracts());
			out.print("%CONTRACT set <");
			for (int i = 1; i <= pi.getNumberOfContracts() ; ++i)
				out.printf(" c%s", contractFormatter.format(i));
			out.println(" >");
			
			DecimalFormat timeIntFormatter = intFormatter(24);
			out.print("%TIME_INT set <");
			for (int i = 1; i <= 24; ++i)
				out.printf(" t%s", timeIntFormatter.format(i));
			out.println(">");
			
			out.println();
			
			out.printf("%%DaysConsidered < %d >\n", daysConsidered);
			out.printf("%%M < %s >\n", doubleFormatter.format(m));
			out.printf("%%PercentageOfS < %s >\n", doubleFormatter.format(percentageOfS));
			
			out.printf("%%CostD < %s >\n", doubleFormatter.format(pi.getCostOnDemand()));
			
			out.print("%%CostS[TIME_INT] default 0 <");
			for (int i = 1; i <= 24; ++i)
				out.printf(" %s", doubleFormatter.format(pi.getCostOnSpot()));
			out.println(" >");
			
			out.print("%%CostR[CONTRACT] default 0 <");
			List<Double> hourlyReserved = pi.getHourlyCostsReserved();
			for (int i = 0; i < hourlyReserved.size(); ++i) {
				out.printf(" %s", doubleFormatter.format(hourlyReserved.get(i)));
			}
			out.println(" >");
			
			out.print("%%InitialCostR[CONTRACT] default 0 <");
			List<Double> initialReserved = pi.getInitialCostsReserved(daysConsidered);
			for (int i = 0; i < initialReserved.size(); ++i) {
				out.printf(" %s", doubleFormatter.format(initialReserved.get(i)));
			}
			out.println(" >");
			
			out.print("%%Instances[TIME_INT] default 0 <");
			int replicas[] = pi.getReplicas();
			for (int i = 0; i < replicas.length; ++i)
				out.printf(" %d", replicas[i]);
			out.println(" >");
			
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
			DataCMPL data = new DataCMPL(pi, daysConsidered, percentageOfS, m);
			data.print(Configuration.RUN_DATA_CMPL, ++i);
		}
		return i;
	}

}
