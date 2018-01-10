/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import model.IPredictionStations;
import model.PredictionPoints;
import model.Route;

/**
 *
 * @author Admin
 */
public class ProgressView {

    private String name;
    private ProgressBar progressBar;

    private Stage progressStage;
    private Label l;

    public ProgressView(IPredictionStations predictionStations) {
    	this.name = predictionStations.getName();
        progressStage = new Stage();
        progressStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent evt) {
				System.exit(0); // schliesse Programm
			}
        	
        });
        BorderPane bp = new BorderPane();
        Scene scene = new Scene(bp, 300, 45);
        progressStage.setResizable(false);
        progressStage.initStyle(StageStyle.UTILITY);
        progressStage.setScene(scene);
        progressStage.setTitle("Preise werden berechnet..");
        if(predictionStations instanceof Route) {
        	l = new Label("Lade Route:\t" + this.name);
        } else if(predictionStations instanceof PredictionPoints) {
        	l = new Label("Lade Vorhersagezeitpunkte:\t" + this.name);
        } else {
        	l = new Label("Lade:\t" + this.name);
        }
        progressBar = new ProgressBar(0);
        progressBar.setPrefSize(300, 28);
        bp.setTop(l);
        bp.setBottom(progressBar);
        //progressStage.initModality(Modality.WINDOW_MODAL);
        //progressStage.initOwner(primaryStage);
        progressStage.show();
        progressStage.setAlwaysOnTop(true);
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }

    public void close() {
        progressStage.close();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
