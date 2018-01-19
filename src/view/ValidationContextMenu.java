package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import model.IPredictionStation;

/**
 * Kontextmenu, über das Validierungen einzelner IPredictionStations (Tankstops bzw. Vorhersagezeitpunkte)
 * angezeigt werden können.
 *
 * @author Sebastian Drath
 *
 */
public class ValidationContextMenu extends ContextMenu {
	private String itemText = "Zeige Validierung";
	
	/**
	 * Erstellt das Kontextmenü für eine IPredictionStation.
	 *
	 * @param station das zu validierende Objekt
	 */
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
}
