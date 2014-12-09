package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.db.QueryDictionary;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.ProblemInstance;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.SolutionMulti;
import it.polimi.modaclouds.space4cloud.generated.costs.Costs;
import it.polimi.modaclouds.space4cloud.generated.costs.Costs.Providers;
import it.polimi.modaclouds.space4cloud.generated.costs.Costs.Providers.Contract;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class Result {
	private ProblemInstance pi;
	private Path path;
	private int daysConsidered;
	
	private Costs costs;
	
	public Result(ProblemInstance pi, Path path, int daysConsidered) {
		this.pi = pi;
		this.path = path;
		this.daysConsidered = daysConsidered;
		
		costs = new Costs();
		costs.setSolutionID(hashCode() + "");
	}
	
	public void parse(String file) {
		try {
			Scanner in = new Scanner(new FileReader(file));
			
			while (in.hasNextLine()) {
				String s = in.nextLine();
				match(s);
			}
			
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File export(int i) {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext
					.newInstance(Costs.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to System.out
//			m.marshal(rme, System.out);

			// Write to File
			File f = Paths.get(path.toString(), "costs-" + pi.getResourceName() + ".xml").toFile();
			m.marshal(costs, f);
			
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<File> parse(SolutionMulti solution, Path path, int daysConsidered) {
		List<File> res = new ArrayList<File>();
		List<ProblemInstance> pis = ProblemInstance.getProblemInstances(solution);
		for (int i = 0; i < pis.size(); ++i) {
			Result result = new Result(pis.get(i), path, daysConsidered);
			result.parse(Configuration.RUN_RES + "-" + (i+1));
			res.add(result.export(i));
		}
		return res;
	}
	
	public void match(String s) {
		if (Pattern.matches("D\\['t[0-9]+'\\] = [0-9]+", s)) {
			String[] ss = s.split("'");
			
			int t = Integer.parseInt(ss[1].substring(1)) - 1;
			
			int value = Integer.parseInt(s.split("=")[1].trim());
			
			System.out.printf("D: %d, %d\n", t, value);
			
		} else if (Pattern.matches("S\\['t[0-9]+'\\] = [0-9]+", s)) {
			String[] ss = s.split("'");
			
			int t = Integer.parseInt(ss[1].substring(1)) - 1;
			
			int value = Integer.parseInt(s.split("=")[1].trim());
			
			System.out.printf("S: %d, %d\n", t, value);
		} else if (Pattern.matches("R\\['c[0-9]+','t[0-9]+'\\] = [0-9]+", s)) {
			String[] ss = s.split("'");
			
			int c = Integer.parseInt(ss[1].substring(1)) - 1;
			int t = Integer.parseInt(ss[3].substring(1)) - 1;
			
			int value = Integer.parseInt(s.split("=")[1].trim());
			
			Contract contract = new Contract();
			contract.setHourCost((float)pi.getHourlyCostsReserved().get(c).doubleValue());
			contract.setInitialCost((float)pi.getInitialCostsReserved(daysConsidered).get(c).doubleValue());
			contract.setReplicas(pi.getReplicas()[t]);
			int i = 0;
			for (QueryDictionary.ReservedYears ry : QueryDictionary.ReservedYears.values())
				for (QueryDictionary.ReservedUsage ru : QueryDictionary.ReservedUsage.values()) {
					if (i == c)
						contract.setContractType(ry.getName() + " " + ru.getName());
					i++;
				}
			contract.setInstanceType(pi.getResourceName());
			
			Providers p = new Providers();
			p.setCost(1.0f);
			p.getContract().add(contract);
			
			costs.getProviders().add(p);
			
			System.out.printf("R: %d, %d, %d\n", c, t, value);
		}
	}
}
