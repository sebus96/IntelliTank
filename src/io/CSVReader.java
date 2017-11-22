package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.GasStation;
import model.Price;
import model.Route;

public class CSVReader {
	
	public static Map<Integer, GasStation> importGasStations() {
		String filename = "data/Tankstellen.csv";
		List<String> lines = readCSV(filename);
		Map<Integer, GasStation> stations = new HashMap<>();
		for(String line: lines) {
			String[] lineElements = prepareRowData(line);
			if(lineElements.length != 9) {
				System.err.println("Input stations: Illegal Input row size");
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
			String[] lineElements = prepareRowData(line);
			if(lineElements.length != 2) {
				System.err.println("Import route: Illegal Input row size");
			}
			route.addRouteElement(stations.get(getInteger(lineElements[1])), getDate(lineElements[0]));
		}
		return route;
	}
	
	public static void importPrices(List<GasStation> gsl) {
		for(GasStation gs: gsl) {
			importPrice(gs);
		}
	}
	
	public static void importPrices(Route route) {
		for(int i = 0; i < route.getLength(); i++) {
			importPrice(route.get(i).getStation());
		}
	}
	
	private static void importPrice(GasStation gs) {
		String filename = "data/Benzinpreise/" + gs.getID() + ".csv";
		List<String> lines = readCSV(filename);
		List<Price> prices = new ArrayList<Price>();
		for(String line: lines) {
			String[] lineElements = prepareRowData(line);
			if(lineElements.length != 2) {
				System.err.println("Import prices: Illegal Input row size");
			}
			prices.add(new Price(getDate(lineElements[0]), getInteger(lineElements[1])));
		}
		gs.setPriceList(prices);
	}
	
	private static String[] prepareRowData(String row) {
		String[] res = row.split(";");
		for(int i = 0; i < res.length; i++) {
			res[i] = res[i].replaceAll("\"", "").trim();
		}
		return res;
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
	
	private static Date getDate(String s) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX").parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
