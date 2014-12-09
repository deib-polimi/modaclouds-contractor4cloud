package it.polimi.modaclouds.space4cloud.contractor4cloud.db;

import java.util.ArrayList;
import java.util.List;

public final class QueryDictionary {
	// 3 parameters: provider name, service name and resource name
	public static final String CostDemand =
			"SELECT 'On-Demand' as 'contract type', AVG(value) as 'cost', unit, 0 as 'initial cost'\n" +
					"FROM cost WHERE description LIKE 'On-Demand %1$s%%'%2$s;";
	public static final String CostSpot =
			"SELECT 'Spot' as 'contract type', AVG(value) as 'cost', unit, 0 as 'initial cost'\n" +
			"FROM cost WHERE description LIKE 'Spot %s%%'";
	public static final String CostReserved =
			"SELECT 'Reserved' as 'contract type', AVG(value) as 'cost', unit,\n" +
			"(SELECT AVG(value) FROM cost WHERE description LIKE 'Reserved %2$s %3$s %1$s%%'%4$s) as 'initial cost'\n" +
			"FROM cost WHERE description LIKE 'Reserved %3$s %1$s%%'%4$s \n" +
			"AND value IN (SELECT %5$s(value) FROM cost WHERE description LIKE 'Reserved %3$s %1$s%%'%4$s GROUP BY description);";
	
	public static String getQueryCostOnDemand(String cloudResource, String region) {
		return String.format(CostDemand, cloudResource, getActualRegion(region));
	}
	
	public static String getQueryCostOnSpot(String cloudResource) {
		return String.format(CostSpot, getOnSpotResource(cloudResource));
	}
	
	public static String getQueryCostReserved(String cloudResource, ReservedYears ry, ReservedUsage ru, String region) {
		return String.format(CostReserved, cloudResource, ry.getName(), ru.getName(), getActualRegion(region), ry.getMethod());
	}
	
	public static List<String> getAllQueryCostReserved(String cloudResource, String region) {
		List<String> res = new ArrayList<String>();
		for (ReservedYears ry : ReservedYears.values())
			for (ReservedUsage ru : ReservedUsage.values())
				res.add(getQueryCostReserved(cloudResource, ry, ru, region));
		return res;
	}
	
	private static String getActualRegion(String region) {
		String actualRegion = "";
		if (region != null) {
			actualRegion = String.format(" AND cost.region = '%s'", region);
		}
		return actualRegion;
	}
	
	private static String getOnSpotResource(String resource) {
		String resourceBis = "Medium";
		if (resource.indexOf("xlarge") > -1)
			resourceBis = "Extra Large";
		else if (resource.indexOf("large") > -1)
			resourceBis = "Large";
		else if (resource.indexOf("medium") > -1)
			resourceBis = "Medium";
		else if (resource.indexOf("small") > -1)
			resourceBis = "Small";
		else if (resource.indexOf("micro") > -1)
			resourceBis = "Micro";
		
		return resourceBis;
	}
	
	public enum ReservedYears {
		ONEYEAR("1year", "MAX", 365), THREEYEARS("3year", "MIN", 365*3);
		
		private String name;
		private String method;
		private int days;
		
		ReservedYears(String name, String method, int days) {
			this.name = name;
			this.method = method;
			this.days = days;
		}
		
		public String getName() {
			return name;
		}
		
		public String getMethod() {
			return method;
		}
		
		public int getDays() {
			return days;
		}
	}
	
	public enum ReservedUsage {
		LIGHT("light"), MEDIUM("medium"), HEAVY("heavy");
		
		private String name;
		
		ReservedUsage(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
}
