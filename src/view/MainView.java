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
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.PredictionPoints;
import model.Route;
/**
 *
 * @author Admin
 */
public class MainView extends BorderPane {

    private MenuBar bar;
    private GasStationController gsc;
    private Stage mainStage;
    
    private Menu validateMenu;
        
    public MainView(Stage primaryStage,GasStationController gsc) {
        super();
        mainStage = primaryStage;
        this.gsc = gsc;
        //Create the foundation: borderPane -> Scrollpane -> Canvas
        mainStage.setTitle("IntelliTank");
        Scene scene = new Scene(this, 800, 600);
        //Methods to fill each part with content
        displayMenubar();
        Image icon = new Image("/img/gas-station.png");
        mainStage.getIcons().add(icon);
        mainStage.setScene(scene);
        Label l = new Label("Klicken Sie auf einen der oberen Reiter, \num sich eine Route oder Vorhersagepunkte anzeigen zu lassen.");
        l.setTextAlignment(TextAlignment.CENTER);
        setCenter(l);
        mainStage.show();
    }
       
    public void displayRoute(Route route) {
        setCenter(new RouteView(mainStage, route));
        mainStage.show();
        validateMenu.setVisible(true);
    }
    
    public void displayPredictionPoints(PredictionPoints predictionPoints) {
        setCenter(new PredictionPointView(mainStage, predictionPoints));
        mainStage.show();
        validateMenu.setVisible(true);
    }
       
    //displays menu bar on the top
    private void displayMenubar() {
        bar = new MenuBar();
        setTop(bar);
        setUpRouteTab();
    	setUpPredictionPointTab();
        setUpValidateButton();
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
                                CSVManager.copyRoute(selectedFile);
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
                                CSVManager.copyPredictionPoints(selectedFile);
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
        setCenter(null);
        this.mainStage.hide();
    }
    public void show() {
        this.mainStage.show();
    }

    private void setUpAboutTab() {
        Menu about = new Menu("\u00dcber");
    	MenuItem itemUeber = new MenuItem("Mitwirkende");
    	about.getItems().addAll(itemUeber);
    	itemUeber.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
    		public void handle(ActionEvent arg0) {
    			PopupBox.displayMessage(101);
    		}
		});
        bar.getMenus().add(about);
    }

    private void setUpValidateButton() {
    	Label menuLabel = new Label("Validieren");
    	menuLabel.setOnMouseClicked(new EventHandler<Event>() {
                    @Override
                    public void handle(Event e) {
                        //PopupBox.displayMessage(102);
                        if(getCenter() instanceof RouteView) {
                        	Route r = gsc.getRoute();
                        	PopupBox.displayValidation(r);
                        } else if(getCenter() instanceof PredictionPointView) {
                        	PredictionPoints p = gsc.getPredictionPoints();
                        	PopupBox.displayValidation(p);
                        } else {
                        	System.out.println("Nothing loaded");
                        }
                    }
                });
    	
        validateMenu = new Menu();
        validateMenu.setVisible(false);
        validateMenu.setGraphic(menuLabel);
        bar.getMenus().add(validateMenu);
    }
}
