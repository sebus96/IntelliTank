package view;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import model.PredictionPoint;
import model.PredictionPoints;

public class PredictionPointView {

	private Scene scene;
    private BorderPane border;
    private Pane pane;
    private TableView<PredictionPoint.TableRow> table;

    public PredictionPointView(Scene scene, BorderPane border) {
        table = new TableView<>();
        this.scene = scene;
        this.border = border;
    }
    public Scene buildPredictionPointView(PredictionPoints predictionPoints) {
     
        final Label label = new Label(predictionPoints.getName());
        label.setFont(new Font("Arial", 20));

        TableColumn<PredictionPoint.TableRow, Integer> nrColumn = new TableColumn<>("Nr.");
//        nrColumn.setMinWidth(32);
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<PredictionPoint.TableRow, String> gasStationColumn = new TableColumn<>("Tankstelle");
//        gasStationColumn.setMinWidth(128);
        gasStationColumn.setCellValueFactory(new PropertyValueFactory<>("station"));
        TableColumn<PredictionPoint.TableRow, String> knownTimeColumn = new TableColumn<>("Bekannte Zeit");
//        knownTimeColumn.setMinWidth(128);
        knownTimeColumn.setCellValueFactory(new PropertyValueFactory<>("knownTime"));
        TableColumn<PredictionPoint.TableRow, String> predictionTimeColumn = new TableColumn<>("Bekannte Zeit");
//        predictionTimeColumn.setMinWidth(128);
        predictionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("predictionTime"));
        TableColumn<PredictionPoint.TableRow, Double> priceColumn = new TableColumn<>("Preis");
//        priceColumn.setMinWidth(128);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(nrColumn, gasStationColumn, knownTimeColumn, predictionTimeColumn, priceColumn);

        ObservableList<PredictionPoint.TableRow> rows = predictionPoints.getList();
        table.setItems(rows);
        table.setMinWidth(640);

        pane = new Pane();
        pane.getChildren().add(table);
        
        border.setCenter(pane);       
        return scene;
    }
}
