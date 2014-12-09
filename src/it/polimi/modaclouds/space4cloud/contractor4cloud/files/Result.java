package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
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
	private SolutionMulti solution;
	private Path path;
	
	public Result(SolutionMulti solution, Path path) {
		this.solution = solution;
		this.path = path;
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
		Costs c = new Costs();
		c.setSolutionID(hashCode() + "");
		
		
		Providers p = new Providers();
		
		Contract contract = new Contract();
		contract.setContractType("");
		contract.setHourCost(0);
		contract.setInstanceType("");
		contract.setReplicas(0);
		
		p.getContract().add(contract);
		
		c.getProviders().add(p);
		
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext
					.newInstance(Costs.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to System.out
//			m.marshal(rme, System.out);

			// Write to File
			m.marshal(c, Paths.get(path.toString(), "costs-" + i + ".xml").toFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<File> parse(SolutionMulti solution, int datas, Path path) {
		List<File> res = new ArrayList<File>();
		for (int i = 1; i <= datas; ++i) {
			Result result = new Result(solution, path);
			result.parse(Configuration.RUN_RES + "-" + i);
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
			
			System.out.printf("R: %d, %d, %d\n", c, t, value);
		}
	}
}
