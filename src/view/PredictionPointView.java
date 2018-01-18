package view;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.PredictionPoint;
import model.PredictionPointList;

/**
 * Erzeugt Graphen für die Preise 
 * @author Axel Claassen, Burak Kadioglu, Sebastian Drath
 *
 */
public class PredictionPointView extends BorderPane {

    private static TableView<PredictionPoint.TableRow> table;
    private Label title;
	private Stage parent;

	/**
	 * Konstruktor für die Anzeige der Vorhersagezeitpunkte.
	 * @param parent Hauptfenster
	 * @param predictionPoints Vorhersagezeitpunkte
	 */
    public PredictionPointView(Stage parent, PredictionPointList predictionPoints) {
    	super();
        this.parent = parent;
        this.title = new Label();
        this.title.setFont(new Font("Arial", 20));
        this.title.setPadding(new Insets(5, 5, 5, 5));
        BorderPane.setAlignment(title, Pos.CENTER);
        createTable();
        this.setTop(title);
        this.setCenter(table);
        title.setText(predictionPoints.getName());
        table.setItems(predictionPoints.getList());
    }
    
    /**
     * Kreierung der Tabelle.
     * Tooltip-Funktion vorhanden.
     */
    private void createTable() {
    	if(table != null) return;
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
            row.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (! row.isEmpty()) {
	                	PredictionPoint.TableRow rowData = row.getItem();
	                	if(event.getButton() == MouseButton.PRIMARY)
	                		PriceDiagram.displayGasStation(rowData.getPredictionPoint());
	                	else if(event.getButton() == MouseButton.SECONDARY)
	                		new ValidationContextMenu(rowData.getPredictionPoint()).show(parent, event.getScreenX(), event.getScreenY());
	                }
				}
                
            });
            return row;
        });
        table.getColumns().add(nrColumn);
        table.getColumns().add(gasStationColumn);
        table.getColumns().add(knownTimeColumn);
        table.getColumns().add(predictionTimeColumn);
        table.getColumns().add(priceColumn);
        table.getColumns().add(realPriceColumn);
    }
}
