/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Admin
 */
public class ProgressView {

    String routeName;
    ProgressBar progressBar;

    Stage progressStage;
    Label l;

    public ProgressView(String routeName) {
        this.routeName = routeName;
        progressStage = new Stage();
        BorderPane bp = new BorderPane();
        Scene scene = new Scene(bp, 300, 45);
        progressStage.setResizable(false);
        progressStage.initStyle(StageStyle.UTILITY);
        progressStage.setScene(scene);
        progressStage.setTitle("Preise werden berechnet..");
        l = new Label("Lade Route:\t" + routeName);
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
