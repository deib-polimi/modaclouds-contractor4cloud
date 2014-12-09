package it.polimi.modaclouds.space4cloud.contractor4cloud.solution;


public class Tier {
	public String id;
	public String providerName;
	public String resourceName;
	public String serviceName;
	public String serviceType;
	public String region;
	
	public Machine[] machines;
	
	public Tier(String providerName, String id, String resourceName, String serviceName, String serviceType, String region) {
		this.id = id;
		this.providerName = providerName;
		this.resourceName = resourceName;
		this.serviceName = serviceName;
		this.serviceType = serviceType;
		this.region = region;
		
		machines = new Machine[24];
		
		for (int h = 0; h < machines.length; ++h)
			machines[h] = new Machine();
	}
	
	public int getMaxMachines() {
		int maxTier = 0;
		for (int i = 0; i < machines.length; ++i) {
			if (machines[i].replicas > maxTier)
				maxTier = machines[i].replicas;
		}
		return maxTier;
	}
	
}
