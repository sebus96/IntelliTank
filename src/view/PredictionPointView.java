package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.PredictionPoint;
import model.PredictionPoints;

public class PredictionPointView extends BorderPane {

    private Scene scene;
    private BorderPane border;
    private TableView<PredictionPoint.TableRow> table;
    private Label title;
    private BorderPane innerBorder;

    public PredictionPointView(MainView mainView, PredictionPoints predictionPoints) {

        this.scene = mainView.getScene();
        this.border = mainView.getBorder();
        this.title = new Label();
        this.title.setFont(new Font("Arial", 20));
        this.title.setPadding(new Insets(5, 5, 5, 5));
        BorderPane.setAlignment(title, Pos.CENTER);
        createTable();
        this.setTop(title);
        this.setCenter(table);
        title.setText(predictionPoints.getName());
        table.setItems(predictionPoints.getList());
        
        border.setCenter(innerBorder);
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
        priceColumn.setCellFactory(rf -> {
        	return new TableCell<PredictionPoint.TableRow, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                    	if(item >= 0) {
                    		setText(item.toString());
                    		setTextFill(Color.BLACK);
                            setStyle("");
                    		this.setTooltip(null);
                    	} else {
                    		setText(" -.--- ");
                    		setTextFill(Color.RED);
//                    		setStyle("-fx-background-color: red");
                    		this.setTooltip(new Tooltip("Es sind keine Daten als Basis für eine Vorhersage vorhanden"));
                    	}
                    }
                }
            };
        });
        priceColumn.setMinWidth(50);
        
        TableColumn<PredictionPoint.TableRow, Double> realPriceColumn = new TableColumn<>("Echter Preis");
        realPriceColumn.setCellValueFactory(new PropertyValueFactory<>("realPrice"));
        realPriceColumn.setMinWidth(90);
        realPriceColumn.setCellFactory(rf -> {
        	return new TableCell<PredictionPoint.TableRow, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                    	if(item >= 0) {
                    		setText(item.toString());
                    		setTextFill(Color.BLACK);
                    		this.setTooltip(null);
                    	} else {
                    		setText(" -.--- ");
                    		setTextFill(Color.RED);
                    		this.setTooltip(new Tooltip("Es sind keine historischen Preise vorhanden"));
                    	}
                    }
                }
            };
        });
        
        table.setRowFactory(tv -> {
        	TableRow<PredictionPoint.TableRow> row = new TableRow<>();
        	// MouseListener für das öffnen des Preisdiagramms aus der Vorhersagezeitpunkttabelle
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty()) {
                	PredictionPoint.TableRow rowData = row.getItem();
                	PriceDiagram.displayGasStation(rowData.getPredictionPoint());
                }
            });
            return row ;
        });
        
        table.getColumns().add(nrColumn);
        table.getColumns().add(gasStationColumn);
        table.getColumns().add(knownTimeColumn);
        table.getColumns().add(predictionTimeColumn);
        table.getColumns().add(priceColumn);
        table.getColumns().add(realPriceColumn);
    }
}
