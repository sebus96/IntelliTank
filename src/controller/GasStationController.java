package controller;

import java.util.Map;

import io.CSVManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import model.GasStation;
import model.IPredictionStation;
import model.IPredictionStations;
import model.MainModel;
import model.PredictionPoints;
import model.Route;
import view.MainView;
import view.ProgressView;

public class GasStationController {

    private Map<Integer, GasStation> allStations;
    private Route route;
    private PredictionPoints predictionPoints;
//    private List<PredictionUnit> predictions;
    private MainModel mainModel;
    private MainView mainView;
    private ProgressView pw;

    public GasStationController(Stage primaryStage) {
        allStations = CSVManager.importGasStations();
        route = CSVManager.importStandardRoute(allStations);
        CSVManager.importPrices(route);
        mainView = new MainView(primaryStage, this);
        mainModel = new MainModel();
        pw = new ProgressView(route.getName());
//        this.predictions = new ArrayList<>();
        this.trainPrediction(route);
    }

//    public void addGasStation(GasStation station) {
//        if (allStations.containsKey(station.getID())) {
//            System.err.println("Warning: Station with same ID will be inserted.");
//        }
//        allStations.put(station.getID(), station);
//    }

    public GasStation getStation(int id) {
        return allStations.get(id);
    }

    public Route getRoute() {
        return route;
    }
    
    public PredictionPoints getPredictionPoints() {
    	return predictionPoints;
    }

    public MainModel getMainModel() {
        return mainModel;
    }

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
                    PredictionUnit pu = new PredictionUnit(gs, station.getTime());
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
                    	if(stations instanceof PredictionPoints) {
	                        mainView.displayPredictionPoints((PredictionPoints)stations);
	                        pw.close();
                    	} else if(stations instanceof Route) {
                    		mainModel.calculateGasUsage((Route)stations);
                            mainView.displayRoute((Route)stations);
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

    public void changeCurrentRoute(String routeName) {
        mainView.hide();
        route = CSVManager.importRoute(allStations, routeName);
        CSVManager.importPrices(route);
        pw = new ProgressView(route.getName());
//        this.predictions = new ArrayList<>();
        this.trainPrediction(route);
    }

    public void showPredictionPoints(String predictionPointName) {
        mainView.hide();
        predictionPoints = CSVManager.importPredictionPoints(allStations, predictionPointName);
        CSVManager.importPrices(predictionPoints);
        pw = new ProgressView(predictionPoints.getName());
//        this.predictions = new ArrayList<>();
        this.trainPrediction(predictionPoints);
    }
}
