package view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.GasStationController;
import io.CSVManager;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.PredictionPoints;
import model.Route;
/**
 *
 * @author Admin
 */
public class MainView {

        Scene scene;
	BorderPane border;
        MenuBar bar;
        RouteView rv;
        PredictionPointView ppv;
        GasStationController gsc;
        Stage mainStage;
        
    public MainView(Stage primaryStage,GasStationController gsc) {
        
        mainStage = primaryStage;
        this.gsc = gsc;
        //Create the foundation: borderPane -> Scrollpane -> Canvas
        primaryStage.setTitle("IntelliTank");
        border = new BorderPane();
        scene = new Scene(border, 640, 600);
        mainStage.setResizable(false);
        //Methods to fill each part with content
        displayMenubar(border);
        Image icon = new Image("/img/gas-station.png");
        mainStage.getIcons().add(icon);
        rv = new RouteView(scene,border,this,gsc,bar.getHeight());
        ppv = new PredictionPointView(scene,border);
    }
       
    public void displayRoute(Route route) {

        mainStage.setScene(rv.buildRouteView(route));
        mainStage.show();
    }
    
    public void displayPredictionPoints(PredictionPoints predictionPoints) {
    	// TODO show predictionpoint window
        mainStage.setScene(ppv.buildPredictionPointView(predictionPoints));
        mainStage.show();
    }
       
    //displays menu bar on the top
    private void displayMenubar(BorderPane border) {    
        bar = new MenuBar();
        border.setTop(bar);
        setUpRouteTab();
    	setUpPredictionPointTab();
    	setUpAboutTab();
    }
    
    private void setUpRouteTab() {
        
        Menu routes = new Menu("Routen");
        //Wenn der "Route"-Reiter gedrueckt wird, aktualisiere die Liste der Routen in dem Ordner (evtl setOnShowing?)
        routes.setOnShowing(new EventHandler<Event>() {
                    @Override
                    public void handle(Event e) {
                        //Entferne alles ausser die ersten 3(Import und 2 seperator) und füge danach alle aus dem Ordner hinzu
                        routes.getItems().remove(3,routes.getItems().size());
                        for(String s : CSVManager.readRouteNames()) {
                            MenuItem mi = new MenuItem(s.substring(0, s.length()-4));
                            mi.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(final ActionEvent e) {
                                    gsc.switchToRoute(mi.getText());
                                }
                            });
                            routes.getItems().add(mi);
                        }
                    }});
    	
        MenuItem itemImportRoute = new MenuItem("Importieren");
    	itemImportRoute.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        FileChooser fc = new FileChooser();
                        File workingDirectory = new File(System.getProperty("user.dir"));
                        fc.setInitialDirectory(workingDirectory);
                        fc.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("CSV-Dateien", "*.csv"));
                        File selectedFile = fc.showOpenDialog(null);
                        if (selectedFile != null) {
                            try {
                                CSVManager.copyRouteFile(selectedFile);
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                        	System.out.println("Keine Datei ausgewaehlt.");
                        }
                    }
                });
        routes.getItems().addAll(itemImportRoute,new SeparatorMenuItem(),new SeparatorMenuItem());
        bar.getMenus().add(routes);
    }

    private void setUpPredictionPointTab() {
        Menu predictionPoints = new Menu("Vorhersagezeitpunkte");
        //Wenn der "Vorhersagezeitpunkt"-Reiter gedrueckt wird, aktualisiere die Liste der Routen in dem Ordner (evtl setOnShowing?)
        predictionPoints.setOnShowing(new EventHandler<Event>() {
                    @Override
                    public void handle(Event e) {
                        //Entferne alles ausser die ersten 3(Import und 2 seperator) und füge danach alle aus dem Ordner hinzu
                        predictionPoints.getItems().remove(3,predictionPoints.getItems().size());
                        for(String s : CSVManager.readPredictionPointNames()) {
                            MenuItem mi = new MenuItem(s.substring(0, s.length()-4));
                            mi.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(final ActionEvent e) {
                                    gsc.switchToPredictionPoints(mi.getText());
                                }
                            });
                            predictionPoints.getItems().add(mi);
                        }
                    }});
    	
        MenuItem itemImportPredictionPoint = new MenuItem("Importieren");
    	itemImportPredictionPoint.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        FileChooser fc = new FileChooser();
                        File workingDirectory = new File(System.getProperty("user.dir"));
                        fc.setInitialDirectory(workingDirectory);
                        fc.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("CSV-Dateien", "*.csv"));
                        File selectedFile = fc.showOpenDialog(null);
                        if (selectedFile != null) {
                            try {
                                CSVManager.copyPredictionPointFile(selectedFile);
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                        	System.out.println("Keine Datei ausgewaehlt.");
                        }
                    }
                });
        predictionPoints.getItems().addAll(itemImportPredictionPoint,new SeparatorMenuItem(),new SeparatorMenuItem());
        bar.getMenus().add(predictionPoints);
    }
    
    public void hide() {
        border.setCenter(null);
        this.mainStage.hide();
    }

    private void setUpAboutTab() {
        Menu about = new Menu("Ueber");
    	MenuItem itemUeber = new MenuItem("Mitwirkende");
    	about.getItems().addAll(itemUeber);
    	itemUeber.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
    		public void handle(ActionEvent arg0) {
    			Alert alert = new Alert(AlertType.INFORMATION);
    			alert.setTitle("Mitwirkende");
    			alert.setHeaderText(null);
    			alert.setContentText("Dieses Projekt wurde von Sebastian Drath, Sezer Dursun, Axel Claassen und Burak Kadioglu entwickelt.");
    			alert.showAndWait();
    		}
		});
        bar.getMenus().add(about);
    }
}
