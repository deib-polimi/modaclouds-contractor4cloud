package it.polimi.modaclouds.space4cloud.contractor4cloud.solution;

import java.util.LinkedHashMap;
import java.util.Map;

public class Solution {
	public String providerName;
	public String region;
	public Map<String, Tier> tiers;
	
	public Solution(String providerName, String region) {
		this.providerName = providerName;
		this.region = region;
		tiers = new LinkedHashMap<String, Tier>();
	}
}
