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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controller.RefillStrategies;
import model.FederalState;
import model.GasStation;
import model.Holidays;
import model.IPredictionStationList;
import model.Postalcodes;
import model.PredictionPointList;
import model.Price;
import model.Route;

/**
 * Dateiverwaltung
 *
 * @author Sebastian Drath
 */
public class CSVManager {

    private static boolean printMessages = true;
    private static Set<Integer> failures = new LinkedHashSet<>();

    private static final String inputPath = "Eingabedaten" + File.separator;
    private static final String routeInputPath = inputPath + "Fahrzeugrouten" + File.separator;
//    private static final String routeTraindataPath = routeInputPath + "Trainingsdaten" + File.separator;
    private static final String predictionInputPath = inputPath + "Vorhersagezeitpunkte" + File.separator;
//    private static final String predictionTraindataPath = predictionInputPath + "Trainingsdaten" + File.separator;
    private static final String pricePath = inputPath + "Benzinpreise" + File.separator;
    private static final String holidayPath = inputPath + "Ferien" + File.separator;

    private static final String outputPath = "Ausgabedaten" + File.separator;
    private static final String routeOutputPath = outputPath + "Tankstrategien" + File.separator;
    private static final String predictionOutputPath = outputPath + "Vorhersagen" + File.separator;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");

    /**
     * Lädt alle relevanten Dateien die zukünftig gebraucht werden. Lädt alle Tankstellen, Feriendaten sowie die Zurordnung
     * von Postleitzahlen zu Bundesländern. Erstellt die Eingabeordner, wenn sie nicht existieren.
     * @return alle Tankstellen aus der Datei Tankstellen.csv
     */
    public static Map<Integer, GasStation> initialImport() {
        importPostalcodes();
        importHolidays();

        String[] files = readFilenames(new File(pricePath), ".csv");
        if (files == null || files.length == 0) {
            failures.add(305);
        }

        // erstelle Eingabeverzeichnisse
        new File(routeInputPath).mkdirs();
//        new File(routeTraindataPath).mkdirs();
        new File(predictionInputPath).mkdirs();
//        new File(predictionTraindataPath).mkdirs();
        new File(pricePath).mkdirs();
        new File(holidayPath).mkdirs();

        return importGasStations();
    }

