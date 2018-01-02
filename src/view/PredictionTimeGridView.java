package view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.PredictionPoint;
import model.PredictionPoints;

public class PredictionTimeGridView {

	Scene scene;
	BorderPane border;
    private TableView table = new TableView();

	
	public void generateGridForPredictionTime(Stage stage, PredictionPoints predictionPoints) {
//		border = new BorderPane();
//		scene = new Scene(border, 640, 600);
//		stage.setScene(scene);
//		TableView<PredictionPoint> table;
//		
//		for(int i = 0; i < predictionPoints.getLength(); i++) {
//			PredictionPoint p = predictionPoints.get(i);
//			//NrColumn
//			TableColumn<PredictionPoint, Integer> nrColumn = new TableColumn<>("Nr.");
//			nrColumn.setMinWidth(128);
//			nrColumn.setText(i + "");
//	
//			//GasstationColumn
//			TableColumn<PredictionPoint, String> gasStationColumn = new TableColumn<>("Tankstelle");
//			gasStationColumn.setMinWidth(128);
//			gasStationColumn.setText(p.getStation().getName());
//			
//			//KnownTime
//			TableColumn<PredictionPoint, String> knownTime = new TableColumn<>("Bekannte Zeit");
//			knownTime.setMinWidth(128);
//			knownTime.setText(p.getTime() + "");
//		
//			//PredictionTime
//			TableColumn<PredictionPoint, String> predictionTime = new TableColumn<>("Vorhersagezeit");
//			predictionTime.setMinWidth(128);
//			predictionTime.setText(p.getTime() + "");
//		
//			//Price
//			TableColumn<PredictionPoint, String> price = new TableColumn<>("Preis");
//			price.setMinWidth(128);
//			price.setText(p.getPredictedPrice() + "");
//		}
//		
//		table = new TableView<>();
////		table.setItems(value);
//		border.setCenter(table);
 
		        Scene scene = new Scene(new Group());
		        stage.setTitle("Table View Sample");
		        stage.setWidth(640);
		        stage.setHeight(600);
		 
		        final Label label = new Label("Tabelle");
		        label.setFont(new Font("Arial", 20));
		 		 
		        TableColumn nrColumn = new TableColumn("Nr.");
		        nrColumn.setMinWidth(128);
		        TableColumn gasStationColumn = new TableColumn("Tankstelle");
		        gasStationColumn.setMinWidth(128);
		        TableColumn knownTimeColumn = new TableColumn("Bekannte Zeit");
		        knownTimeColumn.setMinWidth(128);
		        TableColumn predictionTimeColumn = new TableColumn("Bekannte Zeit");
		        predictionTimeColumn.setMinWidth(128);
		        TableColumn priceColumn = new TableColumn("Preis");
		        priceColumn.setMinWidth(128);
						        
		        table.getColumns().addAll(nrColumn, gasStationColumn, knownTimeColumn, predictionTimeColumn, priceColumn);
		        
		        for(int i = 0; i < predictionPoints.getLength(); i++) {
		        	PredictionPoint p = predictionPoints.get(i);
		        	nrColumn.setText(i + "");
					gasStationColumn.setText(p.getStation().getName());
					knownTimeColumn.setText(p.getTime() + "");
					predictionTimeColumn.setText(p.getTime() + "");
					priceColumn.setText(p.getPredictedPrice() + "");
		        }
		 
		        final VBox vbox = new VBox();
		        vbox.setSpacing(5);
		        vbox.setPadding(new Insets(10, 0, 0, 10));
		        vbox.getChildren().addAll(label, table);
		 
		        ((Group) scene.getRoot()).getChildren().addAll(vbox);
		 
		        stage.setScene(scene);
		        stage.show();
		    
	}	
}
