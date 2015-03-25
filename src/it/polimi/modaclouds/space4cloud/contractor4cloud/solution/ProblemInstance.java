package it.polimi.modaclouds.space4cloud.contractor4cloud.solution;

import it.polimi.modaclouds.space4cloud.contractor4cloud.db.DataHandler;
import it.polimi.modaclouds.space4cloud.contractor4cloud.db.DataHandlerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProblemInstance {
	private String provider;
	private String serviceName;
	private String resourceName;
	private String region;
	private int[] replicas;
	
	private DataHandler dataHandler;
	
	public ProblemInstance(String provider, String region, String serviceName, String resourceName, int[] replicas) throws Exception {
		this.provider = provider;
		this.region = region;
		this.serviceName = serviceName;
		this.resourceName = resourceName;
		this.replicas = replicas;
		
		this.dataHandler = DataHandlerFactory.getHandler();
	}
	
	public String getProvider() {
		return provider;
	}

	public String getResourceName() {
		return resourceName;
	}

	public String getRegion() {
		return region;
	}

	public int[] getReplicas() {
		return replicas;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	private Double costOnDemand = null;
	private Double costOnSpot = null;
	private List<Double> hourlyCostReserved = null;
	private List<Double> initialCostReserved = null;
	
	public Double getCostOnDemand() {
		if (costOnDemand == null)
			costOnDemand = dataHandler.getCostOnDemand(provider, serviceName, resourceName, region);
		return costOnDemand;
	}
	
	public Double getCostOnSpot() {
		if (costOnSpot == null)
			costOnSpot = dataHandler.getCostOnSpot(provider, serviceName, resourceName, region);
		return costOnSpot;
	}
	
	public List<Double> getHourlyCostsReserved() {
		if (hourlyCostReserved == null)
			hourlyCostReserved = dataHandler.getHourlyCostsReserved(provider, serviceName, resourceName, region);
		return hourlyCostReserved;
	}
	
	public List<Double> getInitialCostsReserved(int daysConsidered) {
		if (initialCostReserved == null)
			initialCostReserved = dataHandler.getInitialCostsReserved(provider, serviceName, resourceName, region, daysConsidered);
		return initialCostReserved;
	}
	
	public int getNumberOfContracts() {
		return dataHandler.getNumberOfContracts();
	}

	public static List<ProblemInstance> getProblemInstances(SolutionMulti solutionMulti) throws Exception {
		Map<String, ProblemInstance> map = new LinkedHashMap<String, ProblemInstance>();
		
		for (Solution s : solutionMulti.getAll()) {
			for (Tier t : s.tiers.values()) {
				String key = s.providerName + "@" + (s.region != null ? s.region : "NoRegion") + "@" + t.resourceName;
				ProblemInstance pi = map.get(key);
				if (pi == null) {
					int[] replicas = new int[24];
					for (int i = 0; i < 24; ++i)
						replicas[i] = t.machines[i].replicas;
					pi = new ProblemInstance(s.providerName, s.region, t.serviceName, t.resourceName, replicas);
				} else {
					int[] replicas = pi.getReplicas();
					for (int i = 0; i < 24; ++i)
						replicas[i] += t.machines[i].replicas;
				}
				map.put(key, pi);
			}
		}
		
		List<ProblemInstance> res = new ArrayList<ProblemInstance>();
		for (ProblemInstance pi : map.values())
			res.add(pi);
		
		return res;
	}
}
