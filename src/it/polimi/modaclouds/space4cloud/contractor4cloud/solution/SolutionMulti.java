package it.polimi.modaclouds.space4cloud.contractor4cloud.solution;

import it.polimi.modaclouds.qos_models.schema.CloudService;
import it.polimi.modaclouds.qos_models.schema.Location;
import it.polimi.modaclouds.qos_models.schema.Replica;
import it.polimi.modaclouds.qos_models.schema.ReplicaElement;
import it.polimi.modaclouds.qos_models.schema.ResourceContainer;
import it.polimi.modaclouds.qos_models.schema.ResourceModelExtension;
import it.polimi.modaclouds.qos_models.util.XMLHelper;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class should handle a multi-provider solution, or also, in the
 * particular case, that of a single solution.
 * 
 */
public class SolutionMulti implements Cloneable, Serializable {

	private static final long serialVersionUID = -9050926347950168327L;
	private static final Logger logger = LoggerFactory.getLogger(SolutionMulti.class);
	
	
	// The keys are the providers name, the allocations object are the allocations for each hour.
	private Map<String, Solution> solutions = new LinkedHashMap<String, Solution>();
	
	public int size() {
		return solutions.keySet().size();
	}
	
	public Solution get(String providerName) {
		return solutions.get(providerName);
	}
	
	public Solution add(String providerName, String tierId, 
				String resourceName, String serviceName, String serviceType, String region) {
		Solution sol;
		if (solutions.containsKey(providerName))
			sol = solutions.get(providerName);
		else {
			sol = new Solution(providerName, region);
			solutions.put(providerName, sol);
		}
		
		if (tierId != null && !sol.tiers.containsKey(tierId)) {
			Tier tier = new Tier(providerName, tierId, resourceName, serviceName, serviceType, region);
			sol.tiers.put(tierId, tier);
		}
		
		return sol;
	}

	public static boolean isEmpty(File solution) {
		if (isResourceModelExtension(solution))
			return isEmptyResourceModelExtension(solution);
		else
			return isEmptyFileSolution(solution);
	}
	
	private static boolean isResourceModelExtension(File f) {
		try {
			XMLHelper.deserialize(f.toURI().toURL(), ResourceModelExtension.class);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private static boolean isEmptyResourceModelExtension(File solution) {
		try {
			ResourceModelExtension rme = XMLHelper.deserialize(solution
					.toURI().toURL(), ResourceModelExtension.class);
			
			for (ResourceContainer rc : rme.getResourceContainer()) {
				it.polimi.modaclouds.qos_models.schema.CloudService cs = rc.getCloudElement();
				if (cs != null) {
					Replica r = cs.getReplicas();
					if (r != null) {
						List<ReplicaElement> re = r.getReplicaElement();
						if (re.size() > 0)
							return false;
					}
				}
			}
			
		} catch (MalformedURLException | JAXBException | SAXException e) {
			logger.error("Error in checking if the solution is empty",e);
		}
		return true;
	}

	@Deprecated
	private static boolean isEmptyFileSolution(File solution) {
		if (solution != null && solution.exists())
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(solution);
				doc.getDocumentElement().normalize();

				{
					NodeList nl = doc.getElementsByTagName("HourAllocation");
					return (nl.getLength() == 0);
				}
			} catch (Exception e) {
				logger.error("Error in checking if the solution is empty",e);
			}
		return true;
	}
	
	public SolutionMulti() { }
	
	public SolutionMulti(File solution) {
		setFrom(solution);
	}

	public boolean setFrom(File initialSolution) {
		
		boolean res = false;
		
		try {
			ResourceModelExtension rme = XMLHelper.deserialize(Paths.get(initialSolution.getAbsolutePath()).toUri().toURL(),ResourceModelExtension.class);

			for (ResourceContainer rc : rme.getResourceContainer()) {
				CloudService iaas = rc.getCloudElement();
				
				String provider = rc.getProvider();
				String tierId = rc.getId();
				String resourceName = iaas.getResourceSizeID();
				String serviceName = iaas.getServiceName();
				String serviceType = iaas.getServiceType();
				
				if (!serviceType.equals("Compute"))
					continue;
				
				String region = null;
				
				Location l = iaas.getLocation();
				if (l != null)
					 region = l.getRegion();
				
				Solution sol = add(provider, tierId, resourceName, serviceName, serviceType, region);
				
				Tier t = sol.tiers.get(tierId);

				Solution solution = get(provider);
				if (solution == null)
					continue;

				Replica replicas = iaas.getReplicas();

				for (ReplicaElement re : replicas.getReplicaElement()) {
					int hour = re.getHour();
					int allocation = re.getValue();
					
					t.machines[hour].replicas = allocation;
				}
			}
			
			res = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return res;
	}
	
	public int getTotalMachines() {
		int res = 0;
		for (Solution s : solutions.values()) {
			for (Tier t : s.tiers.values()) {
				res += t.getMaxMachines();
			}
		}
		return res;
	}
	
//	@SuppressWarnings("unused")
	public int getTotalTiers() {
		int res = 0;
		for (Solution s : solutions.values()) {
			res += s.tiers.size();
//			for (Tier t : s.tiers.values()) {
//				res++;
//			}
		}
		return res;
	}
	
	public Collection<Solution> getAll() {
		return solutions.values();
	}

	public Solution get(int i) {
		if (i < 0 || i >= solutions.size())
			return null;
			
		int tmp = 0;
		for (String s : solutions.keySet()) {
			if (tmp == i)
				return solutions.get(s);
			++tmp;
		}
		return null;
	}

}
