package main;

import controller.GasStationController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 
 * @author Axel Claassen, Sebastian Drath
 */
public class Main extends Application {

    /**
     * Start-Methode
     * @param args Beim Start mitgegebene Parameter
     */
	public static void main(String[] args) {
		launch(args);
	}

        /**
         * Initialisiert Gasstationcontroller
         * @param primaryStage Hauptfenster, auf dem Inhalt angezeigt werden soll
         * @throws Exception Startfehler
         */
	@Override
	public void start(Stage primaryStage) throws Exception {

		new GasStationController(primaryStage);
	}

}
