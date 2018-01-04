package view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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

	private Scene scene;
    private BorderPane border;
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
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<PredictionPoint.TableRow, String> gasStationColumn = new TableColumn<>("Tankstelle");
        gasStationColumn.setCellValueFactory(new PropertyValueFactory<>("station"));
        
        TableColumn<PredictionPoint.TableRow, String> knownTimeColumn = new TableColumn<>("Bekannte Zeit");
        knownTimeColumn.setCellValueFactory(new PropertyValueFactory<>("knownTime"));
        
        TableColumn<PredictionPoint.TableRow, String> predictionTimeColumn = new TableColumn<>("Vorhersagezeitpunkt");
        predictionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("predictionTime"));
        
        TableColumn<PredictionPoint.TableRow, Double> priceColumn = new TableColumn<>("Preis");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        TableColumn<PredictionPoint.TableRow, Double> realPriceColumn = new TableColumn<>("Echter Preis");
        realPriceColumn.setCellValueFactory(new PropertyValueFactory<>("realPrice"));

        table.getColumns().addAll(nrColumn, gasStationColumn, knownTimeColumn, predictionTimeColumn, priceColumn, realPriceColumn);

        ObservableList<PredictionPoint.TableRow> rows = predictionPoints.getList();
        table.setItems(rows);
//        table.setMinWidth(scene.getWidth());

        BorderPane titlePane = new BorderPane();
        titlePane.setCenter(label);
        
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        vbox.getChildren().addAll(titlePane, table);
        
        border.setCenter(vbox);
        return scene;
    }
}
