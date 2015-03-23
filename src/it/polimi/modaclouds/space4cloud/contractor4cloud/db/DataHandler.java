package it.polimi.modaclouds.space4cloud.contractor4cloud.db;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(DataHandler.class);
	
	/**
	 * Instantiates a new data handler. it also charges data from the database
	 * 
	 * @param provider
	 *            the provider
	 * @throws SQLException
	 */
	public DataHandler() throws SQLException {
		try {
			FileInputStream fis = new FileInputStream(Configuration.DB_CONNECTION_FILE);
			DatabaseConnector.initConnection(fis);
		} catch (Exception e) {
			throw new SQLException("Error while initializing the database.", e);
		}
	}
	
	private Map<String, Double> costs = new HashMap<String, Double>();
	
	public Double getCostOnDemand(String provider, String serviceName, String resourceName, String region) {
		String key = provider + "@" + serviceName + "@" + resourceName;
		if (region != null)
			key += "@" + region;
		if (costs.containsKey(key))
			return costs.get(key);
		
		Double cost = Double.MAX_VALUE; // -1.0
		
		String query = QueryDictionary.getQueryCostOnDemand(resourceName, region);
		
		try {
			Connection db = DatabaseConnector.getConnection();
			ResultSet rs = db.createStatement().executeQuery(query);
			
			if (rs.next()) {
				cost = rs.getDouble(2);
				costs.put(key, cost);
			}
		} catch (Exception e) {
			logger.error("Error while getting infos from the database.", e);
		}
		
		return cost;

	}
	
	public Double getCostOnSpot(String provider, String serviceName, String resourceName, String region) {
		String key = provider + "@" + serviceName + "@" + resourceName + "@OnSpot";
		if (region != null)
			key += "@" + region;
		if (costs.containsKey(key))
			return costs.get(key);
		
		Double cost = Double.MAX_VALUE; // -1.0
		
		String query = QueryDictionary.getQueryCostOnSpot(resourceName);
		
		try {
			Connection db = DatabaseConnector.getConnection();
			ResultSet rs = db.createStatement().executeQuery(query);
			
			if (rs.next()) {
				cost = rs.getDouble(2);
				costs.put(key, cost);
			}
		} catch (Exception e) {
			logger.error("Error while getting infos from the database.", e);
		}
		
		return cost;

	}
	
	public List<Double> getHourlyCostsReserved(String provider, String serviceName, String resourceName, String region) {
		String key = provider + "@" + serviceName + "@" + resourceName + "@Reserved";
		List<Double> res = new ArrayList<Double>();
		if (region != null)
			key += "@" + region;
		if (costs.containsKey(key + "@" + QueryDictionary.ReservedYears.values()[0].getName() + QueryDictionary.ReservedUsage.values()[0].getName())) {
			for (QueryDictionary.ReservedYears ry : QueryDictionary.ReservedYears.values())
				for (QueryDictionary.ReservedUsage ru : QueryDictionary.ReservedUsage.values())
					res.add(costs.get(key + "@" + ry.getName() + ru.getName()));
			return res;
		}
		
		Double cost = Double.MAX_VALUE; // -1.0
		Double initialCost = Double.MAX_VALUE;
		
		try {
			Connection db = DatabaseConnector.getConnection();
			
			for (QueryDictionary.ReservedYears ry : QueryDictionary.ReservedYears.values()) {
				for (QueryDictionary.ReservedUsage ru : QueryDictionary.ReservedUsage.values()) {
					ResultSet rs = db.createStatement().executeQuery(QueryDictionary.getQueryCostReserved(resourceName, ry, ru, region));
					if (rs.next()) {
						String keyTmp = key + "@" + ry.getName() + ru.getName();
						cost = rs.getDouble(2);
						initialCost = rs.getDouble(4);
						costs.put(keyTmp, cost);
						costs.put(keyTmp + "@Initial", initialCost);
						res.add(cost);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting infos from the database.", e);
		}
		
		return res;

	}
	
	public List<Double> getInitialCostsReserved(String provider, String serviceName, String resourceName, String region, int daysConsidered) {
		String key = provider + "@" + serviceName + "@" + resourceName + "@Reserved";
		List<Double> res = new ArrayList<Double>();
		if (region != null)
			key += "@" + region;
		if (costs.containsKey(key + "@" + QueryDictionary.ReservedYears.values()[0].getName() + QueryDictionary.ReservedUsage.values()[0].getName())) {
			for (QueryDictionary.ReservedYears ry : QueryDictionary.ReservedYears.values())
				for (QueryDictionary.ReservedUsage ru : QueryDictionary.ReservedUsage.values()) {
					int mul = (int)Math.ceil((double)daysConsidered / ry.getDays());
					res.add(costs.get(key + "@" + ry.getName() + ru.getName() + "@Initial") * mul);
				}
			return res;
		}
		
		Double cost = Double.MAX_VALUE; // -1.0
		Double initialCost = Double.MAX_VALUE;
		
		try {
			Connection db = DatabaseConnector.getConnection();
			
			for (QueryDictionary.ReservedYears ry : QueryDictionary.ReservedYears.values()) {
				for (QueryDictionary.ReservedUsage ru : QueryDictionary.ReservedUsage.values()) {
					ResultSet rs = db.createStatement().executeQuery(QueryDictionary.getQueryCostReserved(resourceName, ry, ru, region));
					if (rs.next()) {
						String keyTmp = key + "@" + ry.getName() + ru.getName();
						cost = rs.getDouble(2);
						initialCost = rs.getDouble(4);
						costs.put(keyTmp, cost);
						costs.put(keyTmp + "@Initial", initialCost);
						int mul = (int)Math.ceil((double)daysConsidered / ry.getDays());
						res.add(initialCost * mul);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting infos from the database.", e);
		}
		
		return res;

	}
	
	public int getNumberOfContracts() {
		return QueryDictionary.ReservedUsage.values().length * QueryDictionary.ReservedYears.values().length;
	}
	
}
