package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.qos_models.schema.CostType;
import it.polimi.modaclouds.qos_models.schema.Costs;
import it.polimi.modaclouds.qos_models.schema.Costs.Providers;
import it.polimi.modaclouds.qos_models.schema.Costs.Providers.SpotRequests;
import it.polimi.modaclouds.qos_models.schema.HourPriceType;
import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.ProblemInstance;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.SolutionMulti;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public abstract class Result {
	protected ProblemInstance pi;
	private Path path;
	protected int daysConsidered;
	
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
	
	public static File parse(SolutionMulti solution, Path path, int daysConsidered) throws Exception {
		File f = null;
		switch (Configuration.MATH_SOLVER) {
		case AMPL:
			f = ResultAMPL.parse(solution, path, daysConsidered);
			break;
		case CMPL:
			f = ResultCMPL.parse(solution, path, daysConsidered);
			break;
		}
		return f;
	}
	
	protected Map<String, Providers> providers = new HashMap<String, Providers>();
	protected Map<String, SpotRequests> spotRequests = new HashMap<String, SpotRequests>();
	
	protected Providers getActualProvider() {
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
	
	public abstract void match(String s);
	
	public static File printEmpty(Path p) {
		File f = null;
		switch (Configuration.MATH_SOLVER) {
		case AMPL:
			f = new ResultAMPL(p, 0).export();
			break;
		case CMPL:
			f = new ResultCMPL(p, 0).export();
			break;
		}
		return f;
	}
	
}
