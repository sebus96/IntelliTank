package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.FederalState;
import model.GasStation;
import model.PredictionPoint;
import model.Price;
import model.Route;

public class CSVManager {

    private static List<Postalcode> post2state;
    public static final String routePath = "own data/routes/";

    public static Map<Integer, GasStation> importGasStations() {
        String filename = "data/Tankstellen.csv";
        importPostalcodes();
        List<String> lines = readCSV(filename);
        if (lines == null) {
            System.err.println("Could not import gasstations!");
            return null;
        }
        Map<Integer, GasStation> stations = new HashMap<>();
        for (String line : lines) {
            String[] lineElements = prepareRowData(line);
            if (lineElements.length != 9) {
                System.err.println("Input stations: Illegal Input row size");
            }
            int postcode = getInteger(lineElements[5]);
            GasStation station = new GasStation(
                    getInteger(lineElements[0]),// id
                    lineElements[1],// name
                    lineElements[2],// brand
                    lineElements[3],// street
                    lineElements[4],// houseNumber
                    postcode,// postcode
                    getState(postcode), // state
                    lineElements[6],// location
                    getDouble(lineElements[7]),// longitude
                    getDouble(lineElements[8])// latitude
            );
            stations.put(station.getID(), station);
        }
        return stations;
    }

    public static FederalState getState(int postalCode) {
        for (Postalcode pc : post2state) {
            if (pc.isInArea(postalCode)) {
                return pc.getState();
            }
        }
        return null;
    }

    private static void importPostalcodes() {
        if (post2state != null) {
            return;
        }
        String filename = "own data/postalcode2federalstate.csv";
        post2state = new ArrayList<>();
        List<String> lines = readCSV(filename);
        if (lines == null) {
            System.err.println("Could not import Postalcodes!");
            return;
        }
        for (String line : lines) {
            String[] lineElements = line.split(";");
            if (lineElements.length != 3) {
                System.out.println(line);
                System.err.println("Input postalcode: Illegal Input row size");
                continue;
            }
            Postalcode code = new Postalcode(
                    getInteger(lineElements[0]),// lower
                    getInteger(lineElements[1]),// upper
                    lineElements[2]// state
            );
            post2state.add(code);
        }
    }

    public static Route importStandardRoute(Map<Integer, GasStation> stations) {
//		File f = new File("data/Fahrzeugrouten/Bertha Benz Memorial Route.csv");
//		File f = new File("own data/routes/Hildesheim Harz.csv");
//		File f = new File("own data/routes/Oldenburg Hannover.csv");
        File f = new File(routePath + "Hannover Hildesheim.csv");

        return importRoute(f, stations);
    }

    public static Route importRoute(File routeFile, Map<Integer, GasStation> stations) {
        List<String> lines = readCSV(routeFile);
        if (lines == null) {
            System.err.println("Could not import Route \"" + routeFile.getName() + "\"!");
            return null;
        }
        Route route = new Route(getInteger(lines.remove(0)));
        for (String line : lines) {
            String[] lineElements = prepareRowData(line);
            if (lineElements.length != 2) {
                System.err.println("Import route: Illegal Input row size");
            }
            route.addRouteElement(stations.get(getInteger(lineElements[1])), getDate(lineElements[0]));
        }
        System.out.println(route);
        return route;
    }

    public static List<PredictionPoint> importPredictionPoint(File predictionFile, Map<Integer, GasStation> stations) {
        String filename = "";
        List<String> lines = readCSV(filename);
        if (lines == null) {
            System.err.println("Could not import Prediction \"" + filename + "\"!");
            return null;
        }
        List<PredictionPoint> result = new ArrayList<>();
        for (String line : lines) {
            String[] lineElements = prepareRowData(line);
            if (lineElements.length != 3) {
                System.err.println("Import prediction: Illegal Input row size");
            }
            result.add(new PredictionPoint(stations.get(getInteger(lineElements[2])), getDate(lineElements[0]), getDate(lineElements[1])));
        }
        return result;
    }

    public static void importPrices(List<GasStation> gsl) {
        for (GasStation gs : gsl) {
            importPrice(gs);
        }
    }

    public static void importPrices(Route route) {
        if (route == null) {
            return;
        }
        for (int i = 0; i < route.getLength(); i++) {
            importPrice(route.get(i).getStation());
        }
    }

    public static void importPrice(GasStation gs) {
//		double start = System.nanoTime();
        String filename = "data/Benzinpreise/" + gs.getID() + ".csv";
        List<String> lines = readCSV(new File(filename));
        if (lines == null) {
            System.err.println("Could not import prices for " + gs);
            return;
        }
        List<Price> prices = new ArrayList<Price>();
        for (String line : lines) {
            String[] lineElements = prepareRowData(line);
            if (lineElements.length != 2) {
                System.err.println("Import prices: Illegal Input row size");
            }
            prices.add(new Price(getDate(lineElements[0]), getInteger(lineElements[1])));
        }
        gs.setPriceList(prices);
//		double time = (System.nanoTime() - start) / 1000 / 1000 / 1000;
//		System.out.println("time: " + time);
    }

    private static String[] prepareRowData(String row) {
        String[] res = row.split(";");
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].replaceAll("\"", "").trim();
        }
        return res;
    }

    private static List<String> readCSV(String filename) {
        return readCSV(new File(filename));
    }

    private static List<String> readCSV(File file) {
        List<String> lines = new ArrayList<>();
        if (file == null || !file.exists()) {
            System.err.println("File \"" + file + "\" does not exist!");
            return null;
        }
        Reader r = null;
        BufferedReader br = null;
        try {
            r = new FileReader(file);
            br = new BufferedReader(r);
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (br != null) {
                    br.close();
                }
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

    public static List<String> readRouteNames() {
        File folder = new File("own data/routes");
        File[] listOfFiles = folder.listFiles();
        List<String> routeNames = new ArrayList<>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".csv")) {
                routeNames.add(listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 5));
            }
        }
        return routeNames;
    }
}

    class Postalcode {

        private int upper, lower;
        private String state;

        public Postalcode(int lower, int upper, String state) {
            this.upper = upper;
            this.lower = lower;
            this.state = state;
        }

        public boolean isInArea(int postcode) {
            return postcode <= upper && postcode >= lower;
        }

        public FederalState getState() {
            switch (this.state) {
                case "Baden-Württemberg":
                    return FederalState.BW;
                case "Bayern":
                    return FederalState.BY;
                case "Berlin":
                    return FederalState.BE;
                case "Brandenburg":
                    return FederalState.BB;
                case "Bremen":
                    return FederalState.HB;
                case "Hamburg":
                    return FederalState.HH;
                case "Hessen":
                    return FederalState.HE;
                case "Mecklenburg-Vorpommern":
                    return FederalState.MV;
                case "Niedersachsen":
                    return FederalState.NI;
                case "Nordrhein-Westfalen":
                    return FederalState.NW;
                case "Rheinland-Pfalz":
                    return FederalState.RP;
                case "Saarland":
                    return FederalState.SL;
                case "Sachsen":
                    return FederalState.SN;
                case "Sachsen-Anhalt":
                    return FederalState.ST;
                case "Schleswig-Holstein":
                    return FederalState.SH;
                case "Thüringen":
                    return FederalState.TH;
                default:
                    return FederalState.DEF;
            }
        }
    }
