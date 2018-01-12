package controller;

import java.util.Date;
import java.util.Map;

import controller.PredictionUnit.Mode;
import io.CSVManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import model.GasStation;
import model.IPredictionStation;
import model.IPredictionStations;
import model.PredictionPoint;
import model.PredictionPoints;
import model.Route;
import view.MainView;
import view.PopupBox;
import view.ProgressView;

public class GasStationController {

//    private static final boolean initialImportRoute = true;

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
            PopupBox.displayError(301);
            return;
        }
        //route = CSVManager.importStandardRoute(allStations);
        mainView = new MainView(primaryStage, this);
        refillStrategies = new RefillStrategies();
        mainView.show();
        /*if (route == null) {
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
        }*/
        
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
                    if(station.isPredicted()) continue;
                    GasStation gs = station.getStation();
                    if (gs.getPriceListSize() == 0) {
                        System.err.println("Pricelist of " + gs + " does not exist");
                        continue;
                    }
                    Date until = null;
                    if(stations instanceof Route) {
                    	until = ((Route)stations).getPriceKnownUntil();
                    } else if (station instanceof PredictionPoint) {
                    	until = ((PredictionPoint)station).getPriceKnownUntil();
                    }
                    PredictionUnit pu = new PredictionUnit(gs, until, Mode.SINGLE_LAYER);
//                    predictions.add(pu);
                    boolean trainSuccess = pu.train();
                    if (trainSuccess) {
                    	station.setPrediction(pu);
//                        station.setPredictedPrices(pu.testAndSetHourSteps());
                    }
                    updateProgress((i + 1) * 100 / stations.getLength(), 100);
                }
                System.out.println("Prediction finished");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                    	showPredictedStations(stations);
                    	CSVManager.exportPredictions(stations);
                    }
                });
                return null;
            }

        };
        pw.getProgressBar().progressProperty().bind(predictionThread.progressProperty());
        new Thread(predictionThread).start();
    }
    
    private void showPredictedStations(IPredictionStations stations) {
    	if (stations instanceof PredictionPoints) {
            mainView.displayPredictionPoints((PredictionPoints) stations);
            pw.close();
        } else if (stations instanceof Route) {
            refillStrategies.calculateGasUsage((Route) stations);
            mainView.displayRoute((Route) stations);
            pw.close();
        }
    }

    public void switchToRoute(String routeName) {
        Route routeTest = CSVManager.importRoute(allStations, routeName);
        if (routeTest == null) {
            PopupBox.displayError(302);
            return;
        }
        if(!routeTest.equals(route)) {
        	route = routeTest;
            CSVManager.importPrices(route);
        }
        mainView.hide();
        pw = new ProgressView(route);
//        this.predictions = new ArrayList<>();
        this.trainPrediction(route);
    }

    public void switchToPredictionPoints(String predictionPointName) {
        PredictionPoints predictionPointsTest = CSVManager.importPredictionPoints(allStations, predictionPointName);
        if (predictionPointsTest == null) {
            PopupBox.displayError(303);
            return;
        }
        if(!predictionPointsTest.equals(predictionPoints)) {
        	predictionPoints = predictionPointsTest;
        	CSVManager.importPrices(predictionPoints);
        }
        mainView.hide();
        pw = new ProgressView(predictionPoints);
//        this.predictions = new ArrayList<>();
        this.trainPrediction(predictionPoints);
    }
}
