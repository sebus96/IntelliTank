package controller;

import java.util.HashMap;
import java.util.Map;

import io.CSVReader;
import model.GasStation;
import model.Route;

public class GasStationController {
	private Map<Integer, GasStation> allStations;
	private Route route;
	
	public GasStationController() {
		allStations = CSVReader.importGasStations();
		route = CSVReader.importRoute(allStations);
	}
	
	public void addGasStation(GasStation station) {
		if(allStations.containsKey(station.getID())) {
			System.err.println("Warning: Station with same ID will be inserted.");
		}
		allStations.put(station.getID(), station);
	}
	
	public GasStation getStation(int id) {
		return allStations.get(id);
	}
	
	public Route getRoute() {
		return route;
	}
}
