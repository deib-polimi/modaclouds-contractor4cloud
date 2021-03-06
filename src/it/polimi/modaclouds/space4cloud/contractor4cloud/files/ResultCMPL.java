package it.polimi.modaclouds.space4cloud.contractor4cloud.files;

import it.polimi.modaclouds.qos_models.schema.ContractType;
import it.polimi.modaclouds.qos_models.schema.CostType;
import it.polimi.modaclouds.qos_models.schema.Costs.Providers;
import it.polimi.modaclouds.qos_models.schema.Costs.Providers.SpotRequests;
import it.polimi.modaclouds.qos_models.schema.Costs.Providers.SpotRequests.HourRequest;
import it.polimi.modaclouds.qos_models.schema.HourPriceType;
import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.ProblemInstance;
import it.polimi.modaclouds.space4cloud.contractor4cloud.solution.SolutionMulti;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class ResultCMPL extends Result {

	public ResultCMPL(Path path, int daysConsidered) {
		super(path, daysConsidered);
	}
	
	@Override
	public void match(String s) {
		StringTokenizer st = new StringTokenizer(s, " ");
		
		if (st.countTokens() != 6)
			return;
		
		String name = st.nextToken();
		String params = name.substring(name.indexOf('[') + 1, name.length() - 1);
		st.nextToken();
		String strValue = st.nextToken();
		int value = 0;
		try {
			value = Integer.parseInt(strValue);
		} catch (Exception e) { }
		
		String[] comps = params.split(",");
		
		int c = -1, t = -1;
		
		for (String el : comps)
			switch (el.charAt(0)) {
			case 'c':
				c = Integer.parseInt(el.substring(1)) - 1;
				break;
			case 't':
				t = Integer.parseInt(el.substring(1)) - 1;
				break;
			}
		
		if (Pattern.matches("D\\[t[0-9]+\\]", name)) {
			Providers p = getActualProvider();
			CostType ctp = p.getCost();
			
			HourPriceType hour = null;
			for (HourPriceType h : ctp.getHourPrice())
				if (h.getHour() == t)
					hour = h;
			
			float cost = pi.getCostOnDemand().floatValue() * value;
			
			hour.setCost(hour.getCost() + cost);

			ctp.setTotalCost(ctp.getTotalCost() + cost);
			
		} else if (Pattern.matches("S\\[t[0-9]+\\]", name)) {
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
		} else if (Pattern.matches("R\\[c[0-9]+,t[0-9]+\\]", name)) {
			Providers p = getActualProvider();
			
			ContractType contract = getContract(c, p);
			HourPriceType hour = null;
			for (HourPriceType h : contract.getHourPrice())
				if (h.getHour() == t)
					hour = h;
			
			float cost = contract.getHourCost() * value;
			
			hour.setCost(hour.getCost() + cost);

			contract.setTotalCost(contract.getTotalCost() + cost);
		} else if (Pattern.matches("X\\[c[0-9]+\\]", name)) {
			Providers p = getActualProvider();
			
			ContractType contract = getContract(c, p);
			contract.setMaxReplicas(value);
		}
	}
	
	public static File parse(SolutionMulti solution, Path path, int daysConsidered) throws Exception {
		List<ProblemInstance> pis = ProblemInstance.getProblemInstances(solution);
		ResultCMPL result = new ResultCMPL(path, daysConsidered);
		for (int i = 0; i < pis.size(); ++i) {
			result.parse(pis.get(i), Paths.get(Configuration.LOCAL_TEMPORARY_FOLDER, Configuration.RUN_RES_CMPL + "-" + (i+1)).toString());
		}
		return result.export();
	}

}
