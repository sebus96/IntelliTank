package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.GasStation;
import model.Price;
import model.Route;
import intellitank.IntelliTank;

public class CSVReader {
	
	public static Map<Integer, GasStation> importGasStations() {
		String filename = "data/Tankstellen.csv";
		List<String> lines = readCSV(filename);
		Map<Integer, GasStation> stations = new HashMap<>();
		for(String line: lines) {
			String[] lineElements = line.split(";");
			for(int i = 0; i < lineElements.length; i++) {
				lineElements[i] = lineElements[i].replaceAll("\"", "").trim();
			}
			if(lineElements.length != 9) {
				System.err.println("Illegal Input row size");
			}
			GasStation station = new GasStation(
					getInteger(lineElements[0]),// id
					lineElements[1],// name
					lineElements[2],// brand
					lineElements[3],// street
					lineElements[4],// houseNumber
					getInteger(lineElements[5]),// postcode
					lineElements[6],// location
					getDouble(lineElements[7]),// longitude
					getDouble(lineElements[8])// latitude
			);
			stations.put(station.getID(), station);
		}
		return stations;
	}
	
	public static Route importRoute(Map<Integer, GasStation> stations) {
		String filename = "data/Fahrzeugrouten/Bertha Benz Memorial Route.csv";
		List<String> lines = readCSV(filename);
		Route route = new Route(getInteger(lines.remove(0)));
		for(String line: lines) {
			String[] lineElements = line.split(";");
			for(int i = 0; i < lineElements.length; i++) {
				lineElements[i] = lineElements[i].replaceAll("\"", "").trim();
			}
			if(lineElements.length != 2) {
				System.err.println("Illegal Input row size");
			}
			Date time = new Date();
			try {
				time = DateFormat.getDateTimeInstance().parse(lineElements[0]);
			} catch (ParseException e) {
				//e.printStackTrace();
			}
			route.addRouteElement(stations.get(getInteger(lineElements[1])), time);
		}
		return route;
	}
	
	public static List<Price> importPrices(List<GasStation> refuelStops) {
		for(int i = 0; i < refuelStops.size(); i++) {
			String filename = "data/Benzinpreise/" + refuelStops.get(i).getID() + ".csv";
			List<String> lines = readCSV(filename);
			for(String line: lines) {
				
			}
		}
		return null;
	}
	
	private static List<String> readCSV(String filename) {
		List<String> lines = new ArrayList<>();
		File f = new File(filename);
		Reader r = null;
		BufferedReader br = null;
		try{
			r = new FileReader(f);
			br = new BufferedReader(r);
			String line;
			while((line = br.readLine()) != null){
				lines.add(line);
			}
			return lines;
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(r != null) r.close();
				if(br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static int getInteger(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	private static double getDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}
