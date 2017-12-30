package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.CSVManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import model.GasStation;
import model.RefuelStop;
import model.Route;
import model.MainModel;
import view.MainView;
import view.ProgressView;

public class GasStationController {

    private Map<Integer, GasStation> allStations;
    private Route route;
    private List<PredictionUnit> predictions;
    private MainModel mainModel;
    private MainView mainView;
    private final String standardRoute = "Hannover Hildesheim";
    private Task<Void> predictionThread;
    private ProgressView pw;
    //private Stage primaryStage;

    public GasStationController(Stage primaryStage) {
        allStations = CSVManager.importGasStations();
        route = CSVManager.importStandardRoute(allStations, standardRoute);
        CSVManager.importPrices(route);
        mainView = new MainView(primaryStage, this);
        mainModel = new MainModel();
        pw = new ProgressView(route.getName());
        this.predictions = new ArrayList<>();
        this.trainPrediction();
    }

    public void addGasStation(GasStation station) {
        if (allStations.containsKey(station.getID())) {
            System.err.println("Warning: Station with same ID will be inserted.");
        }
        allStations.put(station.getID(), station);
    }

    public GasStation getStation(int id) {
        return allStations.get(id);
    }

    public Route getRoute() {
        return route;
    }

    public MainModel getMainModel() {
        return mainModel;
    }

    public void trainPrediction() {
        predictionThread = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Start prediction");
                for (int i = 0; i < route.getLength(); i++) {
                    RefuelStop rs = route.get(i);
                    GasStation gs = rs.getStation();
                    if (gs.getPriceListSize() == 0) {
                        System.err.println("Pricelist of " + gs + " does not exist");
                        continue;
                    }
                    PredictionUnit pu = new PredictionUnit(gs, route.get(0).getTime());
                    predictions.add(pu);
                    boolean trainSuccess = pu.train();
                    if (trainSuccess) {
                        rs.setPredictedPrices(pu.testAndSetHourSteps());
                    }
                    updateProgress((i + 1) * 100 / route.getLength(), 100);
                    //pw.setProgress((i + 1)/ route.getLength());
                    //System.out.println(((i + 1) * 100 / route.getLength()) + " %");
                }
                System.out.println("Prediction finished");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mainModel.calculateGasUsage(route);
                        mainView.displayRoute(route);
                        pw.close();
                    }
                });
                return null;
            }

        };
        pw.getProgressBar().progressProperty().bind(predictionThread.progressProperty());
        new Thread(predictionThread).start();
    }

    public void changeCurrentRoute(String text) {
        mainView.hide();
        route = CSVManager.importStandardRoute(allStations, text);
        CSVManager.importPrices(route);
        pw = new ProgressView(route.getName());
        this.predictions = new ArrayList<>();
        this.trainPrediction();
    }
}
