package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import model.IPredictionStation;
import model.IPredictionStations;

public class ValidationContextMenu extends ContextMenu {
	private String itemText = "Zeige Validierung";
	
	public ValidationContextMenu(IPredictionStation station) {
		super();
        MenuItem predictionItem = new MenuItem(itemText);
        predictionItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	PopupBox.displayValidation(station);
            }
        });
 
        getItems().addAll(predictionItem);
	}
	
	public ValidationContextMenu(IPredictionStations stations) {
		super();
        MenuItem predictionItem = new MenuItem(itemText);
        predictionItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	PopupBox.displayValidation(stations);
            }
        });
 
        getItems().addAll(predictionItem);
	}
}
