package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.FederalState;
import model.GasStation;
import model.IPredictionStations;
import model.PredictionPoints;
import model.Price;
import model.Route;
import view.PopupBox;

public class CSVManager {

    private static List<Postalcode> post2state;
    private static final String inputPath = "Eingabedaten/";
    private static final String routeInputPath = inputPath + "Fahrzeugrouten/";
    private static final String predictionInputPath = inputPath + "Vorhersagezeitpunkte/";
    private static final String pricePath = inputPath + "Benzinpreise/";
    
    private static final String outputPath = "Ausgabedaten/";
    private static final String routeOutputPath = outputPath + "Tankstrategien/";
    private static final String predictionOutputPath = outputPath + "Vorhersagen/";
    
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");

    public static Map<Integer, GasStation> importGasStations() {
        String filename = inputPath + "Tankstellen.csv";
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
                PopupBox.displayWarning(201);
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
        String filename = inputPath + "postalcode2federalstate.csv";
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
    /*
    public static Route importStandardRoute(Map<Integer, GasStation> stations) {
    	String[] routes = {"Bertha Benz Memorial Route", "Hildesheim Harz", "Oldenburg Hannover", "Hannover Hildesheim", "Kiel Celle"};
        return importRoute(stations, routes[4]);
    }*/

    public static Route importRoute(Map<Integer, GasStation> stations, String routeName) {
    	File routeFile = new File(routeInputPath + routeName + (routeName.endsWith(".csv")? "" : ".csv"));
    	List<String> lines = readCSV(routeFile);
    	System.out.println("import route: " + routeName);
        if (lines == null) {
            System.err.println("Could not import Route \"" + routeFile.getName() + "\"!");
            return null;
        }
        Route route = new Route(routeName, getInteger(lines.remove(0)));
        for (String line : lines) {
            String[] lineElements = prepareRowData(line);
            if (lineElements.length != 2) {
                System.err.println("Import route: Illegal Input row size");
                return null;
            }
            route.addRouteElement(stations.get(getInteger(lineElements[1])), getDate(lineElements[0]));
        }
        System.out.println(route);
        return route;
    }
    
    public static void exportPredictions(IPredictionStations stations) {
    	writeCSV(stations);
    }
    
    public static PredictionPoints importStandardPredictionPoints(Map<Integer, GasStation> stations) {
        return importPredictionPoints(stations, "Meine Tankstellen");
    }

    public static PredictionPoints importPredictionPoints(Map<Integer, GasStation> stations, String predictionName) {
        File predictionFile = new File(predictionInputPath + predictionName + (predictionName.endsWith(".csv")? "" : ".csv"));
    	List<String> lines = readCSV(predictionFile);
    	System.out.println("import predictionpoints: " + predictionName);
        if (lines == null) {
            System.err.println("Could not import Prediction \"" + predictionFile.getName() + "\"!");
            return null;
        }
        PredictionPoints result = new PredictionPoints(predictionName);
        for (String line : lines) {
            String[] lineElements = prepareRowData(line);
            if (lineElements.length != 3) {
                System.err.println("Import prediction: Illegal Input row size");
            }
            result.addPredictionElement(stations.get(getInteger(lineElements[2])), getDate(lineElements[0]), getDate(lineElements[1]));
        }
        return result;
    }

    public static void importPrices(IPredictionStations stations) {
        if (stations == null) return;
        for (int i = 0; i < stations.getLength(); i++) {
            importPrice(stations.get(i).getStation());
        }
    }

    public static void importPrice(GasStation gs) {
//		double start = System.nanoTime();
    	if(gs.hasPriceList()) {
    		System.out.println("Prices for " + gs + " already imported. (size=" + gs.getPriceListSize() + ")");
    		return;
    	}
        String filename = pricePath + gs.getID() + ".csv";
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
            r = new InputStreamReader(new FileInputStream(file),"UTF-8");
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

    private static boolean writeCSV(IPredictionStations stations) {
        String path = "";
        if(stations instanceof Route) path = routeOutputPath;
        else if (stations instanceof PredictionPoints) path = predictionOutputPath;
        new File(path).mkdirs(); // erstellt output ordner
        File file = new File(path + stations.getName() + (stations.getName().endsWith(".csv")? "" : ".csv"));
        
        Writer w = null;
        BufferedWriter bw = null;
        try {
            w = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            bw = new BufferedWriter(w);
            for(int i = 0; i < stations.getLength(); i++) {
            	bw.write(stations.get(i).toCSVString());
            	if(i+1 < stations.getLength()) bw.newLine();
            }
            bw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (w != null) {
                    w.close();
                }
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
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
            return dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] readRouteNames() {
    	return readFilenames(new File(routeInputPath));
    }

    public static String[] readPredictionPointNames() {
    	return readFilenames(new File(predictionInputPath));
    }
    
    private static String[] readFilenames(File folder) {
    	String[] listOfFiles = folder.list(new PredictionFileFilter());
        return listOfFiles;
    }
    
    public static DateFormat getDateFormat() {
    	return dateFormat;
    }

    public static void copyRoute(File selectedFile) throws FileNotFoundException, IOException {
        Path path = Paths.get(routeInputPath + selectedFile.getName());
        copyFile(selectedFile, path);
    }

    public static void copyPredictionPoints(File selectedFile) throws FileNotFoundException, IOException {
        Path path = Paths.get(predictionInputPath + selectedFile.getName());
        copyFile(selectedFile, path);
    }
    
    private static void copyFile(File selectedFile, Path dest) throws FileNotFoundException, IOException {
    	InputStream is = new FileInputStream(selectedFile);
        Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
        is.close();
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

class PredictionFileFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		File f = new File(dir.getPath() + File.separator + name);
		return f.isFile() && name.endsWith(".csv");
	}
}
