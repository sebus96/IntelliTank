package controller;

import java.util.Map;

import io.CSVManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import model.GasStation;
import model.IPredictionStation;
import model.IPredictionStations;
import model.PredictionPoints;
import model.Route;
import view.MainView;
import view.ProgressView;
import view.PopupBox;

public class GasStationController {

    private static final boolean initialImportRoute = true;

    private Map<Integer, GasStation> allStations;
    private Route route;
    private PredictionPoints predictionPoints;
//    private List<PredictionUnit> predictions;
    private RefillStrategies refillStrategies;
    private MainView mainView;
    private ProgressView pw;

    public GasStationController(Stage primaryStage) {
        allStations = CSVManager.importGasStations();
        if (allStations == null) {
            PopupBox.displayError("Die Datei Tankstellen.csv wurde nicht gefunden!\n\nDas Programm konnte nicht gestartet werden.");
            return;
        }
        route = CSVManager.importStandardRoute(allStations);
        mainView = new MainView(primaryStage, this);
        refillStrategies = new RefillStrategies();
        if (route == null) {
            PopupBox.displayWarning("Die Standartroute konnte nicht geöffnet werden.");
            mainView.show();
        } else {
            predictionPoints = CSVManager.importStandardPredictionPoints(allStations);
            CSVManager.importPrices(route);
            CSVManager.importPrices(predictionPoints);
            if (initialImportRoute) {
                pw = new ProgressView(route);
                this.trainPrediction(route);
            } else {
                pw = new ProgressView(predictionPoints);
                this.trainPrediction(predictionPoints);
            }
        }
    }

//    public void addGasStation(GasStation station) {
//        if (allStations.containsKey(station.getID())) {
//            System.err.println("Warning: Station with same ID will be inserted.");
//        }
//        allStations.put(station.getID(), station);
//    }
/*
    public GasStation getStation(int id) {
        return allStations.get(id);
    }*/
    public Route getRoute() {
        return route;
    }

    public PredictionPoints getPredictionPoints() {
        return predictionPoints;
    }

    /*public RefillStrategies getMainModel() {
        return refillStrategies;
    }*/
    public void trainPrediction(IPredictionStations stations) {
        Task<Void> predictionThread = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Start prediction");
                for (int i = 0; i < stations.getLength(); i++) {
                    IPredictionStation station = stations.get(i);
                    GasStation gs = station.getStation();
                    if (gs.getPriceListSize() == 0) {
                        System.err.println("Pricelist of " + gs + " does not exist");
                        continue;
                    }
                    PredictionUnit pu = new PredictionUnit(gs, station.getTime()); // TODO: richtige Zeit benutzen
//                    predictions.add(pu);
                    boolean trainSuccess = pu.train();
                    if (trainSuccess) {
                        station.setPredictedPrices(pu.testAndSetHourSteps());
                    }
                    updateProgress((i + 1) * 100 / stations.getLength(), 100);
                }
                System.out.println("Prediction finished");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (stations instanceof PredictionPoints) {
                            mainView.displayPredictionPoints((PredictionPoints) stations);
                            pw.close();
                        } else if (stations instanceof Route) {
                            refillStrategies.calculateGasUsage((Route) stations);
                            mainView.displayRoute((Route) stations);
                            pw.close();
                        }
                    }
                });
                return null;
            }

        };
        pw.getProgressBar().progressProperty().bind(predictionThread.progressProperty());
        new Thread(predictionThread).start();
    }

    public void switchToRoute(String routeName) {
        Route routeTest = CSVManager.importRoute(allStations, routeName);
        if (routeTest == null) {
            PopupBox.displayError(routeName + " konnte nicht geladen werden. Datei fehlerhaft oder nicht mehr vorhanden.");
            return;
        }
        mainView.hide();
        route = routeTest;
        CSVManager.importPrices(route);
        pw = new ProgressView(route);
//        this.predictions = new ArrayList<>();
        this.trainPrediction(route);
    }

    public void switchToPredictionPoints(String predictionPointName) {
        PredictionPoints predictionPointsTest = CSVManager.importPredictionPoints(allStations, predictionPointName);
        if (predictionPointsTest == null) {
            PopupBox.displayError(predictionPointName + " konnte nicht geladen werden. Datei fehlerhaft oder nicht mehr vorhanden.");
            return;
        }
        mainView.hide();
        predictionPoints = predictionPointsTest;
        CSVManager.importPrices(predictionPoints);
        pw = new ProgressView(predictionPoints);
//        this.predictions = new ArrayList<>();
        this.trainPrediction(predictionPoints);
    }
}
