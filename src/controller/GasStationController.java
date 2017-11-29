package controller;

import java.util.Calendar;
import java.util.Date;
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
		prediction = new PredictionUnit(route.get(0).getStation());
//		CSVReader.importPrice(allStations.get(3006));
//		prediction = new PredictionUnit(allStations.get(3006));
		prediction.start();
		prediction.test();
		prediction.checkDate(new Date());
		System.out.println(prediction.checkDayHour(Calendar.MONDAY, 18));
		System.out.println(prediction.checkDayHour(Calendar.TUESDAY, 18));
		System.out.println(prediction.checkDayHour(Calendar.WEDNESDAY, 18));
		System.out.println(prediction.checkDayHour(Calendar.THURSDAY, 18));
		System.out.println(prediction.checkDayHour(Calendar.FRIDAY, 18));
		System.out.println(prediction.checkDayHour(Calendar.SATURDAY, 18));
		System.out.println(prediction.checkDayHour(Calendar.SUNDAY, 18));
	}
}
