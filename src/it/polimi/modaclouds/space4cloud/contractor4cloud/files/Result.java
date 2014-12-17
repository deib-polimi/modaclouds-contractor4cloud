package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.qos_models.schema.ContractType;
import it.polimi.modaclouds.qos_models.schema.CostType;
import it.polimi.modaclouds.qos_models.schema.Costs;
import it.polimi.modaclouds.qos_models.schema.Costs.Providers;
import it.polimi.modaclouds.qos_models.schema.Costs.Providers.SpotRequests;
import it.polimi.modaclouds.qos_models.schema.Costs.Providers.SpotRequests.HourRequest;
import it.polimi.modaclouds.qos_models.schema.HourPriceType;
import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.db.QueryDictionary;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.ProblemInstance;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.SolutionMulti;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class Result {
	private ProblemInstance pi;
	private Path path;
	private int daysConsidered;
	
	private Costs costs;
	
	public Result(Path path, int daysConsidered) {
		pi = null;
		this.path = path;
		this.daysConsidered = daysConsidered;
		
		costs = new Costs();
		costs.setSolutionID(hashCode() + "");
	}
	
	public void parse(ProblemInstance pi, String file) {
		this.pi = pi;
		
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
	
	public File export() {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext
					.newInstance(Costs.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to System.out
//			m.marshal(rme, System.out);

			// Write to File
			File f = Paths.get(path.toString(), Configuration.GENERATED_COSTS).toFile();
			m.marshal(costs, f);
			
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static File parse(SolutionMulti solution, Path path, int daysConsidered) {
		List<ProblemInstance> pis = ProblemInstance.getProblemInstances(solution);
		Result result = new Result(path, daysConsidered);
		for (int i = 0; i < pis.size(); ++i) {
			result.parse(pis.get(i), Configuration.RUN_RES + "-" + (i+1));
		}
		return result.export();
	}
	
	private Map<String, Providers> providers = new HashMap<String, Providers>();
	private Map<String, SpotRequests> spotRequests = new HashMap<String, SpotRequests>();
	
	private Providers getActualProvider() {
		Providers p = providers.get(pi.getProvider());
		if (p == null) {
			p = new Providers();
			p.setName(pi.getProvider());
			p.setServiceName(pi.getServiceName());
			
			costs.getProviders().add(p);
			providers.put(pi.getProvider(), p);
		}
		
		CostType ctp = p.getCost();
		if (ctp == null) {
			ctp = new CostType();
			ctp.setTotalCost(0.0f);
			p.setCost(ctp);
			
			for (int h = 0; h < 24; ++h) {
				HourPriceType hour = new HourPriceType();
				hour.setHour(h);
				hour.setCost(0.0f);
				ctp.getHourPrice().add(hour);
			}
		}
		
		return p;
	}
	
	public void match(String s) {
		if (Pattern.matches("D\\['t[0-9]+'\\] = [0-9]+", s)) {
			String[] ss = s.split("'");
			
			int t = Integer.parseInt(ss[1].substring(1)) - 1;
			
			int value = Integer.parseInt(s.split("=")[1].trim());
			
			Providers p = getActualProvider();
			CostType ctp = p.getCost();
			
			HourPriceType hour = null;
			for (HourPriceType h : ctp.getHourPrice())
				if (h.getHour() == t)
					hour = h;
			
			float cost = pi.getCostOnDemand().floatValue() * value;
			
			hour.setCost(hour.getCost() + cost);

			ctp.setTotalCost(ctp.getTotalCost() + cost);
			
			System.out.printf("D: %d, %d\n", t, value);
			
		} else if (Pattern.matches("S\\['t[0-9]+'\\] = [0-9]+", s)) {
			String[] ss = s.split("'");
			
			int t = Integer.parseInt(ss[1].substring(1)) - 1;
			
			int value = Integer.parseInt(s.split("=")[1].trim());
			
			Providers p = getActualProvider();
			
			SpotRequests requests = spotRequests.get(pi.getResourceName());
			if (requests == null) {
				requests = new SpotRequests();
				p.getSpotRequests().add(requests);
				spotRequests.put(pi.getResourceName(), requests);
			}
			
			HourRequest request = new HourRequest();
			request.setHour(t);
			request.setInstanceType(pi.getResourceName());
			request.setReplicas(value);
			request.setExpectedHourCost(pi.getCostOnSpot().floatValue() * value);
			
			requests.getHourRequest().add(request);
			
			System.out.printf("S: %d, %d\n", t, value);
		} else if (Pattern.matches("R\\['c[0-9]+','t[0-9]+'\\] = [0-9]+", s)) {
//			String[] ss = s.split("'");
//			
//			int c = Integer.parseInt(ss[1].substring(1)) - 1;
//			int t = Integer.parseInt(ss[3].substring(1)) - 1;
//			
//			int value = Integer.parseInt(s.split("=")[1].trim());
//			
//			System.out.printf("R: %d, %d, %d\n", t, c, value);
		} else if (Pattern.matches("X\\['c[0-9]+'\\] = [0-9]+", s)) {
			String[] ss = s.split("'");
			
			int c = Integer.parseInt(ss[1].substring(1)) - 1;
			
			int value = Integer.parseInt(s.split("=")[1].trim());
			
			Providers p = getActualProvider();
			
			ContractType contract = new ContractType();
			contract.setHourCost(pi.getHourlyCostsReserved().get(c).floatValue());
			contract.setInitialCost(pi.getInitialCostsReserved(daysConsidered).get(c).floatValue());
			contract.setReplicas(value);
			int i = 0;
			for (QueryDictionary.ReservedYears ry : QueryDictionary.ReservedYears.values())
				for (QueryDictionary.ReservedUsage ru : QueryDictionary.ReservedUsage.values()) {
					if (i == c)
						contract.setContractType(ry.getName() + "_" + ru.getName());
					i++;
				}
			contract.setInstanceType(pi.getResourceName());
			
			p.getContract().add(contract);
			
			System.out.printf("X: %d, %d\n", c, value);
		}
	}
}
