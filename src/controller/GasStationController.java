package controller;

import java.util.Map;

import io.CSVReader;
import model.GasStation;
import model.Route;

public class GasStationController {
	private Map<Integer, GasStation> allStations;
	private Route route;
	private PredictionUnit prediction;
	
	public GasStationController() {
		allStations = CSVReader.importGasStations();
		route = CSVReader.importRoute(allStations);
		CSVReader.importPrices(route);
		this.trainPrediction();
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
	
	public void trainPrediction() {
		GasStation gs = route.get(0).getStation();
		prediction = new PredictionUnit(gs);
//		CSVReader.importPrice(allStations.get(3006));
//		prediction = new PredictionUnit(allStations.get(3006));
		prediction.train(gs.getPriceListElement(8000).getTime());
		prediction.testHourSteps(gs.getPriceListElement(8000).getTime());
	}
}
