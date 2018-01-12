package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import model.PredictionPoint;
import model.PredictionPoints;

public class PredictionPointView {

    private Scene scene;
    private BorderPane border;
    private TableView<PredictionPoint.TableRow> table;
    private Label title;
    private BorderPane innerBorder;

    public PredictionPointView(Scene scene, BorderPane border) {

        this.scene = scene;
        this.border = border;
        this.innerBorder = new BorderPane();
        this.title = new Label();
        this.title.setFont(new Font("Arial", 20));
        this.title.setPadding(new Insets(5, 5, 5, 5));
        BorderPane.setAlignment(title, Pos.CENTER);
        createTable();
        innerBorder.setTop(title);
        innerBorder.setCenter(table);
    }
    public Scene buildPredictionPointView(PredictionPoints predictionPoints) {
     
        title.setText(predictionPoints.getName());
        table.setItems(predictionPoints.getList());

        border.setCenter(innerBorder);
        return scene;
    }

    private void createTable() {

        table = new TableView<>();
        TableColumn<PredictionPoint.TableRow, Integer> nrColumn = new TableColumn<>("Nr.");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nrColumn.setMinWidth(25);
        
        TableColumn<PredictionPoint.TableRow, String> gasStationColumn = new TableColumn<>("Tankstelle");
        gasStationColumn.setCellValueFactory(new PropertyValueFactory<>("station"));
        gasStationColumn.setMinWidth(300);
        
        TableColumn<PredictionPoint.TableRow, String> knownTimeColumn = new TableColumn<>("Bekannte Zeit");
        knownTimeColumn.setCellValueFactory(new PropertyValueFactory<>("knownTime"));
        knownTimeColumn.setMinWidth(145);
        
        TableColumn<PredictionPoint.TableRow, String> predictionTimeColumn = new TableColumn<>("Vorhersagezeitpunkt");
        predictionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("predictionTime"));
        predictionTimeColumn.setMinWidth(160);
        
        TableColumn<PredictionPoint.TableRow, Double> priceColumn = new TableColumn<>("Preis");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setMinWidth(50);
        
        TableColumn<PredictionPoint.TableRow, Double> realPriceColumn = new TableColumn<>("Echter Preis");
        realPriceColumn.setCellValueFactory(new PropertyValueFactory<>("realPrice"));
        realPriceColumn.setMinWidth(90);
        
        table.getColumns().add(nrColumn);
        table.getColumns().add(gasStationColumn);
        table.getColumns().add(knownTimeColumn);
        table.getColumns().add(predictionTimeColumn);
        table.getColumns().add(priceColumn);
        table.getColumns().add(realPriceColumn);
    }
}
