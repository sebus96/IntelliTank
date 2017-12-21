package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.CSVManager;
import model.GasStation;
import model.RefuelStop;
import model.Route;

public class GasStationController {
	private Map<Integer, GasStation> allStations;
	private Route route;
	private List<PredictionUnit> predictions;
	
	public GasStationController() {
		allStations = CSVManager.importGasStations();
		route = CSVManager.importRoute(allStations);
		CSVManager.importPrices(route);
		this.predictions = new ArrayList<>();
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
		System.out.println("Start prediction");
		for(int i = 0; i < route.getLength(); i++) {
			RefuelStop rs = route.get(i);
			GasStation gs = rs.getStation();
			if (gs.getPriceListSize() == 0) {
				System.err.println("Pricelist of " + gs + " does not exist");
				continue;
			}
			PredictionUnit pu = new PredictionUnit(gs, route.get(0).getTime());
			predictions.add(pu);
			boolean trainSuccess = pu.train();
			if(trainSuccess) rs.setPredictedPrices(pu.testAndSetHourSteps());
			System.out.println(((i + 1)*100 / route.getLength()) + " %");
		}
		System.out.println("Prediction finished");
	}
}