    /**
     * Lädt alle Tankstellen aus der Datei Tankstellen.csv
     * @return alle Tankstellen in Tankstellen.csv
     */
    private static Map<Integer, GasStation> importGasStations() {
        String filename = inputPath + "Tankstellen.csv";
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
                failures.add(201);
                continue;
            }
            int postcode = getInteger(lineElements[5]);
            GasStation station = new GasStation(
                    getInteger(lineElements[0]),// index
                    lineElements[1],// Name
                    lineElements[2],// Marke
                    lineElements[3],// Straße
                    lineElements[4],// Hausnummer
                    postcode,// Postleitzahl
                    Postalcodes.getState(postcode), // Bundesland
                    lineElements[6],// Ort
                    getDouble(lineElements[7]),// Längengrad
                    getDouble(lineElements[8])// Breitengrad
            );
            stations.put(station.getID(), station);
        }
        return stations;
    }

    /**
     * Gibt alle Fehler, die während des initialen Imports augetreten sind, als Liste zurück.
     * @return Liste von aufgetretenen Fehlern
     */
    public static List<Integer> getOccuredFailures() {
        List<Integer> res = new ArrayList<>(failures);
        failures.clear();
        return res;
    }

    /**
     * Importiert die Zuordnung der Postleitzahlen zu Bundesländern
     */
    private static void importPostalcodes() {
        if (Postalcodes.isImported()) {
            return;
        }
        String filename = inputPath + "postalcode2federalstate.csv";
        List<String> lines = readFile(filename);
        if (lines == null) {
            failures.add(204);
            return;
        }
        for (String line : lines) {
            String[] lineElements = line.split(";");
            if (lineElements.length != 3) {
                System.err.println("Input postalcode: Illegal Input row size");
                failures.add(205);
                continue;
            }
            Postalcodes.addPostcodeRange(
                    getInteger(lineElements[0]),// lower
                    getInteger(lineElements[1]),// upper
                    lineElements[2]// state
            );
        }
    }

    /**
     * Importiert alle relevanten Informationen für die anzuzeigende Route
     * @param stations Liste mit allen Tankstellen
     * @param routeName Routenname zu ladenden Route
     * @return Routen-Objekt, die eine Liste aller Tankstellen beinhaltet
     */
    public static Route importRoute(Map<Integer, GasStation> stations, String routeName) {
        File routeFile = new File(routeInputPath + routeName + (routeName.endsWith(".csv") ? "" : ".csv"));
        List<String> lines = readFile(routeFile);
        if (printMessages) {
            System.out.println("import route: " + routeName);
        }
        if (lines == null) {
            System.err.println("Could not import Route \"" + routeFile.getName() + "\"!");
            return null;
        }
        int capacity = getInteger(lines.remove(0));
        if(capacity < 0) return null;
        Route route = new Route(routeName,capacity);
        for (String line : lines) {
            String[] lineElements = prepareRowData(line);
            if (lineElements.length != 2) {
                System.err.println("Import route: Illegal Input row size");
                return null;
            }
            route.addRouteElement(stations.get(getInteger(lineElements[1])), getDate(lineElements[0]));
        }
        if (printMessages) {
            System.out.println(route);
        }
        return route;
    }

    /**
     * Verifiziert Routen. Überprüft beispielsweise, ob die Tankstops aufsteigende
     * Zeiten aufweisen und ob die Tankstellen IDs gefunden werden konnten.
     * @param stations Liste aller Tankstellen
     * @return Gibt alle Warnungen zurück, die während der Verifizierung aufgetreten sind
     */
    public static List<String> checkRoutes(Map<Integer, GasStation> stations) {
        List<String> routeWarnings = new ArrayList<>();
        boolean backup = printMessages;
        printMessages = false;
        for (String filename : readRouteNames()) {
            Route cur = importRoute(stations, filename);
            if(cur == null) {
            	routeWarnings.add(filename + ": Formatierung fehlerhaft. Die Datei konnte nicht importiert werden.");
            	continue;
            }
            if (cur.getTankCapacity() <= 0) {
                routeWarnings.add(cur.getName() + ": Tankkapazität zu gering.");
            }
            if (cur.getLength() == 0) {
                routeWarnings.add(cur.getName() + ": Keine Elemente enthalten.");
                continue;
            }
            for (int i = 0; i < cur.getLength(); i++) {
                if (cur.get(i).getStation() == null) {
                    routeWarnings.add(cur.getName() + ": Tankstelle in Routenelement " + (i + 1) + " konnte nicht gefunden werden.");
                    continue;
                }
                if(i == 0) continue;
                if (!cur.get(i).getTime().after(cur.get(i-1).getTime())) {
                    routeWarnings.add(cur.getName() + ": Tankstop mit ID " + cur.get(i).getStation().getID() + " liegt zeitlich vor dem vorherigen.");
                }
                double fuelNeed = cur.get(i).getStation().getDistance(cur.get(i-1).getStation()) * RefillStrategies.GAS_USED_PER_KM;
            	if(fuelNeed > cur.getTankCapacity()) {
            		routeWarnings.add(cur.getName() + ": Die Strecke zur Tankstelle mit ID " + cur.get(i).getStation().getID() + " ist zu lang für die gewählte Tankkapazität.");
            	}
            }
        }
        printMessages = backup;
        return routeWarnings;
    }

    /**
     * Exportiert Vorhersagezeitpunkte
     * @param stations Liste von Tankstellen innerhalb der Vorhersagezeitpunkte-Datei
     */
    public static void exportPredictions(IPredictionStationList stations) {
        writeCSV(stations);
    }

    /**
     * Importiert Vorhersagezeitpunkte
     * @param stations Liste aller Tankstellen
     * @param predictionName Name der Vorhersagezeitpunkt-Datei
     * @return Liste der Vorhersagezetpunkt-Objekte
     */
    public static PredictionPointList importPredictionPoints(Map<Integer, GasStation> stations, String predictionName) {
        File predictionFile = new File(predictionInputPath + predictionName + (predictionName.endsWith(".csv") ? "" : ".csv"));
        List<String> lines = readFile(predictionFile);
        if (printMessages) {
            System.out.println("import predictionpoints: " + predictionName);
        }
        if (lines == null) {
            System.err.println("Could not import Prediction \"" + predictionFile.getName() + "\"!");
            return null;
        }
        PredictionPointList result = new PredictionPointList(predictionName);
        for (String line : lines) {
            String[] lineElements = prepareRowData(line);
            if (lineElements.length != 3) {
                System.err.println("Import prediction: Illegal Input row size");
                continue;
            }
            result.addPredictionElement(stations.get(getInteger(lineElements[2])), getDate(lineElements[0]), getDate(lineElements[1]));
        }
        return result;
    }

    /**
     * Importiere Preise der Tankstellen
     * @param stations Liste von Vorhersagezeitpunkt-Tankstellen
     */
    public static void importPrices(IPredictionStationList stations) {
        if (stations == null) {
            return;
        }
        for (int i = 0; i < stations.getLength(); i++) {
            importPrice(stations.get(i).getStation());
        }
    }

    /**
     * 
     * @param gs Importiere Preise für eine einzelne Tankstelle
     * @return Boolean, ob das importieren erfolgreich war oder o es Komplikationen gab
     */
    private static boolean importPrice(GasStation gs) {
        if (gs.hasPriceList()) {
            System.out.println("Prices for " + gs + " already imported. (size=" + gs.getPriceListSize() + ")");
            return true;
        }
        String filename = pricePath + gs.getID() + ".csv";
        List<String> lines = readFile(new File(filename));
        if (lines == null) {
            System.err.println("Could not import prices for " + gs);
            return false;
        }
        List<Price> prices = new ArrayList<Price>();
        for (String line : lines) {
            String[] lineElements = prepareRowData(line);
            if (lineElements.length != 2) {
                System.err.println("Import prices: Illegal Input row size");
                return false;
            }
            prices.add(new Price(getDate(lineElements[0]), getInteger(lineElements[1])));
        }
        gs.setPriceList(prices);
        return true;
    }

    /**
     * Importiere Ferien der einzelnen Bundesländer
     */
    private static void importHolidays() {
        String[] holidayNames = {"Winter", "Ostern", "Pfingsten", "Sommer", "Herbst", "Weihnachten"};
        String[] files = readFilenames(new File(holidayPath), ".txt");
        if (files == null) {
            System.err.println("InputFolder for holidays does not exist."); // Der Ordner exsitiert nicht
            failures.add(206);
            return;
        }
        if (files.length == 0) {
            failures.add(206); // Es existieren keine Feriendaten im Ordner
        }
        for (String filename : files) {
            int year = getInteger(filename.substring(0, 4)); // setze die ersten vier Zeichen des Dateinames als Jahr
            if (year < 0) {
                System.err.println("Unecpected holiday filename: " + filename); // die ersten vier Zeichen sind keine Zahl
                failures.add(207);
                continue;
            }
            List<String> lines = readFile(holidayPath + filename);
            int ctr = 0; // Zählt die Zeilen pro Bundeslandeintrag
            String readState = null;
            FederalState curState = null;
            for (String line : lines) {
                if (ctr == 0) { // jede siebte Zeile ist der Bundeslandname
                    readState = line;
                    curState = FederalState.getFederalState(line);
                    if (curState == FederalState.DEF) {
                        System.err.println("Illegal federal state: " + line);
                        failures.add(208);
                    }
                } else if (ctr > 0 && ctr <= holidayNames.length) { // die Zeilen stehen immer in der selben Reihenfolge für die jeweiligen Ferien
                    if (curState == null || curState == FederalState.DEF) {
                        System.err.println("Current State could not be parsed: " + readState + " (Holidayfile " + filename + ")");
                        failures.add(208);
                    }
                    if (!Holidays.addHoliday(year, curState, holidayNames[ctr - 1], line)) {
                        failures.add(208);
                    }
                } else {
                    System.err.println("Count to high (" + ctr + ")"); // der Teil sollte nie erreichbar sein
                }
                ctr++;
                ctr %= holidayNames.length + 1; // es wird von 0 bis 6 gezählt
            }
        }
    }

    /**
     * Unterteilt die übergebene importierte Zeile in einzelne Spalten und bereitet Zelleninhalt vor.
     * Das heißt ungültige Zeichen, wie zum Beispiel Anführungsstriche und führende Leerzeichen, werden entfernt
     * 
     * @param row Zeile, die vorbereitet werden soll
     * @return String array. Jedes Feld enspricht einer Zelle in der importierten Tabelle
     */
    private static String[] prepareRowData(String row) {
        String[] res = row.split(";");
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].replaceAll("\"", "").trim();
        }
        return res;
    }

    /**
     * Gibt den Inhalt der Datei mit dem übergebenen Dateinamen als Liste mit den gelesenen Zeilen zurück. Zeilen die mit "##" beginnen werden ignoriert.
     * @param filename Name der Datei, die gelesen werden soll.
     * @return Inhalt der Gelesenen Datei.
     */
    private static List<String> readFile(String filename) {
        return readFile(new File(filename));
    }

    /**
     * Gibt den Inhalt der angegebenen Datei als Liste mit den gelesenen Zeilen zurück. Zeilen die mit "##" beginnen werden ignoriert.
     * @param file Datei, die gelesen werden soll.
     * @return Inhalt der Gelesenen Datei.
     */
    private static List<String> readFile(File file) {
        List<String> lines = new ArrayList<>();
        if (file == null || !file.exists()) {
            System.err.println("File \"" + file + "\" does not exist!");
            return null;
        }
        Reader r = null;
        BufferedReader br = null;
        try {
            r = new InputStreamReader(new FileInputStream(file), "UTF-8");
            br = new BufferedReader(r);
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.startsWith("##")) lines.add(line); // ## zum auskommentieren von Zeilen innerhalb einer Datei
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

    /**
     * Schreibt in eine Datei
     * @param stations Liste der Tankstellen
     * @return ob das Schreiben erfolgreich war
     */
    private static boolean writeCSV(IPredictionStationList stations) {
        String path = "";
        if (stations instanceof Route) {
            path = routeOutputPath;
        } else if (stations instanceof PredictionPointList) {
            path = predictionOutputPath;
        }
        new File(path).mkdirs(); // erstellt output ordner
        File file = new File(path + stations.getName() + (stations.getName().endsWith(".csv") ? "" : ".csv"));

        Writer w = null;
        BufferedWriter bw = null;
        try {
            w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            bw = new BufferedWriter(w);
            for (int i = 0; i < stations.getLength(); i++) {
                bw.write(stations.get(i).toCSVString());
                if (i + 1 < stations.getLength()) {
                    bw.newLine();
                }
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

    /**
     * Parst eine ganze Zahl aus einem String und gibt sie zurück. Gibt null zurück, wenn keine ganze Zahl geparst werden konnte.
     *
     * @param s Der Text aus dem die ganze Zahl geparst werden soll
     * @return die ganze Zahl oder -1, wenn keine Zahl geparst werden konnte
     */
    private static int getInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parst eine Fließkommazahl aus einem String und gibt sie zurück. Gibt null zurück, wenn keine Fließkommazahl geparst werden konnte.
     *
     * @param s Der Text aus dem die Fließkommazahl geparst werden soll
     * @return die Fließkommazahl oder null, wenn keine Fließkommazahl geparst werden konnte
     */
    private static double getDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parst ein Datum aus einem String und gibt es zurück. Gibt null zurück, wenn kein Datum geparst werden konnte.
     *
     * @param s Der Text aus dem das Datum geparst werden soll
     * @return das Datum oder null, wenn kein Datum geparst werden konnte
     */
    private static Date getDate(String s) {
        try {
            return dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Liest alle Routennamen aus, die sich im Routenordner befinden
     * @return  Array mit allen Routennamen
     */
    public static String[] readRouteNames() {
        return readFilenames(new File(routeInputPath), ".csv");
    }

     /**
     * Liest alle Vorhersagezeitpunkte aus, die sich im Vorhersagezeitpunktordner befinden
     * @return  Array mit allen Vorhersagezeitpunktnamen
     */
    public static String[] readPredictionPointNames() {
        return readFilenames(new File(predictionInputPath), ".csv");
    }

    /**
     * Liest alle Dateien mit der angebenen Endung, die sich in einem Ordner befinden
     * @param folder Ort des Ordners
     * @param ending Dateiendung, die gesucht wird
     * @return Array mit allen Dateinamen
     */
    private static String[] readFilenames(File folder, String ending) {
        String[] listOfFiles = folder.list(new InputFileFilter(ending));
        return listOfFiles;
    }

    /**
     * Gibt das für die Ein- und Ausgabedateien verwendete Datumsformat zurück.
     *
     * @return Datumsformat für die Ein- und Ausgabedateien
     */
    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Kopiere Route
     * @param selectedFile Routendatei, die kopiert werden soll
     * @throws FileNotFoundException Wenn die Datei nicht gefunden werden konnte
     * @throws IOException IOException Wenn beim Lesen oder Schreiben ein Fehler aufgetreten ist
     */
    public static void copyRoute(File selectedFile) throws FileNotFoundException, IOException {
        Path path = Paths.get(routeInputPath + selectedFile.getName());
        copyFile(selectedFile, path);
    }

     /**
     * Kopiere Vorhersagezeitpunkte
     * @param selectedFile Vorhersagezeitpunktdatei, die kopiert werden soll
     * @throws FileNotFoundException Wenn die Datei nicht gefunden werden konnte
     * @throws IOException IOException Wenn beim Lesen oder Schreiben ein Fehler aufgetreten ist
     */
    public static void copyPredictionPoints(File selectedFile) throws FileNotFoundException, IOException {
        Path path = Paths.get(predictionInputPath + selectedFile.getName());
        copyFile(selectedFile, path);
    }

     /**
     * Kopiere Datei
     * @param selectedFile Datei, die kopiert werden soll
     * @param dest Zielordner
     * @throws FileNotFoundException Wenn die Datei nicht gefunden werden konnte
     * @throws IOException IOException Wenn beim Lesen oder Schreiben ein Fehler aufgetreten ist
     */
    private static void copyFile(File selectedFile, Path dest) throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(selectedFile);
        Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
        is.close();
    }
    
    /*@Deprecated
    public static void checkImportPrediction(IPredictionStationList stations) {
    	String path = "";
    	if(stations instanceof Route) {
    		path = routeTraindataPath;
    	} else if (stations instanceof PredictionPointList) {
    		path = predictionTraindataPath;
    	} else {
    		return;
    	}
    	path += stations.getName() + File.separator;
    	File f = new File(path);
    	if(!f.isDirectory()) return;
    	String[] names = readFilenames(f,".train");
    	for(String name: names) {
    		System.out.println(name);
    	}
    }*/
}

/**
 * Dateifilter für Routennamen
 * @author Sebastian Drath
 */
class InputFileFilter implements FilenameFilter {

    private String ending;

    
    /**
     * Initialisiert den Dateifilter
     * @param ending Dateinamen-Endung
     */
    public InputFileFilter(String ending) {
        this.ending = ending;
    }

    /**
     * Überprüft, ob eine Datei mit dem Filter angezeigt wird
     * @param dir Pfad in dem gesucht wird
     * @param name Name der Datei
     * @return true, wenn die Datei akzeptiert wurde, ansonsten false
     */
    @Override
    public boolean accept(File dir, String name) {
        File f = new File(dir.getPath() + File.separator + name);
        return f.isFile() && name.endsWith(this.ending);
    }
}
