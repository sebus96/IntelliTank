package controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import controller.PredictionUnit.Mode;
import io.CSVManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import model.GasStation;
import model.IPredictionStation;
import model.IPredictionStationList;
import model.PredictionPoint;
import model.PredictionPointList;
import model.Route;
import view.MainView;
import view.PopupBox;
import view.ProgressView;

/**
 * Der Controller im MVC.
 * @author Axel Claassen, Sebastian Drath
 */
public class GasStationController {

    private Map<Integer, GasStation> allStations;
    private Route route;
    private PredictionPointList predictionPoints;
    private RefillStrategies refillStrategies;
    private MainView mainView;
    private ProgressView pw;

    /**
     * Initialisiert den GasStationController
     *
     * @param primaryStage das Hauptfenster
     */
    public GasStationController(Stage primaryStage) {
        allStations = CSVManager.initialImport();
        if (allStations == null) {
            PopupBox.displayError(301);
            return;
        }
        List<String> warnings = CSVManager.checkRoutes(allStations);
        mainView = new MainView(primaryStage, this);
        refillStrategies = new RefillStrategies();
        mainView.show();
        PopupBox.displayImportWarnings();
        PopupBox.displayRouteWarnings(warnings);
        

    }
    

    public Route getRoute() {
        return route;
    }

    public PredictionPointList getPredictionPoints() {
        return predictionPoints;
    }
    

    /**
     * Startet die Vorhersage der einzelnen Tankstellen in einem anderen Thread
     *
     * @param stations die zu vorhersagenden Tankstellen
     */
    public void trainPrediction(IPredictionStationList stations) {
        Task<Void> predictionThread = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Start prediction");
                for (int i = 0; i < stations.getLength(); i++) {
                    IPredictionStation station = stations.get(i);
                    if (station.isPredicted()) {
                        continue;
                    }
                    GasStation gs = station.getStation();
                    if (gs.getPriceListSize() == 0) {
                        System.err.println("Pricelist of " + gs + " does not exist");
                        continue;
                    }
                    Date until = null;
                    if (stations instanceof Route) {
                        until = ((Route) stations).getPriceKnownUntil();
                    } else if (station instanceof PredictionPoint) {
                        until = ((PredictionPoint) station).getPriceKnownUntil();
                    }
                    Mode mode = Mode.SINGLE_LAYER;
                    Perceptron importedPerceptron = CSVManager.importPrediction(stations, i, mode);
                    PredictionUnit pu = null;
                    if(importedPerceptron == null) pu = new PredictionUnit(station, until,mode );
                    else pu = new PredictionUnit(station, importedPerceptron );
                    boolean trainSuccess = pu.train();
                    if (trainSuccess) {
                        station.setPrediction(pu);
                        if(CSVManager.writePrediction(stations, i, pu.getPerceptron())) {
                        	System.out.println("Perceptron successfully written");
                        } else {
                        	System.err.println("Error while writing perceptron");
                        }
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

    /**
     * Sagt der entsprechenden Ansicht die vorhergesagten Tankstellen anzuzeigen
     *
     * @param stations Anzuzeigende Tankstellen
     */
    private void showPredictedStations(IPredictionStationList stations) {
        if (stations instanceof PredictionPointList) {
            mainView.displayPredictionPoints((PredictionPointList) stations);
            pw.close();
        } else if (stations instanceof Route) {
        	boolean res = true;
        	if(stations.getLength() > 1) // wenn nur ein Routenelement vorhanden ist, wird keine Tankstrategie benötigt.
        		res = refillStrategies.calculateGasUsage((Route) stations);
            mainView.displayRoute((Route) stations);
            pw.close();
            if (!res) {
                PopupBox.displayError(306);
            }
        }
    }

    /**
     * Die neu angeklickte Route wird geöffnet
     *
     * @param routeName Name der ausgewählten Route
     */
    public void switchToRoute(String routeName) {
        Route routeTest = CSVManager.importRoute(allStations, routeName);
        if (routeTest == null) {
            PopupBox.displayError(302);
            return;
        }
        if (!routeTest.equals(route)) {
            route = routeTest;
            CSVManager.importPrices(route);
        }
        // checkt, ob die Tankstops zu weit auseinander liegen für die Tankkapazität
        if(!routeTest.checkTankCapacity()) {
        	PopupBox.displayError(307);
        	return;
        }
        mainView.hide();
        pw = new ProgressView(route);
        this.trainPrediction(route);
    }

    /**
     * Die neu angeklickte Route wird geöffnet
     *
     * @param predictionPointName Name des ausgewählten vorhersagezeitpunktes
     */
    public void switchToPredictionPoints(String predictionPointName) {
        PredictionPointList predictionPointsTest = CSVManager.importPredictionPoints(allStations, predictionPointName);
        if (predictionPointsTest == null) {
            PopupBox.displayError(303);
            return;
        }
        if (!predictionPointsTest.equals(predictionPoints)) {
            predictionPoints = predictionPointsTest;
            CSVManager.importPrices(predictionPoints);
        }
        mainView.hide();
        pw = new ProgressView(predictionPoints);
        this.trainPrediction(predictionPoints);
    }
}
