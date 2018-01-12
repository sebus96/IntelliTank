package view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.PredictionPoint;
import model.PredictionPoints;

public class PredictionPointView {

    private Scene scene;
    private BorderPane border;
    private TableView<PredictionPoint.TableRow> table;
    private Label title;
    private BorderPane innerBorder;

    public PredictionPointView(Scene scene, BorderPane border) {
        table = new TableView<>();
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
/*
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        vbox.getChildren().addAll(title, table);
        */
        border.setCenter(innerBorder);
        return scene;
    }

    private void createTable() {

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
        
        table.getColumns().addAll(nrColumn, gasStationColumn, knownTimeColumn, predictionTimeColumn, priceColumn, realPriceColumn);
    }
}
