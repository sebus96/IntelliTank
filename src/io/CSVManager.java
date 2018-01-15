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
import model.Holidays;
import model.IPredictionStations;
import model.Postalcodes;
import model.PredictionPoints;
import model.Price;
import model.Route;
import view.PopupBox;

public class CSVManager {
	
	private static boolean printMessages = true;
	
    private static final String inputPath = "Eingabedaten" + File.separator;
    private static final String routeInputPath = inputPath + "Fahrzeugrouten" + File.separator;
    private static final String predictionInputPath = inputPath + "Vorhersagezeitpunkte" + File.separator;
    private static final String pricePath = inputPath + "Benzinpreise" + File.separator;
    private static final String holidayPath = inputPath + "Ferien" + File.separator;
    
    private static final String outputPath = "Ausgabedaten" + File.separator;
    private static final String routeOutputPath = outputPath + "Tankstrategien" + File.separator;
    private static final String predictionOutputPath = outputPath + "Vorhersagen" + File.separator;
    
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");

    public static Map<Integer, GasStation> importGasStations() {
        String filename = inputPath + "Tankstellen.csv";
        importPostalcodes();
        List<String> lines = readFile(filename);
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
                    Postalcodes.getState(postcode), // state
                    lineElements[6],// location
                    getDouble(lineElements[7]),// longitude
                    getDouble(lineElements[8])// latitude
            );
            stations.put(station.getID(), station);
        }
        return stations;
    }

    private static void importPostalcodes() {
        if (Postalcodes.isImported()) {
            return;
        }
        String filename = inputPath + "postalcode2federalstate.csv";
        List<String> lines = readFile(filename);
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
            Postalcodes.addPostcodeRange(
            		getInteger(lineElements[0]),// lower
                    getInteger(lineElements[1]),// upper
                    lineElements[2]// state
            		);
        }
    }
    /*
    public static Route importStandardRoute(Map<Integer, GasStation> stations) {
    	String[] routes = {"Bertha Benz Memorial Route", "Hildesheim Harz", "Oldenburg Hannover", "Hannover Hildesheim", "Kiel Celle"};
        return importRoute(stations, routes[4]);
    }*/

    public static Route importRoute(Map<Integer, GasStation> stations, String routeName) {
    	File routeFile = new File(routeInputPath + routeName + (routeName.endsWith(".csv")? "" : ".csv"));
    	List<String> lines = readFile(routeFile);
    	if(printMessages) System.out.println("import route: " + routeName);
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
        if(printMessages) System.out.println(route);
        return route;
    }
    
    public static List<String> checkRoutes(Map<Integer, GasStation> stations) {
    	List<String> routeWarnings = new ArrayList<>();
    	boolean backup = printMessages;
    	printMessages = false;
    	for(String filename: readRouteNames()) {
    		Route cur = importRoute(stations,filename);
    		if(cur.getTankCapacity() <= 0) {
    			routeWarnings.add(cur.getName() + ": Tankkapazität zu gering.");
    		}
    		if(cur.getLength() == 0) {
    			routeWarnings.add(cur.getName() + ": Keine Elemente enthalten.");
    			continue;
    		}
    		Date lastDate = new Date(0);
    		for(int i = 0; i < cur.getLength(); i++) {
    			if(cur.get(i).getStation() == null) {
    				routeWarnings.add(cur.getName() + ": Tankstelle in Routenelement " + (i+1) + " konnte nicht gefunden werden.");
    			}
    			if(!cur.get(i).getTime().after(lastDate)) {
    				routeWarnings.add(cur.getName() + ": Tankstop mit ID " + cur.get(i).getStation().getID() + " liegt zeitlich vor dem vorherigen.");
    			}
    			lastDate = cur.get(i).getTime();
    		}
    	}
    	printMessages = backup;
    	return routeWarnings;
    }
    
    public static void exportPredictions(IPredictionStations stations) {
    	writeCSV(stations);
    }
    
    public static PredictionPoints importStandardPredictionPoints(Map<Integer, GasStation> stations) {
        return importPredictionPoints(stations, "Meine Tankstellen");
    }

    public static PredictionPoints importPredictionPoints(Map<Integer, GasStation> stations, String predictionName) {
        File predictionFile = new File(predictionInputPath + predictionName + (predictionName.endsWith(".csv")? "" : ".csv"));
    	List<String> lines = readFile(predictionFile);
    	if(printMessages) System.out.println("import predictionpoints: " + predictionName);
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
        List<String> lines = readFile(new File(filename));
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
    
    public static void importHolidays() {
    	String[] holidayNames = {"Winter","Ostern","Pfingsten","Sommer","Herbst","Weihnachten"};
    	for(String filename: readFilenames(new File(holidayPath), ".txt")) {
    		int year = getInteger(filename.substring(0, 4));
    		if(year < 0) System.err.println("Unecpected holiday filename: " + filename);
    		List<String> lines = readFile(holidayPath + filename);
    		int ctr = 0;
    		FederalState curState = null;
    		for(String line: lines) {
    			if(ctr == 0) {
    				curState = FederalState.getFederalState(line);
    				if(curState == FederalState.DEF) {
    					System.err.println("Illegal federal state: " + line);
    				}
    			} else if (ctr > 0 && ctr <= holidayNames.length) {
    				if(curState == null) {
    					System.err.println("Current State should not be null: " + line);
    				}
    				Holidays.addHoliday(year, curState, holidayNames[ctr-1], line);
    			} else {
    				System.err.println("Count to high (" + ctr + ")");
    			}
    			ctr++;
    			ctr %= holidayNames.length + 1;
    		}
    	}
    	// überprüfe importierte Daten
    	/*System.out.println("Holiday check: " + Holidays.checkIntegrity());
    	DateFormat test = new SimpleDateFormat("dd.MM.yyyy/HH:mm");
    	try {
			System.out.println("Holiday ja " + Holidays.isHoliday(test.parse("10.05.2013" + "/12:00"), FederalState.NI));
			System.out.println("Holiday ja " + Holidays.isHoliday(test.parse("20.03.2013" + "/12:00"), FederalState.NI));
			System.out.println("Holiday ja " + Holidays.isHoliday(test.parse("30.12.2015" + "/12:00"), FederalState.BB));
			System.out.println("Holiday ja " + Holidays.isHoliday(test.parse("01.01.2016" + "/12:00"), FederalState.BB));
			System.out.println("Holiday ja " + Holidays.isHoliday(test.parse("02.01.2016" + "/12:00"), FederalState.BB));
			System.out.println("Holiday nein " + Holidays.isHoliday(test.parse("10.02.2018" + "/12:00"), FederalState.BY));
			System.out.println("Holiday ja " + Holidays.isHoliday(test.parse("18.11.2015" + "/12:00"), FederalState.BY));
			System.out.println("Holiday nein " + Holidays.isHoliday(test.parse("19.11.2015" + "/12:00"), FederalState.BY));
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
    }

    private static String[] prepareRowData(String row) {
        String[] res = row.split(";");
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].replaceAll("\"", "").trim();
        }
        return res;
    }
    
    private static List<String> readFile(String filename) {
        return readFile(new File(filename));
    }

    private static List<String> readFile(File file) {
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
    	return readFilenames(new File(routeInputPath), ".csv");
    }

    public static String[] readPredictionPointNames() {
    	return readFilenames(new File(predictionInputPath), ".csv");
    }
    
    private static String[] readFilenames(File folder, String ending) {
    	String[] listOfFiles = folder.list(new InputFileFilter(ending));
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

class InputFileFilter implements FilenameFilter {
	private String ending;
	
	public InputFileFilter(String ending) {
		this.ending = ending;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		File f = new File(dir.getPath() + File.separator + name);
		return f.isFile() && name.endsWith(this.ending);
	}
}
