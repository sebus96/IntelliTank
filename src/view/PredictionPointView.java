package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.PredictionPoint;
import model.PredictionPoints;

public class PredictionPointView {

    Scene scene;
    BorderPane border;
    Pane pane;
    private TableView<PredictionPoint> table;
    
    public PredictionPointView(Scene scene, BorderPane border) {
        table = new TableView<PredictionPoint>();
        this.scene = scene;
        this.border = border;
    }
    public Scene buildPredictionPointView(PredictionPoints predictionPoints) {
     
        final Label label = new Label("Tabelle");
        label.setFont(new Font("Arial", 20));

        TableColumn<PredictionPoint, Integer> nrColumn = new TableColumn("Nr.");
        nrColumn.setMinWidth(128);
        TableColumn<PredictionPoint, String> gasStationColumn = new TableColumn("Tankstelle");
        gasStationColumn.setMinWidth(128);
        TableColumn<PredictionPoint, String> knownTimeColumn = new TableColumn("Bekannte Zeit");
        knownTimeColumn.setMinWidth(128);
        TableColumn<PredictionPoint, String> predictionTimeColumn = new TableColumn("Bekannte Zeit");
        predictionTimeColumn.setMinWidth(128);
        TableColumn<PredictionPoint, Integer> priceColumn = new TableColumn("Preis");
        priceColumn.setMinWidth(128);

        table.getColumns().addAll(nrColumn, gasStationColumn, knownTimeColumn, predictionTimeColumn, priceColumn);

        for (int i = 0; i < predictionPoints.getLength(); i++) {
            PredictionPoint p = predictionPoints.get(i);
            System.out.println(p + "!!!!");
            nrColumn.setText(i + "");
            gasStationColumn.setText(p.getStation().getName());
            knownTimeColumn.setText(p.getTime() + "");
            predictionTimeColumn.setText(p.getTime() + "");
            priceColumn.setText(p.getPredictedPrice() + "");
            System.out.println("Hallo");
        }
        
        final ObservableList<PredictionPoint> list = FXCollections.observableArrayList();
        for(int i = 0; i < predictionPoints.getLength(); i++) {
       
        	list.add(predictionPoints.get(i)); 
        }
        nrColumn.setCellValueFactory(new PropertyValueFactory<PredictionPoint, Integer>("nrColumn"));
        gasStationColumn.setCellValueFactory(new PropertyValueFactory<PredictionPoint, String>("gasStationColumn"));
        knownTimeColumn.setCellValueFactory(new PropertyValueFactory<PredictionPoint, String>("knownTimeColumn"));
        predictionTimeColumn.setCellValueFactory(new PropertyValueFactory<PredictionPoint, String>("predictionTimeColumn"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<PredictionPoint, Integer>("priceColumn"));
        table.setItems(list);

        pane = new Pane();
        pane.getChildren().add(table);
        
        border.setCenter(pane);       
        return scene;
    }
}
